package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.UserRestrictedAttendanceSubgroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.UserRestrictedAttendanceSubgroupDTO;

@Controller
@RequestMapping("/web")
public class UserRestrictedAttendanceSubgroupResource {

	private final Logger log = LoggerFactory.getLogger(UserRestrictedAttendanceSubgroupResource.class);
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private AttendanceStatusSubgroupService attendanceStatusSubgroupService;
	
	@Inject
	private UserRestrictedAttendanceSubgroupService userRestrictedAttendanceSubgroupService;
	
	/**
	 * GET /user-restricted-attendance-subgroup : get all the filled user.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of filled
	 *         forms in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping("/user-restricted-attendance-subgroup")
	@Timed
	public String getUserRestrictedAttendanceSubgroup(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		model.addAttribute("attendanceSubgroups", attendanceStatusSubgroupService.findAllByCompany());
		return "company/userRestrictedAttendanceSubgroup";
	}
	
	@RequestMapping(value = "/user-restricted-attendance-subgroup/save", method = RequestMethod.POST)
	@ResponseBody
	@Timed
	public ResponseEntity<UserRestrictedAttendanceSubgroupDTO> createUserRestrictedAttendanceSubgroup( @RequestParam String userPid,@RequestParam String selectedSubgroups) throws URISyntaxException {
		log.debug("Web request to save User Restricted Attendance Subgroup : {}", selectedSubgroups);
		String[]subgroupIds=selectedSubgroups.split(",");
		List<Long>attendanceSubgroupIds=new ArrayList<>();
		for (String string : subgroupIds) {
			attendanceSubgroupIds.add((long) Integer.parseInt(string));
		}
		userRestrictedAttendanceSubgroupService.save(userPid, attendanceSubgroupIds);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@Timed
	@RequestMapping(value = "/user-restricted-attendance-subgroup/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserRestrictedAttendanceSubgroupDTO>> getUserRestrictedAttendanceSubgroup(@PathVariable String userPid) {
		log.debug("Web request to get get Favourite Documents by user pid : {}", userPid);
		return new ResponseEntity<>(userRestrictedAttendanceSubgroupService.findByUserPid(userPid),
				HttpStatus.OK);
	}
}
