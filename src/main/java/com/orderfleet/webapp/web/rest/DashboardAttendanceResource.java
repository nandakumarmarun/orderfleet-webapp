package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.service.DashboardAttendanceService;
import com.orderfleet.webapp.service.DashboardAttendanceUserService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardAttendanceDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class DashboardAttendanceResource {

	private final Logger log = LoggerFactory.getLogger(DashboardAttendanceResource.class);
	
	@Inject
	private UserService userService;
	
	@Inject
	private DashboardAttendanceService dashboardAttendanceService;
	
	@Inject
	private AttendanceStatusSubgroupService attendanceStatusSubgroupService;
	
	@Inject
	private DashboardAttendanceUserService dashboardAttendanceUserService;
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/dashboard-attendance", method = RequestMethod.GET)
	public String getAllDashboardAttendances(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of DashboardAttendances");
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("dashboardAttendances", dashboardAttendanceService.findAllByCompany());
		model.addAttribute("attendanceStatusSubgroups",attendanceStatusSubgroupService.findAllByCompany());
		return "company/dashboardAttendances";
	}
	
	/**
	 * POST /dashboardAttendances : Create a new dashboardAttendance.
	 *
	 * @param dashboardAttendanceDTO
	 *            the dashboardAttendanceDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new dashboardAttendanceDTO, or with status 400 (Bad Request) if the
	 *         dashboardAttendance has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/dashboard-attendance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardAttendanceDTO> createDashboardAttendance(@Valid @RequestBody DashboardAttendanceDTO dashboardAttendanceDTO)
			throws URISyntaxException {
		log.debug("Web request to save DashboardAttendance : {}", dashboardAttendanceDTO);
		if (dashboardAttendanceDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dashboardAttendance", "idexists",
					"A new dashboardAttendance cannot already have an ID")).body(null);
		}
		if (dashboardAttendanceService.findByName(dashboardAttendanceDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("dashboardAttendance", "nameexists", "Dashboard Item already in use"))
					.body(null);
		}
		DashboardAttendanceDTO result = dashboardAttendanceService.save(dashboardAttendanceDTO);
		return ResponseEntity.created(new URI("/web/dashboard-attendance" )).headers(HeaderUtil.createEntityCreationAlert("dashboardAttendance", result.getId().toString()))
				.body(result);
	}
	
	/**
	 * PUT /dashboardAttendances : Updates an existing dashboardAttendance.
	 *
	 * @param dashboardAttendanceDTO
	 *            the dashboardAttendanceDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         dashboardAttendanceDTO, or with status 400 (Bad Request) if the
	 *         dashboardAttendanceDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the dashboardAttendanceDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/dashboard-attendance", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DashboardAttendanceDTO> updateDashboardAttendance(@Valid @RequestBody DashboardAttendanceDTO dashboardAttendanceDTO)
			throws URISyntaxException {
		log.debug("Web request to update DashboardAttendance : {}", dashboardAttendanceDTO);
		if (dashboardAttendanceDTO.getId() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("dashboardAttendance", "idNotexists", "Dashboard Item must have an ID"))
					.body(null);
		}
		Optional<DashboardAttendanceDTO> existingDashboardAttendance = dashboardAttendanceService.findByName(dashboardAttendanceDTO.getName());
		if (existingDashboardAttendance.isPresent()
				&& (!existingDashboardAttendance.get().getId().equals(dashboardAttendanceDTO.getId()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("dashboardAttendance", "nameexists", "Dashboard Item already in use"))
					.body(null);
		}
		
		DashboardAttendanceDTO result = dashboardAttendanceService.update(dashboardAttendanceDTO);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("dashboardAttendance", dashboardAttendanceDTO.getId().toString()))
				.body(result);
	}
	
	/**
	 * GET /dashboardAttendances/:id : get the "id" dashboardAttendance.
	 *
	 * @param id
	 *            the id of the dashboardAttendanceDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         dashboardAttendanceDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/dashboard-attendance/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DashboardAttendanceDTO> getDashboardAttendance(@PathVariable Long id) {
		log.debug("Web request to get DashboardAttendance by id : {}", id);
		DashboardAttendanceDTO dashboardAttendanceDTO=dashboardAttendanceService.findOne(id);
		return new ResponseEntity<DashboardAttendanceDTO>(dashboardAttendanceDTO, HttpStatus.OK);
	}
	/**
	 * DELETE /dashboard-attendance/:id : delete the "id" dashboardAttendance.
	 *
	 * @param id
	 *            the id of the dashboardAttendanceDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/dashboard-attendance/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDashboardAttendance(@PathVariable Long id) {
		log.debug("REST request to delete DashboardAttendance : {}", id);
		dashboardAttendanceService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("dashboardAttendance", id.toString()))
				.build();
	}

	@RequestMapping(value = "/dashboard-attendance/users/{id}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<UserDTO>> getAssignedUsers(@PathVariable Long id) {
		log.debug("REST request to assign users : {}", id);
		List<UserDTO> userDTOs = dashboardAttendanceUserService.findUserByDashboardAttendanceId(id);
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/dashboard-attendance/assign-users", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedPriceLevels(@RequestParam Long id, @RequestParam String assignedUsers) {
		log.debug("REST request to save assigned users : {}", id);
		dashboardAttendanceUserService.saveAssignedUsers(id, assignedUsers);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
}
