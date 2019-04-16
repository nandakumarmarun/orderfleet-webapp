package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * 
 * @author Sarath
 * @since July 13, 2016
 */

@Controller
@RequestMapping("/web")
public class TaskListResource {

	private final Logger log = LoggerFactory.getLogger(TaskListResource.class);

	@Inject
	private TaskListService taskListService;

	@Inject
	private TaskService taskService;
	
	@Inject
	private ActivityService activityService;
	
	@Inject
	private LocationService locationService;

	/**
	 * POST /task-lists : Create a new taskList.
	 *
	 * @param taskListDTO
	 *            the taskListDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskListDTO, or with status 400 (Bad Request) if the taskList
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-lists", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskListDTO> createTaskList(@Valid @RequestBody TaskListDTO taskListDTO)
			throws URISyntaxException {
		log.debug("Web request to save TaskList : {}", taskListDTO);
		if (taskListDTO.getPid() != null) {
			return ResponseEntity
					.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("taskList", "idexists",
									"A new taskList cannot already have an ID")).body(null);
		}
		if (taskListService.findByName(taskListDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskList", "nameexists", "Task List already in use"))
					.body(null);
		}
		TaskListDTO result = taskListService.save(taskListDTO);
		return ResponseEntity.created(new URI("/web/task-lists/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taskList", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /task-lists : Updates an existing taskList.
	 *
	 * @param taskListDTO
	 *            the taskListDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskListDTO, or with status 400 (Bad Request) if the taskListDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         taskListDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-lists", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskListDTO> updateTaskList(@Valid @RequestBody TaskListDTO taskListDTO)
			throws URISyntaxException {
		log.debug("Web request to update TaskList : {}", taskListDTO);
		if (taskListDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskList", "idNotexists", "Task List must have an ID"))
					.body(null);
		}
		Optional<TaskListDTO> existingTaskList = taskListService.findByName(taskListDTO.getName());
		if (existingTaskList.isPresent() && (!existingTaskList.get().getPid().equals(taskListDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskList", "nameexists", "Task List already in use"))
					.body(null);
		}
		TaskListDTO result = taskListService.update(taskListDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskList", "idNotexists", "Invalid Task List ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("taskList", taskListDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /task-lists : get all the taskLists.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of taskLists
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/task-lists", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTaskLists(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaskLists");
		List<TaskListDTO> taskList = taskListService.findAllByCompany();
		model.addAttribute("taskLists", taskList);

		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("locations", locationService.findAllByCompany());
		model.addAttribute("tasks", taskService.findAllByCompany());

		return "company/taskLists";
	}
	
	/**
	 * GET /task-lists/:id : get the "id" taskList.
	 *
	 * @param id
	 *            the id of the taskListDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskListDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-lists/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskListDTO> getTaskList(@PathVariable String pid) {
		log.debug("Web request to get TaskList by pid : {}", pid);
		return taskListService.findOneByPid(pid).map(taskListDTO -> new ResponseEntity<>(taskListDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /task-lists/:id : delete the "id" taskList.
	 *
	 * @param id
	 *            the id of the taskListDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/task-lists/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaskList(@PathVariable String pid) {
		log.debug("REST request to delete TaskList : {}", pid);
		taskListService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taskList", pid.toString())).build();
	}

	@RequestMapping(value = "/task-lists/assignTasks", method = RequestMethod.POST)
	@Timed
	@ResponseBody
	public ResponseEntity<TaskListDTO> saveAssignedTasks(@RequestParam String pid, @RequestParam String assignedTasks) {
		log.debug("REST request to save assigned account type : {}", pid);
		TaskListDTO taskListDTO = taskListService.saveAssignedTasks(pid, assignedTasks);
		return new ResponseEntity<>(taskListDTO, HttpStatus.OK);
	}
}
