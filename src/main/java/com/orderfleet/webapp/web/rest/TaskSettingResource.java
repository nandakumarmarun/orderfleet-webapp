package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.TaskSetting;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.TaskSettingRepository;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.TaskSettingService;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.TaskSettingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing TaskSetting.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
@Controller
@RequestMapping("/web")
public class TaskSettingResource {

	private final Logger log = LoggerFactory.getLogger(TaskSettingResource.class);

	@Inject
	private TaskSettingService taskSettingService;
	
	@Inject
	private TaskSettingRepository taskSettingRepository;

	@Inject
	private ActivityService activityService;

	@Inject
	private DocumentFormsRepository documentFormsRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	/**
	 * POST /taskSettings : Create a new taskSetting.
	 *
	 * @param taskSettingDTO
	 *            the taskSettingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new taskSettingDTO, or with status 400 (Bad Request) if the
	 *         taskSetting has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/taskSettings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<TaskSettingDTO> createTaskSetting(@Valid @RequestBody TaskSettingDTO taskSettingDTO)
			throws URISyntaxException {
		log.debug("Web request to save TaskSetting : {}", taskSettingDTO);
		if (taskSettingDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("taskSetting", "idexists",
					"A new taskSetting cannot already have an ID")).body(null);
		}
		Optional<TaskSetting> optionalTaskSetting = taskSettingRepository.findByActivityPidAndDocumentPidAndStartDateColumnAndActivityEventAndTaskActivityPid(taskSettingDTO.getActivityPid(),
				taskSettingDTO.getDocumentPid(),taskSettingDTO.getStartDateColumn(),taskSettingDTO.getActivityEvent(), taskSettingDTO.getTaskActivityPid());
		if (optionalTaskSetting.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskSetting", "Task Setting already in use", "Task Setting already in use"))
					.body(null);
		}
		TaskSettingDTO result = taskSettingService.save(taskSettingDTO);
		return ResponseEntity.created(new URI("/web/taskSettings/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("taskSetting", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /taskSettings : Updates an existing taskSetting.
	 *
	 * @param taskSettingDTO
	 *            the taskSettingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         taskSettingDTO, or with status 400 (Bad Request) if the
	 *         taskSettingDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the taskSettingDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/taskSettings", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskSettingDTO> updateTaskSetting(@Valid @RequestBody TaskSettingDTO taskSettingDTO)
			throws URISyntaxException {
		log.debug("Web request to update TaskSetting : {}", taskSettingDTO);
		if (taskSettingDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("taskSetting", "idNotexists", "TaskSetting must have an ID"))
					.body(null);
		}
		Optional<TaskSetting> optionalTaskSetting = taskSettingRepository.findByActivityPidAndDocumentPidAndStartDateColumnAndActivityEventAndTaskActivityPid(taskSettingDTO.getActivityPid(),
				taskSettingDTO.getDocumentPid(),taskSettingDTO.getStartDateColumn(),taskSettingDTO.getActivityEvent(), taskSettingDTO.getTaskActivityPid());
		if (optionalTaskSetting.isPresent() && optionalTaskSetting.get() != null && (!optionalTaskSetting.get().getPid().equals(taskSettingDTO.getPid()))) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("taskSetting", "Task Setting already in use", "Task Setting already in use"))
						.body(null);
		}
		Optional<TaskSettingDTO> savedTaskSetting = taskSettingService.findOneByPid(taskSettingDTO.getPid());
		if (savedTaskSetting.isPresent()) {
			TaskSettingDTO result = taskSettingService.update(taskSettingDTO);
			return ResponseEntity.ok()
					.headers(HeaderUtil.createEntityUpdateAlert("taskSetting", taskSettingDTO.getPid().toString()))
					.body(result);
		}
		return ResponseEntity.ok().body(null);
	}

	/**
	 * GET /taskSettings : get all the taskSettings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         taskSettings in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/taskSettings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTaskSettings(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of TaskSettings");
		model.addAttribute("taskSettings", taskSettingService.findAllByCompany());
		model.addAttribute("activities", activityService.findAllByCompany());
		return "company/taskSettings";
	}

	/**
	 * GET /taskSettings/load-documents : get document list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         documentDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/taskSettings/load-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getDocuments(@RequestParam String pid) {
		log.debug("Web request to get document list : {}", pid);
		List<Document> documents = activityDocumentRepository.findProductGroupsByActivityPid(pid);
		return new ResponseEntity<>(documents.stream().map(DocumentDTO::new).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	/**
	 * GET /taskSettings/load-start-date-columns : get document list.
	 *
	 * @param pid
	 * 
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         form elements, or with status 404 (Not Found)
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/taskSettings/load-start-date-columns", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFormElementDTO>> getStartDateColumn(@RequestParam String pid) {
		log.debug("Web request to get start Date Columns : {}", pid);
		List<FormFormElementDTO> result = new ArrayList<>();
		List<Form> forms = documentFormsRepository.findFormsByDocumentPid(pid);
		if (!forms.isEmpty()) {
			List<FormFormElement> formFormElements = formFormElementRepository
					.findByCompanyIdAndFormsAndFormElementTypeIsDatePicker(forms);
			result = formFormElements.stream().map(FormFormElementDTO::new).collect(Collectors.toList());
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET /taskSettings/:id : get the "id" taskSetting.
	 *
	 * @param id
	 *            the id of the taskSettingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         taskSettingDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/taskSettings/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<TaskSettingDTO> getTaskSetting(@PathVariable String pid) {
		log.debug("Web request to get TaskSetting by pid : {}", pid);
		return taskSettingService.findOneByPid(pid)
				.map(taskSettingDTO -> new ResponseEntity<>(taskSettingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /taskSettings/:id : delete the "id" taskSetting.
	 *
	 * @param id
	 *            the id of the taskSettingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/taskSettings/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteTaskSetting(@PathVariable String pid) {
		log.debug("REST request to delete TaskSetting : {}", pid);
		taskSettingService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("taskSetting", pid.toString())).build();
	}

}
