package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.UserTaskListAssociation;
import com.orderfleet.webapp.repository.UserTaskListAssociationRepository;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.UserTaskListAssociationService;
import com.orderfleet.webapp.web.rest.dto.UserTaskListTaskDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web")
public class UserTaskListAssociationResource {

	private final Logger log = LoggerFactory.getLogger(UserTaskListAssociationResource.class);

	@Inject
	private TaskListService taskListService;

	@Inject
	private UserService userService;

	@Inject
	private UserTaskListAssociationService userTaskListAssociationService;

	@Inject
	private UserTaskListAssociationRepository userTaskListAssociationRepository;

	@RequestMapping(value = "/user-task-list-association", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllUserTaskListAssignments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UserTaskListAssignments");

		model.addAttribute("taskLists", taskListService.findAllByCompany());

		model.addAttribute("users", userService.findAllByCompany());

		return "company/userTaskListAssociation";
	}

	@RequestMapping(value = "/user-task-list-association", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UserTaskListTaskDTO> createUserTaskListAssociation(
			@Valid @RequestBody UserTaskListTaskDTO userTaskListAssignmentDTO) throws URISyntaxException {

		List<UserTaskListAssociation> userTaskList = userTaskListAssociationRepository.findAll();
		Optional<UserTaskListAssociation> userTask = userTaskList.stream()
				.filter(ust -> ust.getUser().getPid().equals(userTaskListAssignmentDTO.getUserPid())
						&& ust.getTaskListId().equals(userTaskListAssignmentDTO.getTaskListPid()))
				.findAny();
		if(userTask.isPresent())
		{
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "User has already assigned this TaskList", "Account Profile already in use"))
					.body(null);
		}

		log.debug("Web request to save UserTaskListAssociation : {}", userTaskListAssignmentDTO);
		if (userTaskListAssignmentDTO.getPid() != null) {
			return null;
		}

		UserTaskListTaskDTO result = userTaskListAssociationService.save(userTaskListAssignmentDTO);
		System.out.println(result);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-task-list-association/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserTaskListTaskDTO> getEmployeeWiseTaskList(@RequestParam String userPid) {
		log.info("web request to get employee wise task list:" + userPid);
		List<UserTaskListTaskDTO> userWiseTaskList = userTaskListAssociationService.getAllTasksByEmployee(userPid);
//         userWiseTaskList.stream().forEach(a->log.info("size of tasks  : "+a.getTasks().size()));
		return userWiseTaskList;

	}

	@RequestMapping(value = "/user-task-list-association/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUserTaskListAssignment(@PathVariable String pid) {
		log.debug("REST request to delete UserTaskListAssignment : {}", pid);
		userTaskListAssociationService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("userTaskListAssignment", pid.toString())).build();
	}
}
