package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.TaskGroupService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing TaskGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Controller
@RequestMapping("/web")
public class TaskGroupResource {

	private final Logger log = LoggerFactory.getLogger(TaskGroupResource.class);

	@Inject
	private TaskGroupService taskGroupService;

	@Inject
	private TaskService taskService;

	@Inject
	private ActivityGroupService activityGroupService;

	/**
	 * POST /task-groups : Create a new taskGroup.
	 *
	 * @param taskGroupDTO
	 *            the taskGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskGroupDTO, or with status 400 (Bad Request) if the
	 *         taskGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskGroupDTO> createTaskGroup(@Valid @RequestBody TaskGroupDTO taskGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save TaskGroup : {}", taskGroupDTO);
		if (taskGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskGroup", "idexists", "A new taskGroup cannot already have an ID"))
					.body(null);
		}
		if (taskGroupService.findByName(taskGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskGroup", "nameexists", "Task Group already in use"))
					.body(null);
		}
		TaskGroupDTO result = taskGroupService.save(taskGroupDTO);
		return ResponseEntity.created(new URI("/web/task-groups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taskGroup", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /task-groups : Updates an existing taskGroup.
	 *
	 * @param taskGroupDTO
	 *            the taskGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskGroupDTO, or with status 400 (Bad Request) if the
	 *         taskGroupDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the taskGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskGroupDTO> updateTaskGroup(@Valid @RequestBody TaskGroupDTO taskGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to update TaskGroup : {}", taskGroupDTO);
		if (taskGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskGroup", "idNotexists", "Task Group must have an ID"))
					.body(null);
		}
		Optional<TaskGroupDTO> existingTaskGroup = taskGroupService.findByName(taskGroupDTO.getName());
		if (existingTaskGroup.isPresent() && (!existingTaskGroup.get().getPid().equals(taskGroupDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskGroup", "nameexists", "Task Group already in use"))
					.body(null);
		}
		TaskGroupDTO result = taskGroupService.update(taskGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskGroup", "idNotexists", "Invalid Task Group ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("taskGroup", taskGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /task-groups : get all the taskGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         taskGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/task-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTaskGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaskGroups");
		
		model.addAttribute("taskGroups", taskGroupService.findAllByCompany());

		model.addAttribute("activityGroups", activityGroupService.findAllByCompany());
		model.addAttribute("tasks", taskService.findAllByCompany());

		return "company/taskGroups";
	}

	/**
	 * GET /task-groups/:id : get the "id" taskGroup.
	 *
	 * @param id
	 *            the id of the taskGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-groups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskGroupDTO> getTaskGroup(@PathVariable String pid) {
		log.debug("Web request to get TaskGroup by pid : {}", pid);
		return taskGroupService.findOneByPid(pid).map(taskGroupDTO -> new ResponseEntity<>(taskGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /task-groups/:id : delete the "id" taskGroup.
	 *
	 * @param id
	 *            the id of the taskGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/task-groups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaskGroup(@PathVariable String pid) {
		log.debug("REST request to delete TaskGroup : {}", pid);
		taskGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taskGroup", pid.toString())).build();
	}

	@RequestMapping(value = "/task-groups/assignTasks", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedTasks(@RequestParam String pid, @RequestParam String assignedTasks) {
		log.debug("REST request to save assigned account type : {}", pid);
		taskGroupService.saveAssignedTasks(pid, assignedTasks);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
