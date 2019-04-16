package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Task.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Controller
@RequestMapping("/web")
public class TaskResource {

	private final Logger log = LoggerFactory.getLogger(TaskResource.class);

	@Inject
	private TaskService taskService;

	@Inject
	private ActivityService activityService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	/**
	 * POST /tasks : Create a new task.
	 *
	 * @param taskDTO
	 *            the taskDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskDTO, or with status 400 (Bad Request) if the task has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/tasks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<List<TaskDTO>> createTask(@Valid @RequestBody TaskDTO taskDTO) throws URISyntaxException {
		log.debug("Web request to save Task : {}", taskDTO);
		if (taskDTO.getPid() != null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("task", "idexists", "A new task cannot already have an ID"))
					.body(null);
		}
		if (taskDTO.getAccountPids() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("task", "accounts", "Please select accounts")).body(null);
		}
		taskDTO.setActivated(true);
		List<TaskDTO> taskDTOs = taskService.save(taskDTO);
		return ResponseEntity.created(new URI("/web/tasks/")).headers(HeaderUtil.createEntityCreationAlert("task", ""))
				.body(taskDTOs);
	}

	/**
	 * PUT /tasks : Updates an existing task.
	 *
	 * @param taskDTO
	 *            the taskDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskDTO, or with status 400 (Bad Request) if the taskDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the taskDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/tasks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskDTO> updateTask(@Valid @RequestBody TaskDTO taskDTO) throws URISyntaxException {
		log.debug("Web request to update Task : {}", taskDTO);
		if (taskDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("task", "idNotexists", "Task must have an ID")).body(null);
		}
		TaskDTO result = taskService.update(taskDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("task", "idNotexists", "Invalid Task ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("task", taskDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /tasks : get all the tasks.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of tasks in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTasks(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Tasks");
		// Page<TaskDTO> pageTask = taskService.findAllByCompany(pageable);
		// model.addAttribute("pageTask", pageTask);
		model.addAttribute("deactivatedTasks", taskService.findAllByCompanyAndActivated(false));
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		return "company/tasks";
	}

	@GetMapping(value = "/tasks.json")
	@ResponseBody
	public List<TaskDTO> getAllTasks() {
		return taskService.findAllByCompany();
	}

	/**
	 * GET /tasks/:id : get the "id" task.
	 *
	 * @param id
	 *            the id of the taskDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/tasks/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskDTO> getTask(@PathVariable String pid) {
		log.debug("Web request to get Task by pid : {}", pid);
		return taskService.findOneByPid(pid).map(taskDTO -> new ResponseEntity<>(taskDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /tasks/:id : delete the "id" task.
	 *
	 * @param id
	 *            the id of the taskDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/tasks/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTask(@PathVariable String pid) {
		log.debug("REST request to delete Task : {}", pid);
		taskService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("task", pid.toString())).build();
	}

	@RequestMapping(value = "/tasks/accountTypes/{activityPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountTypeDTO>> getAccountTypes(@PathVariable String activityPid) {
		log.debug("Web request to get AccountTypes by activity pid : {}", activityPid);
		return new ResponseEntity<>(activityService.findActivityAccountTypesByPid(activityPid), HttpStatus.OK);
	}

	@RequestMapping(value = "/tasks/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getAccouns(@RequestParam String activityPid,
			@RequestParam String accountTypePid) {
		log.debug("Web request to get get AccountTypes by account type pid : {}", accountTypePid);

		List<AccountProfileDTO> accountProfileDTOs = taskService
				.findAccountProfileByAccountTypePidAndActivityPid(activityPid, accountTypePid);

		if (accountProfileDTOs.size() == 0) {
			accountProfileDTOs = accountProfileService.findAllByAccountType(accountTypePid);
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		} else {
			List<String> accountProfilePids2 = new ArrayList<>();
			for (AccountProfileDTO dto : accountProfileDTOs) {
				accountProfilePids2.add(dto.getPid());
			}

			List<AccountProfileDTO> taskNotAssighnedAccountProfileDTOs = accountProfileService
					.findByPidNotIn(accountProfilePids2, accountTypePid);
			return new ResponseEntity<>(taskNotAssighnedAccountProfileDTOs, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/tasks/filterByActivityLocaion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<TaskDTO>> filterByActivityAndAccountProfileInLocation(@RequestParam String activityPids,
			@RequestParam String locationPids) throws URISyntaxException {
		List<TaskDTO> taskDTOs = new ArrayList<>();
		List<String> accountProfilePids = new ArrayList<>();
		// none selected
		if (activityPids.isEmpty() && locationPids.isEmpty()) {
			taskDTOs.addAll(taskService.findAllByCompany());
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		// location selected
		if (!locationPids.isEmpty()) {
			List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
					.findAccountProfileByLocationPidIn(Arrays.asList(locationPids.split(",")));
			if (accountProfileDTOs.size() > 0) {
				accountProfilePids = accountProfileDTOs.stream().map(ac -> ac.getPid()).collect(Collectors.toList());
			}
		}
		// both selected
		if (!activityPids.isEmpty() && !locationPids.isEmpty()) {
			if (accountProfilePids.size() > 0) {
				taskDTOs.addAll(taskService.findByActivityPidsAndAccountProfilePids(
						Arrays.asList(activityPids.split(",")), accountProfilePids));
			}
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		// activity selected
		if (!activityPids.isEmpty() && locationPids.isEmpty()) {
			taskDTOs.addAll(taskService.findByActivityPids(Arrays.asList(activityPids.split(","))));
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		// location selected
		if (activityPids.isEmpty() && !locationPids.isEmpty()) {
			if (accountProfilePids.size() > 0) {
				taskDTOs.addAll(taskService.findByAccountProfilePids(accountProfilePids));
			}
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 *        Filter Task /tasks/filterByActivityAndAccountType : filter the
	 *        task.
	 *
	 * @param activityPids
	 *            the activityPids of the Entity
	 * @param accountTypePids
	 *            the accountTypePids of the Entity
	 * 
	 * @return the TasksDTOs Object and ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/tasks/filterByActivityAndAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<TaskDTO>> filterTasksByActivityAndAccountType(@RequestParam String activityPids,
			@RequestParam String accountTypePids) throws URISyntaxException {
		List<TaskDTO> taskDTOs = new ArrayList<>();
		// none selected
		if (activityPids.isEmpty() && accountTypePids.isEmpty()) {
			taskDTOs.addAll(taskService.findAllByCompanyAndActivated(true));
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		// both selected
		if (!activityPids.isEmpty() && !accountTypePids.isEmpty()) {
			taskDTOs.addAll(taskService.findByActivityPidsAndAccountTypePidsAndActivated(
					Arrays.asList(activityPids.split(",")), Arrays.asList(accountTypePids.split(","))));
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);

		}
		// AccountType selected correct
		if (activityPids.isEmpty() && !accountTypePids.isEmpty()) {
			taskDTOs.addAll(taskService.findByAccountTypePidsAndActivated(Arrays.asList(accountTypePids.split(","))));
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		// Activity selected
		if (!activityPids.isEmpty() && accountTypePids.isEmpty()) {
			taskDTOs.addAll(taskService.findByActivityPidsAndActivated(Arrays.asList(activityPids.split(","))));
			return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(taskDTOs, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 *        Activate Task /tasks/changeStatus : deactivate the task.
	 *
	 * @param taskDTO
	 *            the taskDTO of the Entity
	 * @return the TaskDTO object and ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/tasks/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskDTO> updateTaskStatus(@Valid @RequestBody TaskDTO taskDTO) {
		log.debug("Web request to update status task : {}", taskDTO);
		TaskDTO res = taskService.updateTaskStatus(taskDTO.getPid(), taskDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 *        Activate Task /tasks/activateTask : activate the task.
	 *
	 * @param tasks
	 *            the tasks of the taskDTO
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/tasks/activateTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskDTO> activateTask(@Valid @RequestParam String tasks) {
		log.debug("Web request to activate status of Task ");
		String[] task = tasks.split(",");
		for (String taskpid : task) {
			taskService.updateTaskStatus(taskpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
