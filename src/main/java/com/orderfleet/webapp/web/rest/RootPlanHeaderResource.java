package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.RootPlanDetailService;
import com.orderfleet.webapp.service.RootPlanHeaderService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.RootPlanDetailDTO;
import com.orderfleet.webapp.web.rest.dto.RootPlanHeaderAndDetailDTO;
import com.orderfleet.webapp.web.rest.dto.RootPlanHeaderDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class RootPlanHeaderResource {

	private final Logger log = LoggerFactory.getLogger(RootPlanHeaderResource.class);

	@Inject
	private RootPlanHeaderService rootPlanHeaderService;

	@Inject
	private TaskListService taskListService;

	@Inject
	private RootPlanDetailService rootPlanDetailService;

	@Inject
	private UserService userService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	/**
	 * GET /root-plan-headers : get all the root Plan Headers.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of root Plans in
	 *         body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/root-plan-headers", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllRootPlanHeaders(Model model) throws URISyntaxException {
		log.debug("Web request to get page of root plan");

		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		log.info("No: subordinate users :" + userIds);
//		if(userIds.isEmpty()) {
//			model.addAttribute("users", userService.findAllByCompany());
//		} else {
//			model.addAttribute("users", userService.findByUserIdIn(userIds));
//		}

		if (userIds.isEmpty()) {
			List<EmployeeProfile> employeeProfiles = employeeProfileRepository
					.findAllByCompanyAndDeativatedEmployeeProfile(true);

			userIds = employeeProfiles.stream().map(emp -> emp.getUser() != null ? emp.getUser().getId() : 0)
					.collect(Collectors.toList());
		}
		List<UserDTO> userDtos = userService.findByUserIdIn(userIds);

		userDtos.sort(
				(UserDTO s1, UserDTO s2) -> s1.getFirstName().toLowerCase().compareTo(s2.getFirstName().toLowerCase()));

		model.addAttribute("users", userDtos);

		model.addAttribute("taskLists", taskListService.findAllByCompany());
		return "company/rootPlanHeader";
	}

	/**
	 * POST /root-plan-headers : save RootPlanHeaderDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK)
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/root-plan-headers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RootPlanHeaderDTO> saveRootPlanHeader(
			@Valid @RequestBody RootPlanHeaderDTO rootPlanHeaderDTO) {
		log.debug("request to save RootPlanHeader ");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate fromDateTime = LocalDate.parse(rootPlanHeaderDTO.getFromDateString(), formatter);
		LocalDate toDateTime = LocalDate.parse(rootPlanHeaderDTO.getToDateString(), formatter);
		rootPlanHeaderDTO.setFromDate(fromDateTime);
		rootPlanHeaderDTO.setToDate(toDateTime);
		// change
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = rootPlanHeaderService
				.findAllByUserPid(rootPlanHeaderDTO.getUserPid());
		if (!rootPlanHeaderDTOs.isEmpty()) {
			for (RootPlanHeaderDTO rootPlanHeaderDTO1 : rootPlanHeaderDTOs) {
				if (rootPlanHeaderDTO.getUserPid().equals(rootPlanHeaderDTO1.getUserPid())) {
					if ((fromDateTime.compareTo(rootPlanHeaderDTO1.getFromDate()) >= 0
							&& fromDateTime.compareTo(rootPlanHeaderDTO1.getToDate()) <= 0)
							|| (toDateTime.compareTo(rootPlanHeaderDTO1.getFromDate()) >= 0
									&& toDateTime.compareTo(rootPlanHeaderDTO1.getToDate()) <= 0)) {
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createFailureAlert("rootPlanHeader",
										"rootPlanHeader exists within Dates ",
										"A new Root Plan Header not range between same dates"))
								.body(null);
					}
				}
			}
		}
		if (rootPlanHeaderDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("rootPlanHeader", "idexists",
					"A new Root Plan Header cannot already have an ID")).body(null);
		}
		if (rootPlanHeaderService.findByName(rootPlanHeaderDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("rootPlanHeader", "nameexists", "Root Plan Header already in use"))
					.body(null);
		}
		rootPlanHeaderService.save(rootPlanHeaderDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /root-plan-headers/{id}: get all the RootPlanHeaderDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         RootPlanHeaderDTO list in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/root-plan-headers/{id}", method = RequestMethod.GET)
	public ResponseEntity<RootPlanHeaderDTO> getRootPlanHeaderById(@PathVariable String id) {
		log.debug("Web request to get RootPlanHeaderDTO by pid : {}", id);
		return rootPlanHeaderService.findOneByPid(id)
				.map(rootPlanHeaderDTO -> new ResponseEntity<>(rootPlanHeaderDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * PUT /root-plan-headers : update RootPlanHeaderDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK)
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/root-plan-headers", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RootPlanHeaderDTO> updateRootPlanHeader(
			@Valid @RequestBody RootPlanHeaderDTO rootPlanHeaderDTO) {
		log.debug("request to update RootPlanHeaderDTO ");
		log.debug("REST request to update RootPlanHeader : {}", rootPlanHeaderDTO);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate fromDateTime = LocalDate.parse(rootPlanHeaderDTO.getFromDateString(), formatter);
		LocalDate toDateTime = LocalDate.parse(rootPlanHeaderDTO.getToDateString(), formatter);
		rootPlanHeaderDTO.setFromDate(fromDateTime);
		rootPlanHeaderDTO.setToDate(toDateTime);
		if (rootPlanHeaderDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("rootPlanHeader", "idNotexists", "RootPlanHeader must have an ID"))
					.body(null);
		}
		// change
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = rootPlanHeaderService
				.findAllByUserPid(rootPlanHeaderDTO.getUserPid());
		List<RootPlanHeaderDTO> newRootPlanHeaderDTOs = new ArrayList<RootPlanHeaderDTO>();
		for (RootPlanHeaderDTO rootPlanHeaderDTO2 : rootPlanHeaderDTOs) {
			if (!rootPlanHeaderDTO.getPid().equals(rootPlanHeaderDTO2.getPid())) {
				newRootPlanHeaderDTOs.add(rootPlanHeaderDTO2);
			}
		}

		if (!newRootPlanHeaderDTOs.isEmpty()) {
			for (RootPlanHeaderDTO rootPlanHeaderDTO1 : newRootPlanHeaderDTOs) {
				if (rootPlanHeaderDTO.getUserPid().equals(rootPlanHeaderDTO1.getUserPid())) {
					if ((fromDateTime.compareTo(rootPlanHeaderDTO1.getFromDate()) >= 0
							&& fromDateTime.compareTo(rootPlanHeaderDTO1.getToDate()) <= 0)
							|| (toDateTime.compareTo(rootPlanHeaderDTO1.getFromDate()) >= 0
									&& toDateTime.compareTo(rootPlanHeaderDTO1.getToDate()) <= 0)) {
						return ResponseEntity.badRequest()
								.headers(HeaderUtil.createFailureAlert("rootPlanHeader",
										"rootPlanHeader exists within Dates ",
										"A new Root Plan Header not range between same dates"))
								.body(null);
					}
				}
			}
		}
		Optional<RootPlanHeaderDTO> existingRootPlanHeader = rootPlanHeaderService
				.findByName(rootPlanHeaderDTO.getName());
		if (existingRootPlanHeader.isPresent()
				&& (!existingRootPlanHeader.get().getPid().equals(rootPlanHeaderDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("rootPlanHeader", "nameexists", "RootPlanHeader already in use"))
					.body(null);
		}
		rootPlanHeaderService.update(rootPlanHeaderDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * DELETE /root-plan-headers/:id : delete the "id" RootPlanHeader.
	 *
	 * @param id the id of the rootPlanHeaderDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/root-plan-headers/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteRootPlanHeader(@PathVariable String pid) {
		log.debug("REST request to delete RootPlanHeader : {}", pid);
		rootPlanHeaderService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("rootPlanHeader", pid.toString()))
				.build();
	}

	/**
	 * GET /root-plan-headers/loadTaskList/{id}: get all the
	 * RootPlanHeaderTaskListDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         RootPlanHeaderTaskListDTO list in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/root-plan-headers/loadTaskList/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<RootPlanDetailDTO>> getAssignTaskListById(@PathVariable String id) {
		log.debug("Web request to get RootPlanHeaderTaskListDTO by pid : {}", id);
		List<RootPlanDetailDTO> rootPlanDetailDTOs = rootPlanDetailService.findAllByRootPlanHeaderPid(id);
		return new ResponseEntity<>(rootPlanDetailDTOs, HttpStatus.OK);
	}

	/**
	 * POST /root-plan-headers/assignTasklist : save RootPlanDetailDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK)
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP details
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/root-plan-headers/assignTasklist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RootPlanDetailDTO> saveRootPlanDetail(
			@Valid @RequestBody List<RootPlanDetailDTO> rootPlanDetailDTOs) {

		log.debug("request to save RootPlanDetail ");

		rootPlanDetailService.save(rootPlanDetailDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /root-plan-headers/changeStatus: activate the RootPlanHeader.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         RootPlanHeaderTaskListDTO list in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/root-plan-headers/changeStatus", method = RequestMethod.GET)
	public ResponseEntity<Boolean> changeActivateStatus(@RequestParam String userPid,
			@RequestParam String rootPlanHeaderPid, @RequestParam boolean activated) {
		log.debug("Web request to activate RootPlanHeaderTaskListDTO by pid : {}", rootPlanHeaderPid);
		boolean b = false;
		int i = 0;
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = rootPlanHeaderService.findAllByUserPid(userPid);
		if (activated) {
			for (RootPlanHeaderDTO rootPlanHeaderDTO : rootPlanHeaderDTOs) {
				if (rootPlanHeaderDTO.getActivated()) {
					b = true;
					break;
				}
			}
		} else {
			Optional<RootPlanHeaderDTO> rootPlanHeaderDTO = rootPlanHeaderService.findOneByPid(rootPlanHeaderPid);
			if (rootPlanHeaderDTO.isPresent()) {
				rootPlanHeaderDTO.get().setActivated(activated);
				rootPlanHeaderService.update(rootPlanHeaderDTO.get());
				i = 1;
			}
		}
		if (!b && i == 0) {
			Optional<RootPlanHeaderDTO> rootPlanHeaderDTO = rootPlanHeaderService.findOneByPid(rootPlanHeaderPid);
			if (rootPlanHeaderDTO.isPresent()) {
				rootPlanHeaderDTO.get().setActivated(activated);
				rootPlanHeaderService.update(rootPlanHeaderDTO.get());
			}
		}
		return new ResponseEntity<Boolean>(b, HttpStatus.OK);
	}

	/**
	 * POST /root-plan-headers/copyTasklist : save RootPlanHeaderDTO and
	 * RootPlanDetailDTO.
	 *
	 * @return the ResponseEntity with status 200 (OK)
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP details
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/root-plan-headers/copyTasklist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RootPlanHeaderAndDetailDTO> copyRootPlanDetail(
			@Valid @RequestBody RootPlanHeaderAndDetailDTO rootPlanHeaderAndDetailDTO) {

		log.debug("request to save RootPlanHeaderDTO and RootPlanDetailDTO ");

		// save Root Plan Header
		RootPlanHeaderDTO rootPlanHeaderDTO = rootPlanHeaderAndDetailDTO.getRootPlanHeaderDTO();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate fromDateTime = LocalDate.parse(rootPlanHeaderDTO.getFromDateString(), formatter);
		LocalDate toDateTime = LocalDate.parse(rootPlanHeaderDTO.getToDateString(), formatter);
		rootPlanHeaderDTO.setFromDate(fromDateTime);
		rootPlanHeaderDTO.setToDate(toDateTime);
		rootPlanHeaderDTO = rootPlanHeaderService.save(rootPlanHeaderDTO);

		// save Root Plan Detail
		for (RootPlanDetailDTO rootPlanDetailDTO : rootPlanHeaderAndDetailDTO.getRootPlanDetailDTOs()) {
			rootPlanDetailDTO.setRootPlanHeaderPid(rootPlanHeaderDTO.getPid());
		}
		rootPlanDetailService.save(rootPlanHeaderAndDetailDTO.getRootPlanDetailDTOs());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/root-plan-headers/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<RootPlanHeaderDTO>> filterExecutiveTaskExecutions(
			@RequestParam("userPid") String userPid, @RequestParam("active") String active) {
		log.debug("Web request to filter RootPlanHeader");
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		rootPlanHeaderDTOs = getFilterData(userPid, active);
		rootPlanHeaderDTOs.sort((RootPlanHeaderDTO s1, RootPlanHeaderDTO s2) -> s1.getUserName().toLowerCase()
				.compareTo(s2.getUserName().toLowerCase()));

		return new ResponseEntity<List<RootPlanHeaderDTO>>(rootPlanHeaderDTOs, HttpStatus.OK);

	}

	private List<RootPlanHeaderDTO> getFilterData(String userPid, String active) {
		List<RootPlanHeaderDTO> result = new ArrayList<>();
		if (userPid.equals("no")) {
//			if (active.equals("Active")) {
//				result = rootPlanHeaderService.findAllByActivated(true);
//			} else if (active.equals("Deactive")) {
//				result = rootPlanHeaderService.findAllByActivated(false);
//			} else {
//				result = rootPlanHeaderService.findAllByCompany();
//			}

			List<Long> userIds = new ArrayList<>();

			List<EmployeeProfile> employeeProfiles = employeeProfileRepository
					.findAllByCompanyAndDeativatedEmployeeProfile(true);

			userIds = employeeProfiles.stream().map(emp -> emp.getUser() != null ? emp.getUser().getId() : 0)
					.collect(Collectors.toList());

			if (active.equals("Active")) {
				result = rootPlanHeaderService.findAllByUserIdsInAndActivated(userIds, true);
			} else if (active.equals("Deactive")) {
				result = rootPlanHeaderService.findAllByUserIdsInAndActivated(userIds, false);
			} else {
				result = rootPlanHeaderService.findAllByUserIdsInAndCompany(userIds);
			}
		} else {
			if (active.equals("Active")) {
				result = rootPlanHeaderService.findAllByUserPidAndActivated(userPid, true);
			} else if (active.equals("Deactive")) {
				result = rootPlanHeaderService.findAllByUserPidAndActivated(userPid, false);
			} else {
				result = rootPlanHeaderService.findAllByUserPid(userPid);
			}
		}
		return result;

	}
}