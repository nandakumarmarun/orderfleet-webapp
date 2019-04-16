package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing UserTaskAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Controller
@RequestMapping("/web")
public class UserTaskAssignmentResource {

	private final Logger log = LoggerFactory.getLogger(UserTaskAssignmentResource.class);

	@Inject
	private UserTaskAssignmentService userTaskAssignmentService;

	@Inject
	private TaskService taskService;

	@Inject
	private UserService userService;

	/**
	 * POST /user-task-assignment : Create a new userTaskAssignment.
	 *
	 * @param userTaskAssignmentDTO
	 *            the userTaskAssignmentDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new userTaskAssignmentDTO, or with status 400 (Bad Request) if
	 *         the userTaskAssignment has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-task-assignment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UserTaskAssignmentDTO> createUserTaskAssignment(
			@Valid @RequestBody UserTaskAssignmentDTO userTaskAssignmentDTO) throws URISyntaxException {
		log.debug("Web request to save UserTaskAssignment : {}", userTaskAssignmentDTO);
		if (userTaskAssignmentDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userTaskAssignment", "idexists",
					"A new userTaskAssignment cannot already have an ID")).body(null);
		}

		UserTaskAssignmentDTO result = userTaskAssignmentService.save(userTaskAssignmentDTO);
		return ResponseEntity.created(new URI("/web/user-task-assignment/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("userTaskAssignment", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /user-task-assignment : Updates an existing userTaskAssignment.
	 *
	 * @param userTaskAssignmentDTO
	 *            the userTaskAssignmentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         userTaskAssignmentDTO, or with status 400 (Bad Request) if the
	 *         userTaskAssignmentDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the userTaskAssignmentDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-task-assignment", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserTaskAssignmentDTO> updateUserTaskAssignment(
			@Valid @RequestBody UserTaskAssignmentDTO userTaskAssignmentDTO) throws URISyntaxException {
		log.debug("Web request to update UserTaskAssignment : {}", userTaskAssignmentDTO);
		if (userTaskAssignmentDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userTaskAssignment", "idNotexists", "Task Group must have an ID"))
					.body(null);
		}
		UserTaskAssignmentDTO result = userTaskAssignmentService.update(userTaskAssignmentDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("userTaskAssignment", "idNotexists", "Invalid Task Group ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("userTaskAssignment", userTaskAssignmentDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /user-task-assignment : get all the userTaskAssignments.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskAssignments in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-task-assignment", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllUserTaskAssignments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UserTaskAssignments");
		
		model.addAttribute("userTaskAssignments", userTaskAssignmentService.findAllByCompany());

		model.addAttribute("tasks", taskService.findAllByCompany());

		model.addAttribute("users", userService.findAllByCompany());

		return "company/userTaskAssignments";
	}

	/**
	 * GET /user-task-assignment/:id : get the "id" userTaskAssignment.
	 *
	 * @param id
	 *            the id of the userTaskAssignmentDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         userTaskAssignmentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/user-task-assignment/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserTaskAssignmentDTO> getUserTaskAssignment(@PathVariable String pid) {
		log.debug("Web request to get UserTaskAssignment by pid : {}", pid);
		return userTaskAssignmentService.findOneByPid(pid)
				.map(userTaskAssignmentDTO -> new ResponseEntity<>(userTaskAssignmentDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /user-task-assignment/:id : delete the "id" userTaskAssignment.
	 *
	 * @param id
	 *            the id of the userTaskAssignmentDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/user-task-assignment/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUserTaskAssignment(@PathVariable String pid) {
		log.debug("REST request to delete UserTaskAssignment : {}", pid);
		userTaskAssignmentService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userTaskAssignment", pid.toString()))
				.build();
	}

}
