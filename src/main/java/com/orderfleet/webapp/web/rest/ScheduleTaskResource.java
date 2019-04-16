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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.TaskGroupService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.UserTaskGroupAssignmentService;
import com.orderfleet.webapp.service.UserTaskListAssignmentService;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;
import com.orderfleet.webapp.web.rest.dto.UserTaskGroupAssignmentDTO;
import com.orderfleet.webapp.web.rest.dto.UserTaskListAssignmentDTO;

/**
 * Web controller for managing Schedule Task.
 * 
 * @author Muhammed Riyas T
 * @since Novembor 02, 2016
 */
@Controller
@RequestMapping("/web")
public class ScheduleTaskResource {

	private final Logger log = LoggerFactory.getLogger(ScheduleTaskResource.class);

	@Inject
	private UserService userService;

	@Inject
	private TaskService taskService;

	@Inject
	private TaskGroupService taskGroupService;

	@Inject
	private TaskListService taskListService;

	@Inject
	private UserTaskAssignmentService userTaskAssignmentService;

	@Inject
	private UserTaskGroupAssignmentService userTaskGroupAssignmentService;

	@Inject
	private UserTaskListAssignmentService userTaskListAssignmentService;

	/**
	 * GET /schedule-tasks : get schedule tasks page.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/schedule-tasks", method = RequestMethod.GET)
	public String getScheduleTasks(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Schedule Tasks");
		
		model.addAttribute("users",  userService.findAllByCompany());
		model.addAttribute("tasks", taskService.findAllByCompany());
		model.addAttribute("taskGroups", taskGroupService.findAllByCompany());
		model.addAttribute("taskLists", taskListService.findAllByCompany());
		return "company/scheduleTasks";
	}

	/**
	 * POST /schedule-tasks/user-tasks : save assigned user tasks.
	 *
	 * @param userTaskAssignmentDTOs
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/schedule-tasks/user-tasks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveAssignedUserTasks(@RequestBody List<UserTaskAssignmentDTO> userTaskAssignmentDTOs) {
		userTaskAssignmentService.save(userTaskAssignmentDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /schedule-tasks/user-tasks : get user tasks.
	 *
	 * @param userPid
	 * @return the ResponseEntity with status 200 (OK) and the list of user
	 *         tasks in body
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/schedule-tasks/user-tasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserTaskAssignmentDTO> getUserTaskAssignments(@RequestParam String userPid) {
		return userTaskAssignmentService.findUserTasksByUserPid(userPid);
	}

	/**
	 * POST /schedule-tasks/user-tasks : save assigned user tasks.
	 *
	 * @param userTaskAssignmentDTOs
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/schedule-tasks/user-task-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveAssignedUserTaskGroups(
			@RequestBody List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentDTOs) {
		userTaskGroupAssignmentService.save(userTaskGroupAssignmentDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /schedule-tasks/user-tasks : get user task groups.
	 *
	 * @param userPid
	 * @return the ResponseEntity with status 200 (OK) and the list of user task
	 *         group in body
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/schedule-tasks/user-task-groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserTaskGroupAssignmentDTO> getUserTaskGroupAssignments(@RequestParam String userPid) {
		return userTaskGroupAssignmentService.findUserTaskGroupsByUserPid(userPid);
	}

	/**
	 * POST /schedule-tasks/user-tasks : save assigned user tasks.
	 *
	 * @param userTaskAssignmentDTOs
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/schedule-tasks/user-task-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveAssignedUserTaskList(
			@RequestBody List<UserTaskListAssignmentDTO> userTaskListAssignmentDTOs) {
		userTaskListAssignmentService.save(userTaskListAssignmentDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /schedule-tasks/user-tasks : get user task list.
	 *
	 * @param userPid
	 * @return the ResponseEntity with status 200 (OK) and the list of user task
	 *         list in body
	 */
	@Timed
	@ResponseBody
	@RequestMapping(value = "/schedule-tasks/user-task-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserTaskListAssignmentDTO> getUserTaskListAssignments(@RequestParam String userPid) {
		return userTaskListAssignmentService.findUserTaskListByUserPid(userPid);
	}

}
