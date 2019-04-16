package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.UserTaskListAssignmentService;
import com.orderfleet.webapp.web.rest.dto.UserTaskListAssignmentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class UserTaskListAssignmentResource {

	private final Logger log = LoggerFactory.getLogger(UserTaskListAssignmentResource.class);

	@Inject
	private UserTaskListAssignmentService userTaskListAssignmentService;

	@Inject
	private TaskListService taskListService;

	@Inject
	private UserService userService;

	/**
	 * POST /user-task-list-assignment : Create a new userTaskListAssignment.
	 *
	 * @param userTaskListAssignmentDTO
	 *            the userTaskListAssignmentDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new userTaskListAssignmentDTO, or with status 400 (Bad Request)
	 *         if the userTaskListAssignment has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-task-list-assignment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UserTaskListAssignmentDTO> createUserTaskListAssignment(
			@Valid @RequestBody UserTaskListAssignmentDTO userTaskListAssignmentDTO) throws URISyntaxException {
		log.debug("Web request to save UserTaskListAssignment : {}", userTaskListAssignmentDTO);
		if (userTaskListAssignmentDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userTaskListAssignment",
					"idexists", "A new userTaskListAssignment cannot already have an ID")).body(null);
		}

		UserTaskListAssignmentDTO result = userTaskListAssignmentService.save(userTaskListAssignmentDTO);
		System.out.println(result);
		return ResponseEntity.created(new URI("/web/user-task-list-assignment/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("userTaskListAssignment", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /user-task-list-assignment : Updates an existing
	 * userTaskListAssignment.
	 *
	 * @param userTaskListAssignmentDTO
	 *            the userTaskListAssignmentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         userTaskListAssignmentDTO, or with status 400 (Bad Request) if
	 *         the userTaskListAssignmentDTO is not valid, or with status 500
	 *         (Internal Server Error) if the userTaskListAssignmentDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-task-list-assignment", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserTaskListAssignmentDTO> updateUserTaskListAssignment(
			@Valid @RequestBody UserTaskListAssignmentDTO userTaskListAssignmentDTO) throws URISyntaxException {
		log.debug("Web request to update UserTaskListAssignment : {}", userTaskListAssignmentDTO);
		if (userTaskListAssignmentDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userTaskListAssignment", "idNotexists", "Task List must have an ID"))
					.body(null);
		}
		UserTaskListAssignmentDTO result = userTaskListAssignmentService.update(userTaskListAssignmentDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userTaskListAssignment", "idNotexists", "Invalid Task List ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("userTaskListAssignment",
				userTaskListAssignmentDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /user-task-list-assignment : get all the userTaskListAssignments.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskListAssignments in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-task-list-assignment", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllUserTaskListAssignments(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UserTaskListAssignments");
		Page<UserTaskListAssignmentDTO> pageUserTaskListAssignment = userTaskListAssignmentService
				.findAllByCompany(pageable);
		model.addAttribute("pageUserTaskListAssignment", pageUserTaskListAssignment);

		model.addAttribute("taskLists", taskListService.findAllByCompany());

		model.addAttribute("users", userService.findAllByCompany());

		return "company/userTaskListAssignments";
	}

	/**
	 * GET /user-task-list-assignment/:id : get the "id" userTaskListAssignment.
	 *
	 * @param id
	 *            the id of the userTaskListAssignmentDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         userTaskListAssignmentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/user-task-list-assignment/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserTaskListAssignmentDTO> getUserTaskListAssignment(@PathVariable String pid) {
		log.debug("Web request to get UserTaskListAssignment by pid : {}", pid);
		return userTaskListAssignmentService.findOneByPid(pid)
				.map(userTaskListAssignmentDTO -> new ResponseEntity<>(userTaskListAssignmentDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /user-task-list-assignment/:id : delete the "id"
	 * userTaskListAssignment.
	 *
	 * @param id
	 *            the id of the userTaskListAssignmentDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/user-task-list-assignment/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUserTaskListAssignment(@PathVariable String pid) {
		log.debug("REST request to delete UserTaskListAssignment : {}", pid);
		userTaskListAssignmentService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("userTaskListAssignment", pid.toString())).build();
	}
}
