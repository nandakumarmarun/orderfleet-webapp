package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DashboardUserService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardUserDTO;

/**
 * Web controller for managing DashboardUser.
 * 
 * @author Muhammed Riyas T
 * @since August 23, 2016
 */
@Controller
@RequestMapping("/web")
public class DashboardUserResource {

	private final Logger log = LoggerFactory.getLogger(DashboardUserResource.class);

	@Inject
	private DashboardUserService dashboardUserService;

	@Inject
	private UserService userService;

	/**
	 * POST /dashboard-users : Create a new dashboardUsers.
	 *
	 * @param usersPids
	 *            the dashboardUsers to create
	 * @return the ResponseEntity with status 201 (Created)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/dashboard-users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Void> createDashboardUser(@RequestBody List<DashboardUserDTO> dashboardUserDTOs) throws URISyntaxException {
		log.debug("Web request to save DashboardUser : {}", dashboardUserDTOs);
		dashboardUserService.save(dashboardUserDTOs);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * GET /dashboard-users : get all the dashboard users.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         dashboard-users in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/dashboard-users", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDashboardUsers(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of DashboardUsers");
		model.addAttribute("dashboardUsers", dashboardUserService.findUsersByCompanyId());
		model.addAttribute("users", userService.findAllByCompany());
		return "company/dashboardUsers";
	}

	/**
	 * GET /dashboard-users/edit : get dashboardUser.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         UserDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/dashboard-users/edit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DashboardUserDTO>> getDashboardUser() {
		log.debug("Web request to get DashboardUser");
		
		List<DashboardUserDTO> userDTOs = dashboardUserService.findDashboardUsersByCompanyIdAndSordOrder();
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}

}
