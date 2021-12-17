package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentPrintService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintCheckDTO;

/**
 * Web controller for managing Attendance.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentPrintResource {

	private final Logger log = LoggerFactory.getLogger(DocumentPrintResource.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private UserService userService;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private DocumentPrintService documentPrintService;

	/**
	 * GET /document-print : get documentPrint list.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/document-print", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentPrint(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Activities");
		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		return "company/documentPrint";
	}

	@RequestMapping(value = "/document-print", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<?> saveDocumentPrint(@Valid @RequestParam String userPid,
			@Valid @RequestParam String activityPid, @Valid @RequestParam String printEnableDocuments)
			throws URISyntaxException {
		documentPrintService.saveDocumentPrint(userPid, activityPid, printEnableDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-print/getActivities/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityDTO>> getActivities(@PathVariable String userPid) {
		log.debug("Web request to get activities by user : {}", userPid);
		List<ActivityDTO> activitys = userActivityService.findActivitiesByUserPid(userPid);
		return new ResponseEntity<>(activitys, HttpStatus.OK);
	}

	@RequestMapping(value = "/document-print/getDocuments/{activityPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DocumentPrintCheckDTO> getDocuments(@PathVariable String activityPid,@Valid @RequestParam String userPid) {
		log.debug("Web request to get documents by activity : {}", activityPid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AD_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by activityPid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityDocument> activityDocuments = activityDocumentRepository.findByActivityPid(activityPid);
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
		List<Document> documents = activityDocuments.stream().map(a -> a.getDocument()).collect(Collectors.toList());
		List<DocumentDTO> allactivityDocuments = documents.stream().map(DocumentDTO::new).collect(Collectors.toList());
		List<DocumentDTO> trueActivityDocuments = documentPrintService.findAllDocumentsByActivityPidAndUserPid(activityPid, userPid);
		DocumentPrintCheckDTO checkDTO = new DocumentPrintCheckDTO(allactivityDocuments, trueActivityDocuments);
		return new ResponseEntity<>(checkDTO, HttpStatus.OK);
	}
}
