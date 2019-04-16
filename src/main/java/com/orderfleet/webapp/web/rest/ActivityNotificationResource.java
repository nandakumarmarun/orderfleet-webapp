package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.enums.NotificationType;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.service.ActivityNotificationService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web Controller for Activity Notification
 *
 * @author fahad
 * @since May 10, 2017
 */
@Controller
@RequestMapping("/web")
public class ActivityNotificationResource {

	private final Logger log = LoggerFactory.getLogger(ActivityNotificationResource.class);

	private final ActivityService activityService;

	private final ActivityNotificationService activityNotificationService;

	private final ActivityDocumentRepository activityDocumentRepository;

	private final DocumentService documentService;

	public ActivityNotificationResource(ActivityService activityService,
			ActivityDocumentRepository activityDocumentRepository, DocumentService documentService,
			ActivityNotificationService activityNotificationService) {
		super();
		this.activityService = activityService;
		this.activityNotificationService = activityNotificationService;
		this.activityDocumentRepository = activityDocumentRepository;
		this.documentService = documentService;
	}

	/**
	 * GET /activity-notifications : get all the activitie-notifications.
	 *
	 * @param model
	 *            the model
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activities in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/activity-notifications", method = RequestMethod.GET)
	public String otherVoucherTransaction(Model model) {
		log.debug("Web request to get a page of Activity Notification");
		List<ActivityNotificationDTO> activityNotificationDTOs = activityNotificationService
				.findAllActivityNotificationByCompanyId();
		model.addAttribute("activityNotifications", activityNotificationDTOs);
		model.addAttribute("activities", activityService.findAllByCompany());
		return "company/activityNotifications";

	}

	@Timed
	@RequestMapping(value = "/load-documents/{activityPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<DocumentDTO> loadAccountProfiles(@PathVariable("activityPid") String activityPid) {
		log.debug("Web request to  load Document by activitypid  {}", activityPid);
		List<ActivityDocument> activityDocuments = activityDocumentRepository.findByActivityPid(activityPid);
		List<DocumentDTO> documentDTOs = new ArrayList<DocumentDTO>();
		for (ActivityDocument activityDocument : activityDocuments) {
			DocumentDTO documentDTO = documentService.findOne(activityDocument.getDocument().getId());
			documentDTOs.add(documentDTO);
		}
		return documentDTOs;
	}

	/**
	 * POST /activity-notifications/activity : Create a new activity
	 * notification.
	 *
	 * @param id
	 *            for create
	 * @param activityPid
	 *            for create
	 * @param documentPid
	 *            for create
	 * @param notificationType
	 *            for create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new activityDTO, or with status 400 (Bad Request) if the activity
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */

	@RequestMapping(value = "/activity-notifications/activity", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<ActivityNotificationDTO> saveActivityNotifications(
			@RequestBody ActivityNotificationDTO activityNotificationDTO) {
		log.debug("REST request to save activity Notification : {}", activityNotificationDTO);
		if (activityNotificationDTO.getActivityPid().equals("-1")) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("Select Activity", "Activity is  Null", "Activity Needed"))
					.body(null);
		} else if (activityNotificationDTO.getDocumentPid().equals("-1")) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("Select Document", "Document is  Null", "Document Needed"))
					.body(null);
		} else if (activityNotificationDTO.getNotificationType() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Please Check Notificaton Type",
					"Not Checked Notificaton Type", "select any one")).body(null);
		}
		if (activityNotificationService
				.findActivityNotificationByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(
						activityNotificationDTO.getActivityPid(), activityNotificationDTO.getDocumentPid(),
						activityNotificationDTO.getNotificationType())
				.isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Activity Notification",
					" Activity Notification exists", "Activity already in use")).body(null);
		}
		if (activityNotificationDTO.getActivityPid() != "-1" && activityNotificationDTO.getNotificationType() != null
				&& activityNotificationDTO.getDocumentPid() != "-1") {
			activityNotificationService.save(activityNotificationDTO);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * PUT /activity-notifications/activity : update a new activity
	 * notification.
	 *
	 * @param id
	 *            for update
	 * @param activityPid
	 *            for update
	 * @param documentPid
	 *            for update
	 * @param notificationType
	 *            for update
	 * @return the ResponseEntity with status 201 (update) and with body the new
	 *         activityDTO, or with status 400 (Bad Request) if the activity has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/activity-notifications/activity", method = RequestMethod.PUT)
	@Timed
	public ResponseEntity<ActivityNotificationDTO> updateActivityNotifications(@RequestBody ActivityNotificationDTO activityNotificationDTO) {
		log.debug("REST request to save activity Notification : {}", activityNotificationDTO.getActivityPid());
		if (activityNotificationDTO.getActivityPid().equals("-1")) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("Select Activity", "Activity is  Null", "Activity Needed"))
					.body(null);
		} else if (activityNotificationDTO.getDocumentPid().equals("-1")) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("Select Document", "Document is  Null", "Document Needed"))
					.body(null);
		} else if ( activityNotificationDTO.getNotificationType() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Please Check Notificaton Type",
					"Not Checked Notificaton Type", "select any one")).body(null);
		}
		ActivityNotificationDTO oidActivityNotificationDTO = activityNotificationService
				.findActivityNotificationById(activityNotificationDTO.getId());
		if (oidActivityNotificationDTO != null) {
			if (activityNotificationDTO.getActivityPid() != "-1" && activityNotificationDTO.getNotificationType() != null && activityNotificationDTO.getDocumentPid() != "-1") {
				activityNotificationService.update(activityNotificationDTO);
			}

		} else {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Activity Notification",
					" Activity Notification not exists", "invalid id for Activity Notification")).body(null);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /activity-notifications/:pid : get the "pid" activity -notifications.
	 *
	 * @param pid
	 *            the pid of the activityNotificationDTO to retrieve
	 * @param documentPid
	 * @param notificationType
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         activityDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/activity-notifications/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ActivityNotificationDTO> getActivityNotification(@PathVariable String pid,
			@RequestParam(value = "documentPid") String documentPid,
			@RequestParam(value = "notificationType") String notificationType) {
		log.debug("Web request to get Activity Notification by pid : {}", pid);
		
		return activityNotificationService
				.findActivityNotificationByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(pid, documentPid,
						NotificationType.valueOf(notificationType))
				.map(ActivityNotificationDTO -> new ResponseEntity<>(ActivityNotificationDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /activities/:id : delete the "id" activity.
	 *
	 * @param pids
	 *            the pids of the activityNotificationDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/activity-notifications/{pids}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteActivity(@PathVariable String pids) {
		log.debug("REST request to delete Activity Notification : {}", pids);
		String[] pidArray = pids.split("_");
		activityNotificationService.delete(pidArray[0], pidArray[1], pidArray[2]);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activity", pidArray[0].toString()))
				.build();
	}

}