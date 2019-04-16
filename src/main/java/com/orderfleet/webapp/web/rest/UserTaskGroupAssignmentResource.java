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
import com.orderfleet.webapp.service.UserTaskGroupAssignmentService;
import com.orderfleet.webapp.service.TaskGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.UserTaskGroupAssignmentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing UserTaskGroupAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
@Controller
@RequestMapping("/web")
public class UserTaskGroupAssignmentResource {

	private final Logger log = LoggerFactory.getLogger(UserTaskGroupAssignmentResource.class);

	@Inject
	private UserTaskGroupAssignmentService userTaskGroupAssignmentService;

	@Inject
	private TaskGroupService taskGroupService;

	@Inject
	private UserService userService;

	/**
	 * POST /user-task-group-assignment : Create a new userTaskGroupAssignment.
	 *
	 * @param userTaskGroupAssignmentDTO
	 *            the userTaskGroupAssignmentDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new userTaskGroupAssignmentDTO, or with status 400 (Bad Request)
	 *         if the userTaskGroupAssignment has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-task-group-assignment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UserTaskGroupAssignmentDTO> createUserTaskGroupAssignment(
			@Valid @RequestBody UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO) throws URISyntaxException {
		log.debug("Web request to save UserTaskGroupAssignment : {}", userTaskGroupAssignmentDTO);
		if (userTaskGroupAssignmentDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userTaskGroupAssignment",
					"idexists", "A new userTaskGroupAssignment cannot already have an ID")).body(null);
		}

		UserTaskGroupAssignmentDTO duplicateUserTaskGroupAssignmentDTO = userTaskGroupAssignmentService
				.findAllByTaskGroupPidAndStartDate(userTaskGroupAssignmentDTO.getTaskGroupPid());

		if (duplicateUserTaskGroupAssignmentDTO != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userTaskGroupAssignment",
					"exists", "A new userTaskGroupAssignment cannot already created for this day")).body(null);
		}

		UserTaskGroupAssignmentDTO result = userTaskGroupAssignmentService.save(userTaskGroupAssignmentDTO);
		return ResponseEntity.created(new URI("/web/user-task-group-assignment/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("userTaskGroupAssignment", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /user-task-group-assignment : Updates an existing
	 * userTaskGroupAssignment.
	 *
	 * @param userTaskGroupAssignmentDTO
	 *            the userTaskGroupAssignmentDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         userTaskGroupAssignmentDTO, or with status 400 (Bad Request) if
	 *         the userTaskGroupAssignmentDTO is not valid, or with status 500
	 *         (Internal Server Error) if the userTaskGroupAssignmentDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-task-group-assignment", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserTaskGroupAssignmentDTO> updateUserTaskGroupAssignment(
			@Valid @RequestBody UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO) throws URISyntaxException {
		log.debug("Web request to update UserTaskGroupAssignment : {}", userTaskGroupAssignmentDTO);
		if (userTaskGroupAssignmentDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userTaskGroupAssignment",
					"idNotexists", "Task Group must have an ID")).body(null);
		}
		UserTaskGroupAssignmentDTO result = userTaskGroupAssignmentService.update(userTaskGroupAssignmentDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userTaskGroupAssignment", "idNotexists", "Invalid Task Group ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("userTaskGroupAssignment",
				userTaskGroupAssignmentDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /user-task-group-assignment : get all the userTaskGroupAssignments.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userTaskGroupAssignments in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-task-group-assignment", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllUserTaskGroupAssignments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UserTaskGroupAssignments");
		
		model.addAttribute("userTaskGroupAssignments", userTaskGroupAssignmentService.findAllByCompany());

		model.addAttribute("taskGroups", taskGroupService.findAllByCompany());

		model.addAttribute("users", userService.findAllByCompany());

		return "company/userTaskGroupAssignments";
	}

	/**
	 * GET /user-task-group-assignment/:id : get the "id"
	 * userTaskGroupAssignment.
	 *
	 * @param id
	 *            the id of the userTaskGroupAssignmentDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         userTaskGroupAssignmentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/user-task-group-assignment/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserTaskGroupAssignmentDTO> getUserTaskGroupAssignment(@PathVariable String pid) {
		log.debug("Web request to get UserTaskGroupAssignment by pid : {}", pid);
		return userTaskGroupAssignmentService.findOneByPid(pid)
				.map(userTaskGroupAssignmentDTO -> new ResponseEntity<>(userTaskGroupAssignmentDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /user-task-group-assignment/:id : delete the "id"
	 * userTaskGroupAssignment.
	 *
	 * @param id
	 *            the id of the userTaskGroupAssignmentDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/user-task-group-assignment/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUserTaskGroupAssignment(@PathVariable String pid) {
		log.debug("REST request to delete UserTaskGroupAssignment : {}", pid);
		userTaskGroupAssignmentService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("userTaskGroupAssignment", pid.toString())).build();
	}

}
