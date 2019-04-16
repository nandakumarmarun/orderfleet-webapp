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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.UserActivityGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;

/**
 * Web controller for managing UserActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 30, 2016
 */
@Controller
@RequestMapping("/web")
public class UserActivityGroupResource {

	private final Logger log = LoggerFactory.getLogger(UserActivityGroupResource.class);

	@Inject
	private UserActivityGroupService userActivityGroupService;

	@Inject
	private ActivityGroupService activityGroupService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-activity-groups : get all the user activity groups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         activity groups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-activity-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserActivityGroupAssignment(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Activity Group Assignment");
		List<UserDTO> user = userService.findAllByCompany();
		model.addAttribute("users", user);
		model.addAttribute("activityGroups", activityGroupService.findAllByCompany());
		return "company/userActivityGroups";
	}

	/**
	 * * GET /user-activity-groups/save : save user activity groups.
	 * 
	 * @param pid
	 * @param assignedActivityGroups
	 * @return
	 */
	@RequestMapping(value = "/user-activity-groups/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String assignedActivityGroups) {
		log.debug("REST request to save assigned assigned activity groups", userPid);
		userActivityGroupService.save(userPid, assignedActivityGroups);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-activity-groups/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityGroupDTO>> getUserActivityGroups(@PathVariable String userPid) {
		log.debug("Web request to get get Activity Groups by user pid : {}", userPid);
		return new ResponseEntity<>(userActivityGroupService.findActivityGroupsByUserPid(userPid), HttpStatus.OK);
	}

}
