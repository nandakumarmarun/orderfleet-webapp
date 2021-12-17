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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.TaskUserSetting;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.TaskSettingService;
import com.orderfleet.webapp.service.TaskUserSettingService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.TaskSettingDTO;
import com.orderfleet.webapp.web.rest.dto.TaskUserSettingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing TaskUserSetting.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
@Controller
@RequestMapping("/web")
public class TaskUserSettingResource {

	private final Logger log = LoggerFactory.getLogger(TaskUserSettingResource.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private TaskUserSettingService taskUserSettingService;

	@Inject
	private ActivityService activityService;

	@Inject
	private UserService userService;

	@Inject
	private TaskSettingService taskSettingService;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	/**
	 * POST /taskUserSettings : Create a new taskUserSetting.
	 *
	 * @param taskUserSettingDTO
	 *            the taskUserSettingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskUserSettingDTO, or with status 400 (Bad Request) if the
	 *         taskUserSetting has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/taskUserSettings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskUserSettingDTO> createTaskUserSetting(
			@Valid @RequestBody TaskUserSettingDTO taskUserSettingDTO) throws URISyntaxException {
		log.debug("Web request to save TaskUserSetting : {}", taskUserSettingDTO);
		if (taskUserSettingDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("taskUserSetting", "idexists",
					"A new taskUserSetting cannot already have an ID")).body(null);
		}
		if (taskUserSettingService.findByExecutorPidAndTaskSettingPid(taskUserSettingDTO.getExecutorPid(),
				taskUserSettingDTO.getTaskSettingPid()) != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserSetting", "nameexists", "Task User Setting already in use"))
					.body(null);
		}
		TaskUserSettingDTO result = taskUserSettingService.save(taskUserSettingDTO);
		return ResponseEntity.created(new URI("/web/taskUserSettings/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taskUserSetting", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /taskUserSettings : Updates an existing taskUserSetting.
	 *
	 * @param taskUserSettingDTO
	 *            the taskUserSettingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskUserSettingDTO, or with status 400 (Bad Request) if the
	 *         taskUserSettingDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the taskUserSettingDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/taskUserSettings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskUserSettingDTO> updateTaskUserSetting(
			@Valid @RequestBody TaskUserSettingDTO taskUserSettingDTO) throws URISyntaxException {
		log.debug("Web request to update TaskUserSetting : {}", taskUserSettingDTO);
		if (taskUserSettingDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserSetting", "idNotexists", "TaskUserSetting must have an ID"))
					.body(null);
		}
		TaskUserSetting taskUserSetting = taskUserSettingService.findByExecutorPidAndTaskSettingPid(
				taskUserSettingDTO.getExecutorPid(), taskUserSettingDTO.getTaskSettingPid());
		if (taskUserSetting != null && (!taskUserSetting.getPid().equals(taskUserSettingDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserSetting", "nameexists", "Task User Setting already in use"))
					.body(null);
		}
		TaskUserSettingDTO result = taskUserSettingService.update(taskUserSettingDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("taskUserSetting", "idNotexists", "Invalid TaskUserSetting ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("taskUserSetting", taskUserSettingDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /taskUserSettings : get all the taskUserSettings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         taskUserSettings in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/taskUserSettings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTaskUserSettings(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaskUserSettings");
		model.addAttribute("taskUserSettings", taskUserSettingService.findAllByCompany());
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("taskSettings", taskSettingService.findAllByCompany());
		return "company/taskUserSettings";
	}

	/**
	 * GET /taskUserSettings/load-documents : get document list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         DocumentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/taskUserSettings/load-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
	 * GET /taskUserSettings/load-task-settings : get task setting list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         task setting or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/taskUserSettings/load-task-settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<TaskSettingDTO>> getTaskSettings(@RequestParam String activityPid,
			@RequestParam String documentPid) {
		log.debug("Web request to get TaskSetting list : {}", documentPid);
		List<TaskSettingDTO> taskSettingDTOs = new ArrayList<>();
		if (documentPid.equals("-1")) {
			taskSettingDTOs = taskSettingService.findAllByCompany();
		} else {
			taskSettingDTOs = taskSettingService.findByActivityPidAndDocumentPid(activityPid, documentPid);
		}
		return new ResponseEntity<>(taskSettingDTOs, HttpStatus.OK);
	}

	/**
	 * GET /taskUserSettings/:id : get the "id" taskUserSetting.
	 *
	 * @param id
	 *            the id of the taskUserSettingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskUserSettingDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/taskUserSettings/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskUserSettingDTO> getTaskUserSetting(@PathVariable String pid) {
		log.debug("Web request to get TaskUserSetting by pid : {}", pid);
		return taskUserSettingService.findOneByPid(pid)
				.map(taskUserSettingDTO -> new ResponseEntity<>(taskUserSettingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /taskUserSettings/:id : delete the "id" taskUserSetting.
	 *
	 * @param id
	 *            the id of the taskUserSettingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/taskUserSettings/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaskUserSetting(@PathVariable String pid) {
		log.debug("REST request to delete TaskUserSetting : {}", pid);
		taskUserSettingService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taskUserSetting", pid.toString()))
				.build();
	}

}
