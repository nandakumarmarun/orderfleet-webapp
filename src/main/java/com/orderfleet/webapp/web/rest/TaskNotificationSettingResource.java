package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentAccountingVoucherColumnService;
import com.orderfleet.webapp.service.DocumentInventoryVoucherColumnService;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.TaskNotificationSettingService;
import com.orderfleet.webapp.web.rest.api.dto.DocumentAccountingVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.api.dto.DocumentInventoryVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.TaskNotificationSettingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing TaskSetting.
 * 
 * @author Fahad
 * @since October 30, 2017
 */

@Controller
@RequestMapping("/web")
public class TaskNotificationSettingResource {

	private final Logger log = LoggerFactory.getLogger(TaskNotificationSettingResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private ActivityService activityService;
	
	@Inject
	private ActivityDocumentRepository activityDocumentRepository;
	
	@Inject
	private FormElementService formElementService;
	
	@Inject
	private DocumentInventoryVoucherColumnService inventoryVoucherColumnService;
	
	@Inject
	private DocumentAccountingVoucherColumnService accountingVoucherColumnService;
	
	@Inject
	private TaskNotificationSettingService taskNotificationSettingService;
	
	
	
	/**
	 * POST /task-notification-settings : Create a new TaskNotificationSetting.
	 *
	 * @param taskNotificationSettingDTO
	 *            the taskNotificationSettingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskNotificationSettingDTO, or with status 400 (Bad Request) if the
	 *         taskNotificationSetting has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-notification-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskNotificationSettingDTO> createTaskNotificationSetting(@Valid @RequestBody TaskNotificationSettingDTO taskNotificationSettingDTO)
			throws URISyntaxException {
		log.debug("Web request to save  TaskNotificationSetting : {}", taskNotificationSettingDTO);
		if (taskNotificationSettingDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("taskSetting", "idexists",
					"A new taskSetting cannot already have an ID")).body(null);
		}
		Optional<TaskNotificationSettingDTO> optionalTaskNotificationSetting = taskNotificationSettingService.findByActivityPidAndDocumentPidAndActivityEvent(taskNotificationSettingDTO.getActivityPid(),
				taskNotificationSettingDTO.getDocumentPid(),taskNotificationSettingDTO.getActivityEvent());
		if (optionalTaskNotificationSetting.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskNotificationSetting", "Task Notification Setting already in use", "Task Notification Setting already in use"))
					.body(null);
		}
		
		TaskNotificationSettingDTO result = taskNotificationSettingService.save(taskNotificationSettingDTO);
		return ResponseEntity.created(new URI("/web/task-notification-settings/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taskSetting", result.getPid().toString())).body(result);
	}
	
	/**
	 * PUT /task-notification-settings : Updates an existing TaskNotificationSetting.
	 *
	 * @param taskNotificationSettingDTO
	 *            the taskNotificationSettingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskNotificationSettingDTO, or with status 400 (Bad Request) if the
	 *         taskNotificationSettingDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the taskNotificationSettingDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/task-notification-settings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskNotificationSettingDTO> updateTaskSetting(@Valid @RequestBody TaskNotificationSettingDTO taskNotificationSettingDTO)
			throws URISyntaxException {
		log.debug("Web request to update TaskSetting : {}", taskNotificationSettingDTO);
		if (taskNotificationSettingDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskSetting", "idNotexists", "TaskSetting must have an ID"))
					.body(null);
		}
		Optional<TaskNotificationSettingDTO> optionalTaskNotificationSetting = taskNotificationSettingService.findByActivityPidAndDocumentPidAndActivityEvent(taskNotificationSettingDTO.getActivityPid(),
				taskNotificationSettingDTO.getDocumentPid(),taskNotificationSettingDTO.getActivityEvent());
		if (optionalTaskNotificationSetting.isPresent() && optionalTaskNotificationSetting.get() != null && (!optionalTaskNotificationSetting.get().getPid().equals(taskNotificationSettingDTO.getPid()))) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("taskSetting", "Task Setting already in use", "Task Setting already in use"))
						.body(null);
		}
		Optional<TaskNotificationSettingDTO> savedTaskSetting = taskNotificationSettingService.findOneByPid(taskNotificationSettingDTO.getPid());
		if (savedTaskSetting.isPresent()) {
			TaskNotificationSettingDTO result = taskNotificationSettingService.update(taskNotificationSettingDTO);
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityUpdateAlert("taskSetting", taskNotificationSettingDTO.getPid().toString()))
					.body(result);
		}
		return ResponseEntity.ok().body(null);
	}
	
	/**
	 * GET /task-notification-settings : get all the taskNotificationSettings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         taskNotificationSettings in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/task-notification-settings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTaskNotificationSettings(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaskNotificationSettings");
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("taskNotificationSettings", taskNotificationSettingService.findAllByCompanyId());
		return "company/taskNotificationSettings";
	}
	
	/**
	 * GET /task-notification-settings/load-documents : get document list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         documentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-notification-settings/load-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
	 * GET /task-notification-settings/load-dynamic-documents : get Form Element list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         form elements, or with status 404 (Not Found)
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/task-notification-settings/load-dynamic-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormElementDTO>> getDynamicDocument(@RequestParam String pid) {
		log.debug("Web request to get Form Elements : {}", pid);
		List<FormElementDTO> result =formElementService.findAllByCompanyAndDeactivatedFormElement(true);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	/**
	 * GET /task-notification-settings/load-inventory-vouchers: get DocumentInventoryVoucherColumnDTO list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         form elements, or with status 404 (Not Found)
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/task-notification-settings/load-inventory-vouchers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentInventoryVoucherColumnDTO>> getInventoryVoucher(@RequestParam String pid) {
		log.debug("Web request to get document inventory voucher column : {}", pid);
		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = inventoryVoucherColumnService.findAllByCompanyIdAndDocumentPid(pid);
		List<DocumentInventoryVoucherColumnDTO>documentInventoryVoucherColumnDTOs=documentInventoryVoucherColumns.stream().map(DocumentInventoryVoucherColumnDTO::new)
		.collect(Collectors.toList());
		return new ResponseEntity<>(documentInventoryVoucherColumnDTOs, HttpStatus.OK);
	}
	/**
	 * GET /task-notification-settings/load-accounting-vouchers : get DocumentAccountingVoucherColumnDTO list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         form elements, or with status 404 (Not Found)
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/task-notification-settings/load-accounting-vouchers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentAccountingVoucherColumnDTO>> getAccountingVoucher(@RequestParam String pid) {
		log.debug("Web request to get document accounting voucher column : {}", pid);
		List<DocumentAccountingVoucherColumn> documentAccountingVoucherColumns=accountingVoucherColumnService.findByCompanyIdAndDocumentPid(pid);
		List<DocumentAccountingVoucherColumnDTO> result =documentAccountingVoucherColumns.stream().map(DocumentAccountingVoucherColumnDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	/**
	 * GET /task-notification-settings/:id : get the "id" taskNotificationSetting.
	 *
	 * @param id
	 *            the id of the taskNotificationSettingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskSettingDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/task-notification-settings/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskNotificationSettingDTO> getTaskNotificationSetting(@PathVariable String pid) {
		log.debug("Web request to get TaskNotificationSetting by pid : {}", pid);
		return taskNotificationSettingService.findOneByPid(pid)
				.map(taskNotificationSettingDTO -> new ResponseEntity<>(taskNotificationSettingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /task-notification-settings/:id : delete the "id" taskNotificationSetting.
	 *
	 * @param id
	 *            the id of the taskNotificationSettingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/task-notification-settings/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaskNotificationSetting(@PathVariable String pid) {
		log.debug("REST request to delete TaskNotificationSetting : {}", pid);
		taskNotificationSettingService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taskSetting", pid.toString())).build();
	}
}
