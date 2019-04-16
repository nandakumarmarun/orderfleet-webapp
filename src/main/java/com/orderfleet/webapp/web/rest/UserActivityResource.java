package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;

/**
 * Web controller for managing UserActivity.
 * 
 * @author Muhammed Riyas T
 * @since June 30, 2016
 */
@Controller
@RequestMapping("/web")
public class UserActivityResource {

	private final Logger log = LoggerFactory.getLogger(UserActivityResource.class);

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private ActivityService activityService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-activities : get all the user activities.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         activities in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-activities", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserActivities(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Activities");

		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		model.addAttribute("activities", activityService.findAllByCompany());

		return "company/userActivities";
	}

	/**
	 * * GET /user-activities/save : save user activities.
	 * 
	 * @param pid
	 * @param assignedActivities
	 * @return
	 */
	@Timed
	@RequestMapping(value = "/user-activities/save/{userPid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> save(@PathVariable String userPid, @RequestBody List<ActivityDTO> assignedActivities) {
		log.debug("REST request to save assigned assigned activities {}", userPid);
		userActivityService.save(userPid, assignedActivities);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-activities/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityDTO>> getUserActivities(@PathVariable String userPid) {
		log.debug("Web request to get get Activities by user pid : {}", userPid);
		return new ResponseEntity<>(userActivityService.findActivitiesByUserPid(userPid), HttpStatus.OK);
	}

}
