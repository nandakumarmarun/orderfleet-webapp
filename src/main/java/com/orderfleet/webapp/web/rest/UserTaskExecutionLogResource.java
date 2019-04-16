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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.UserTaskExecutionLogService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;

/**
 * Web controller for managing UserTaskExecutionLog.
 * 
 * @author Muhammed Riyas T
 * @since 11 Novembor, 2016
 */
@Controller
@RequestMapping("/web")
public class UserTaskExecutionLogResource {

	private final Logger log = LoggerFactory.getLogger(UserTaskExecutionLogResource.class);

	@Inject
	private UserTaskExecutionLogService userTaskExecutionLogService;

	@Inject
	private UserTaskAssignmentService userTaskAssignmentService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private EmployeeProfileService employeeProfileService;

	/**
	 * GET /user-task-execution-logs
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-task-execution-logs", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserTaskExecutionLogs(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Task Execution Logs");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/userTaskExecutionLog";
	}

	/**
	 * GET /user-task-execution-logs/filter :filter User Task Execution Logs.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         UserTaskAssignment
	 */
	@Timed
	@RequestMapping(value = "/user-task-execution-logs/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserTaskAssignmentDTO>> filterUserTaskExecutionLogs(@RequestParam("employeePid") String employeePid) {
		log.debug("Web request to filter User Task Execution Logs ");
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		return new ResponseEntity<>(userTaskExecutionLogService.findUserTaskExecutionLogsByUserPid(userPid),
				HttpStatus.OK);
	}

	/**
	 * GET /user-task-execution-logs/filter :filter User Task Execution Logs.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         UserTaskAssignment
	 */
	@Timed
	@RequestMapping(value = "/user-task-execution-logs/close-user-task", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> closeUserTask(@RequestParam String userTaskPid) {
		log.debug("Web request to close user task");
		userTaskAssignmentService.closeUserTaskAssignment(userTaskPid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
