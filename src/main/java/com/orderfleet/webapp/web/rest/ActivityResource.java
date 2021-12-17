package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.ActivityStage;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityStageRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDocumentDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityStageDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivityResource {

	private final Logger log = LoggerFactory.getLogger(ActivityResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private ActivityService activityService;

	@Inject
	private AccountTypeService accountTypeService;
	
	@Inject
	private StageService stageService;

	@Inject
	private DocumentService documentService;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;
	
	@Inject
	private ActivityStageRepository activityStageRepository;

	@Inject
	private CompanyService companyService;

	/**
	 * POST /activities : Create a new activity.
	 *
	 * @param activityDTO
	 *            the activityDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         activityDTO, or with status 400 (Bad Request) if the activity has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.MASTER_DATA_MANAGER })
	@RequestMapping(value = "/activities", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO activityDTO)
			throws URISyntaxException {
		log.debug("Web request to save Activity : {}", activityDTO);
		if (activityDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activity", "idexists", "A new activity cannot already have an ID"))
					.body(null);
		}
		if (activityService.findByName(activityDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "nameexists", "Activity already in use"))
					.body(null);
		}
		activityDTO.setActivated(true);
		ActivityDTO result = activityService.save(activityDTO);
		return ResponseEntity.created(new URI("/web/activities/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("activity", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /activities : Updates an existing activity.
	 *
	 * @param activityDTO
	 *            the activityDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         activityDTO, or with status 400 (Bad Request) if the activityDTO is
	 *         not valid, or with status 500 (Internal Server Error) if the
	 *         activityDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activities", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityDTO> updateActivity(@Valid @RequestBody ActivityDTO activityDTO)
			throws URISyntaxException {
		log.debug("Web request to update Activity : {}", activityDTO);
		if (activityDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "idNotexists", "Activity must have an ID"))
					.body(null);
		}
		Optional<ActivityDTO> existingActivity = activityService.findByName(activityDTO.getName());
		if (existingActivity.isPresent() && (!existingActivity.get().getPid().equals(activityDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "nameexists", "Activity already in use"))
					.body(null);
		}
		ActivityDTO result = activityService.update(activityDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "idNotexists", "Invalid Activity ID"))
					.body(null);
		}
		activityService.update(activityDTO);
		
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("activity", activityDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /activities : get all the activities.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of activities in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/activities", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllActivities(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Activities");
		model.addAttribute("deactivatedActivity", activityService.findAllByCompanyAndDeactivatedActivity(false));
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("stages", stageService.findAllByCompany());
		model.addAttribute("documents", documentService.findAllByCompany());
		return "company/activities";
	}

	/**
	 * GET /site-activities : get all the activities.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of activities in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/site-activities", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllActivitieFromSiteadmin(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Activities");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/activities";
	}

	/**
	 * GET /activities/:id : get the "id" activity.
	 *
	 * @param id
	 *            the id of the activityDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/activities/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityDTO> getActivity(@PathVariable String pid) {
		log.debug("Web request to get Activity by pid : {}", pid);
		return activityService.findOneByPid(pid).map(activityDTO -> new ResponseEntity<>(activityDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /activities/:id : delete the "id" activity.
	 *
	 * @param id
	 *            the id of the activityDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/activities/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteActivity(@PathVariable String pid) {
		log.debug("REST request to delete Activity : {}", pid);
		activityService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activity", pid.toString())).build();
	}

	@RequestMapping(value = "/activities/assignAccountTypes", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountTypes(@RequestParam String pid,
			@RequestParam String assignedAccountTypes) {
		log.debug("REST request to save assigned account type : {}", pid);
		activityService.saveAssignedAccountTypes(pid, assignedAccountTypes);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/activities/documents/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActivityDocumentDTO>> getDocuments(@PathVariable String pid) {
		log.debug("Web request to get get targetBlocks : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by activityPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityDocument> activityDocuments = activityDocumentRepository.findByActivityPid(pid);
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

		List<ActivityDocumentDTO> result = activityDocuments.stream().map(ActivityDocumentDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/activities/assignDocuments/{pid}", method = RequestMethod.POST)
	public ResponseEntity<Void> assignDocuments(@PathVariable String pid,
			@RequestBody List<ActivityDocumentDTO> assignedDocuments) {
		log.debug("REST request to save assigned documents: {}", pid);
		activityService.saveAssignedDocumentsWithCompany(pid, assignedDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/activities/stages/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActivityStageDTO>> getSavedStages(@PathVariable String pid) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AS_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by activityPid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityStage> activityStages = activityStageRepository.findByActivityPid(pid);
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
		List<ActivityStageDTO> result = activityStages.stream().map(ActivityStageDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/activities/assignStages/{pid}", method = RequestMethod.POST)
	public ResponseEntity<Void> assignStages(@PathVariable String pid,
			@RequestParam String[] stagePids) {
		activityService.saveAssignedStages(pid, stagePids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * @author Fahad
	 * @since feb 6, 2017
	 * 
	 *        UPDATE STATUS /activities/changeStatus : update status
	 *        (Activated/Deactivated) of activity.
	 *
	 * @param activityDTO
	 *            the activityDTO to update Status
	 * @return the ResponseEntity with status 200 (OK) and with body the activityDTO
	 */
	@Timed
	@RequestMapping(value = "/activities/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActivityDTO> updateActivityStatus(@Valid @RequestBody ActivityDTO activityDTO) {
		log.debug("Request to update Status of activity", activityDTO);
		ActivityDTO res = activityService.updateActivityStatus(activityDTO.getPid(), activityDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since March 2, 2017
	 * 
	 *        Activate STATUS /activity/activateActivity : activate status of
	 *        Activity.
	 * 
	 * @param activitys
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/activities/activateActivity", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActivityDTO> activateActivity(@Valid @RequestParam String activitys) {
		log.debug("request to activate Activity ");
		String[] activity = activitys.split(",");
		for (String activitypid : activity) {
			activityService.updateActivityStatus(activitypid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/activities/get-by-status-filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActivityDTO>> getActivitysByStatus(@RequestParam boolean active,
			@RequestParam boolean deactivate) {
		log.debug("Web request to get get activitys : {}");
		List<ActivityDTO> activityDTOs = new ArrayList<>();
		if (active == true && deactivate == true) {
			activityDTOs.addAll(activityService.findAllByCompanyAndDeactivatedActivity(true));
			activityDTOs.addAll(activityService.findAllByCompanyAndDeactivatedActivity(false));
		} else if (active) {
			activityDTOs.addAll(activityService.findAllByCompanyAndDeactivatedActivity(true));
		} else if (deactivate) {
			activityDTOs.addAll(activityService.findAllByCompanyAndDeactivatedActivity(false));
		}

		return new ResponseEntity<>(activityDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/activities/get-by-company-filter/{companyPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ActivityDTO>> getActivitiesByCompany(@PathVariable String companyPid) {
		log.debug("Web request to get get activitys by company Pid : {}", companyPid);
		List<ActivityDTO> activityDTOs = activityService.findAllByCompanyPid(companyPid);
		return new ResponseEntity<>(activityDTOs, HttpStatus.OK);
	}

	/**
	 * POST /activities : Create a new activity.
	 *
	 * @param activityDTO
	 *            the activityDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         activityDTO, or with status 400 (Bad Request) if the activity has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activities/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ActivityDTO> createActivityFromAdmin(@Valid @RequestBody ActivityDTO activityDTO)
			throws URISyntaxException {
		log.debug("Web request to save Activity : {}", activityDTO);
		if (activityDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("activity", "idexists", "A new activity cannot already have an ID"))
					.body(null);
		}
		if (activityService.findByNameAndCompanyPid(activityDTO.getName(), activityDTO.getCompanyPid()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "nameexists", "Activity already in use"))
					.body(null);
		}
		activityDTO.setActivated(true);
		ActivityDTO result = activityService.saveFormSAdmin(activityDTO);
		return ResponseEntity.created(new URI("/web/activities/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("activity", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /activities : Updates an existing activity.
	 *
	 * @param activityDTO
	 *            the activityDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         activityDTO, or with status 400 (Bad Request) if the activityDTO is
	 *         not valid, or with status 500 (Internal Server Error) if the
	 *         activityDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activities/save", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityDTO> updateActivityFromAdmin(@Valid @RequestBody ActivityDTO activityDTO)
			throws URISyntaxException {
		log.debug("Web request to update Activity : {}", activityDTO);
		if (activityDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "idNotexists", "Activity must have an ID"))
					.body(null);
		}
		Optional<ActivityDTO> existingActivity = activityService.findByNameAndCompanyPid(activityDTO.getName(),
				activityDTO.getCompanyPid());
		if (existingActivity.isPresent() && (!existingActivity.get().getPid().equals(activityDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "nameexists", "Activity already in use"))
					.body(null);
		}
		ActivityDTO result = activityService.update(activityDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("activity", "idNotexists", "Invalid Activity ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("activity", activityDTO.getPid().toString())).body(result);
	}
	
	
	
}
