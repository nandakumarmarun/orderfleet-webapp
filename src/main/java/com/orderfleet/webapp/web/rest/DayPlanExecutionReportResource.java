package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;

/**
 * Web controller for managing DayPlanExecutionConfig.
 * 
 * @author Shaheer
 * @since May 05, 2017
 */
@Controller
@RequestMapping("/web")
public class DayPlanExecutionReportResource {

	private final Logger log = LoggerFactory.getLogger(DayPlanExecutionReportResource.class);

	private final ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	private final ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	private final EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserService userService;

	@Inject
	private GeoLocationService geoLocationService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	public DayPlanExecutionReportResource(ExecutiveTaskPlanRepository executiveTaskPlanRepository,
			ExecutiveTaskExecutionRepository executiveTaskExecutionRepository,
			EmployeeHierarchyService employeeHierarchyService) {
		super();
		this.executiveTaskPlanRepository = executiveTaskPlanRepository;
		this.executiveTaskExecutionRepository = executiveTaskExecutionRepository;
		this.employeeHierarchyService = employeeHierarchyService;
	}

	/**
	 * GET /day-plan-execution-report : get all the DayPlanExecutionReport.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/day-plan-execution-report", method = RequestMethod.GET)
	public String getAllDayPlanExecutionReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Day Plan Execution Report");
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/dayPlanExecutionReport";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/day-plan-execution-report/load", method = RequestMethod.GET)
	public ResponseEntity<List<ExecutiveTaskPlanDTO>> getExecutiveTaskPlan(
			@RequestParam("employeePid") String employeePid, @RequestParam String date) {
		log.debug("Web request to get Executive Task Plan");

		List<String> userPids = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
			if (employeeProfileDTO.getPid() != null) {
				userPids.add(employeeProfileDTO.getUserPid());
			}
		} else {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.size() > 0) {
				List<UserDTO> users = userService.findByUserIdIn(userIds);
				for (UserDTO user : users) {
					userPids.add(user.getPid());
				}
			}
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate dateTime = LocalDate.parse(date, formatter);

		List<ExecutiveTaskPlan> actualEcutiveTaskPlans = executiveTaskPlanRepository
				.findByUserPidInAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPids, dateTime.atTime(0, 0),
						dateTime.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
		
		List<ExecutiveTaskPlanDTO> result = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		List<ExecutiveTaskPlanDTO> executiveTaskPlanDTO = new ArrayList<>();
		if (!actualEcutiveTaskPlans.isEmpty()) {
			List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
					.findExecutiveTaskPlanByExecutiveTaskPlanIn(actualEcutiveTaskPlans);

			List<ExecutiveTaskPlan> convertedTaskPlans = actualEcutiveTaskPlans.stream().map(etp -> {
				int index = savedExecutedPlan.indexOf(etp);
				if (index != -1) {
					etp.setSortOrder(index + 1);
				}

				return etp;
			}).collect(Collectors.toList());

			result = convertedTaskPlans.stream().map(ExecutiveTaskPlanDTO::new).collect(Collectors.toList());

			ids = actualEcutiveTaskPlans.stream().map(data -> data.getId()).collect(Collectors.toList());

			List<ExecutiveTaskExecution> executiveTaskExecution = new ArrayList<>();

			System.out.println("report size :"+ids);
			if (ids.size() != 0) {
				executiveTaskExecution = executiveTaskExecutionRepository
						.findExecutiveTaskExecutionByExecutiveTaskPlanIdIn(ids);

			}
			String variance;

			for (ExecutiveTaskPlanDTO extDTO : result) {

				for (ExecutiveTaskExecution ext : executiveTaskExecution) {

					if (extDTO.getPid().equalsIgnoreCase(ext.getExecutiveTaskPlan().getPid())) {

						extDTO.setServerDate(ext.getSendDate());
						extDTO.setExecutiveTaskExecutionPid(ext.getPid());
						extDTO.setExecutionLatitude(ext.getLatitude());
						extDTO.setExecutionLongitude(ext.getLongitude());
						extDTO.setGeoLocation(ext.getLocation());
						extDTO.setGeoTagLocation(ext.getAccountProfile().getLocation());
						variance = calculateVariance(ext.getPid());
						extDTO.setVariance(variance);
						executiveTaskPlanDTO.add(extDTO);
					}

				}
				if (extDTO.getTaskPlanStatus().equals(TaskPlanStatus.PENDING)) {
					extDTO.setTaskPlanStatus(TaskPlanStatus.PENDING);
					extDTO.setUserRemarks("");
					executiveTaskPlanDTO.add(extDTO);
				}
				else if(extDTO.getTaskPlanStatus().equals(TaskPlanStatus.SKIPPED))
				{
					extDTO.setTaskPlanStatus(TaskPlanStatus.SKIPPED);
					executiveTaskPlanDTO.add(extDTO);
				}
				
			}

		}

		return new ResponseEntity<>(executiveTaskPlanDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/day-plan-execution-report/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ExecutiveTaskPlanDTO> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {

		System.out.println("Enter to update location *************************************");
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);

		ExecutiveTaskPlanDTO executionView = new ExecutiveTaskPlanDTO();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();

			if (execution.getLatitude() != BigDecimal.ZERO) {
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);
				execution.setLocation(location);

			} else {
				System.out.println("-------No Location");
				execution.setLocation("No Location");
			}

			execution = executiveTaskExecutionRepository.save(execution);

			executionView.setGeoLocation(execution.getLocation());

		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/day-plan-execution-report/updateGeoLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ExecutiveTaskPlanDTO> updateLocationAccountProfiles(@PathVariable String pid) {

		System.out.println("Enter to update location *************************************");
		Optional<AccountProfile> opExecutiveeExecution = accountProfileRepository.findOneByPid(pid);

		ExecutiveTaskPlanDTO executionView = new ExecutiveTaskPlanDTO();
		if (opExecutiveeExecution.isPresent()) {
			AccountProfile execution = opExecutiveeExecution.get();

			if (execution.getLatitude() != BigDecimal.ZERO) {
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);
				execution.setLocation(location);

			} else {
				System.out.println("-------No Location");
				execution.setLocation("No Location");
			}

			execution = accountProfileRepository.save(execution);

			executionView.setGeoTagLocation(execution.getLocation());

		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}
	public String calculateVariance(String executionPid) {
		log.debug("Web request to update geo location variance report by executionPid " + executionPid);

		if (!executionPid.isEmpty()) {

			ExecutiveTaskExecution execution = executiveTaskExecutionRepository.findByExecutionPid(executionPid);

			if (execution.getLatitude() != null && execution.getAccountProfile().getLatitude() != null
					&& execution.getLatitude() != BigDecimal.ZERO
					&& execution.getAccountProfile().getLatitude() != BigDecimal.ZERO) {
				String variance = "";
				double accLocLat = execution.getAccountProfile().getLatitude().doubleValue();
				double accLocLng = execution.getAccountProfile().getLongitude().doubleValue();

				double exeLocLat = execution.getLatitude().doubleValue();
				double exeLocLng = execution.getLongitude().doubleValue();

				String origin = accLocLat + "," + accLocLng;
				String destination = exeLocLat + "," + exeLocLng;

				if (!origin.equals(destination)) {
					double distance = geoLocationService.computeDistanceBetween(accLocLat, accLocLng, exeLocLat,
							exeLocLng);
					double roundedNum = Math.round(distance * 100.0) / 100.0;
					variance = roundedNum + " KM";
				} else {
					variance = "0 KM";
				}
				execution.setLocationVariance(variance);
				// update
				executiveTaskExecutionRepository.save(execution);

				return variance;
			} else {

				return "";
			}
		} else {
			return "";
		}
	}

}
