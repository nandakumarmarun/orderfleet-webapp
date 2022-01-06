package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.TaskUserNotificationSetting;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.TaskNotificationSettingService;
import com.orderfleet.webapp.service.TaskUserNotificationSettingService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.TaskNotificationSettingDTO;
import com.orderfleet.webapp.web.rest.dto.TaskUserNotificationSettingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 *Web controller for managing TaskUserNotificationSetting.
 *
 * @author fahad
 * @since May 31, 2017
 */
@Controller
@RequestMapping("/web")
public class TaskUserNotificationSettingResource {

	private final Logger log = LoggerFactory.getLogger(TaskUserNotificationSettingResource.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ActivityService activityService;

	@Inject
	private UserService userService;
	
	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	@Inject
	private TaskNotificationSettingService taskNotificationSettingService;
	
	@Inject
	private TaskUserNotificationSettingService taskUserNotificationSettingService;
	
	/**
	 * POST /task-user-notification-settings : Create a new taskUserNotificationSetting.
	 *
	 * @param taskUserNotificationSettingDTO
	 *            the taskUserNotificationSettingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskUserNotificationSettingDTO, or with status 400 (Bad Request) if the
	 *         taskUserNotificationSetting has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-user-notification-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskUserNotificationSettingDTO> createTaskUserSetting(
			@Valid @RequestBody TaskUserNotificationSettingDTO taskUserNotificationSettingDTO) throws URISyntaxException {
		log.debug("Web request to save TaskUserNotificationSetting : {}", taskUserNotificationSettingDTO);
		if (taskUserNotificationSettingDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("taskUserSetting", "idexists",
					"A new taskUserSetting cannot already have an ID")).body(null);
		}
		if (taskUserNotificationSettingService.findByExecutorPidAndTaskNotificationSettingPid(taskUserNotificationSettingDTO.getExecutorPid(),
				taskUserNotificationSettingDTO.getTaskNotificationSettingPid()) != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserSetting", "nameexists", "Task User Setting already in use"))
					.body(null);
		}
		TaskUserNotificationSettingDTO result = taskUserNotificationSettingService.save(taskUserNotificationSettingDTO);
		return ResponseEntity.created(new URI("/web/taskUserSettings/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taskUserSetting", result.getPid().toString()))
				.body(result);
	}
	
	/**
	 * PUT /task-user-notification-settings : Updates an existing taskUserNotificationSetting.
	 *
	 * @param taskUserNotificationSettingDTO
	 *            the taskUserNotificationSettingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskUserNotificationSettingDTO, or with status 400 (Bad Request) if the
	 *         taskUserNotificationSettingDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the taskUserNotificationSettingDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-user-notification-settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskUserNotificationSettingDTO> updateTaskUserNotificationSetting(
			@Valid @RequestBody TaskUserNotificationSettingDTO taskUserNotificationSettingDTO) throws URISyntaxException {
		log.debug("Web request to update TaskUserNotificationSetting : {}", taskUserNotificationSettingDTO);
		if (taskUserNotificationSettingDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserNotificationSetting", "idNotexists", "TaskUserNotificationSetting must have an ID"))
					.body(null);
		}
		TaskUserNotificationSetting taskUserNotificationSetting = taskUserNotificationSettingService.findByExecutorPidAndTaskNotificationSettingPid(
				taskUserNotificationSettingDTO.getExecutorPid(), taskUserNotificationSettingDTO.getTaskNotificationSettingPid());
		if (taskUserNotificationSetting != null && (!taskUserNotificationSetting.getPid().equals(taskUserNotificationSettingDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserNotificationSetting", "nameexists", "Task User Notification Setting already in use"))
					.body(null);
		}
		TaskUserNotificationSettingDTO result = taskUserNotificationSettingService.update(taskUserNotificationSettingDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserNotificationSetting", "idNotexists", "Invalid TaskUserNotificationSetting ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("taskUserNotificationSetting", taskUserNotificationSettingDTO.getPid().toString()))
				.body(result);
	}
	
	/**
	 * GET /task-user-notification-settings : get all the taskUserNotificationSettings.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         taskUserNotificationSettings in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the HTTP headers
	 */
	@RequestMapping(value = "/task-user-notification-settings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTaskUserNotificationSettings( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaskUserNotificationSettings");
		model.addAttribute("taskUserNotificationSettings", taskUserNotificationSettingService.findAllByCompanyId());
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("taskNotificationSettings", taskNotificationSettingService.findAllByCompanyId());
		return "company/taskUserNotificationSettings";
	}
	
	/**
	 * GET /task-user-notification-settings/load-documents : get document list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         DocumentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-user-notification-settings/load-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getDocuments(@RequestParam String pid) {
		log.debug("Web request to get document list : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AD_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get documents by activty pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Document> documents = activityDocumentRepository.findProductGroupsByActivityPid(pid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		return new ResponseEntity<>(documents.stream().map(DocumentDTO::new).collect(Collectors.toList()),
				HttpStatus.OK);
	}
	
	/**
	 * GET /task-user-notification-settings/load-task-notification-settings : get task notification setting list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         task setting or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-user-notification-settings/load-task-notification-settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<TaskNotificationSettingDTO>> getTaskSettings(@RequestParam String activityPid,
			@RequestParam String documentPid) {
		log.debug("Web request to get TaskSetting list : {}", documentPid);
		List<TaskNotificationSettingDTO> taskNotificationSettingDTOs = new ArrayList<>();
		if (documentPid.equals("-1")) {
			taskNotificationSettingDTOs = taskNotificationSettingService.findAllByCompanyId();
		} else {
			taskNotificationSettingDTOs = taskNotificationSettingService.findByActivityPidAndDocumentPid(activityPid, documentPid);
		}
		return new ResponseEntity<>(taskNotificationSettingDTOs, HttpStatus.OK);
	}
	
	/**
	 * GET /task-user-notification-settings/:id : get the "id" taskUserNotificationSetting.
	 *
	 * @param id
	 *            the id of the taskUserNotificationSettingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskUserNotificationSettingDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-user-notification-settings/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskUserNotificationSettingDTO> getTaskUserNotificationSetting(@PathVariable String pid) {
		log.debug("Web request to get TaskUserNotificationSetting by pid : {}", pid);
		return taskUserNotificationSettingService.findOneByPid(pid)
				.map(taskUserNotificationSettingDTO -> new ResponseEntity<>(taskUserNotificationSettingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /task-user-notification-settings/:id : delete the "id" taskUserNotificationSetting.
	 *
	 * @param id
	 *            the id of the taskUserNotificationSettingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/task-user-notification-settings/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaskUserNotificationSetting(@PathVariable String pid) {
		log.debug("REST request to delete TaskUserNotificationSetting : {}", pid);
		taskUserNotificationSettingService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taskUserNotificationSetting", pid.toString()))
				.build();
	}
}
