package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DocumentApproval;
import com.orderfleet.webapp.domain.DocumentApprovedUser;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.repository.DocumentApprovedUserRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentApprovalService;
import com.orderfleet.webapp.web.rest.dto.DocumentApprovalDTO;

/**
 * REST controller for managing DocumentApproval.
 * 
 * @author Muhammed Riyas T
 * @since November 23, 2016
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentApprovalController {

	private final Logger log = LoggerFactory.getLogger(DocumentApprovalController.class);

	@Inject
	private DocumentApprovalService documentApprovalService;

	@Inject
	private DocumentApprovedUserRepository documentApprovalUserTaskRepository;

	@Inject
	private UserRepository userRepository;

	@Timed
	@Transactional
	@RequestMapping(value = "/document-approval/approve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> documentApproval(@RequestParam String documentApprovalPid, @RequestParam String remarks,
			@RequestParam ApprovalStatus approvalStatus) throws URISyntaxException {
		log.debug("Web request to create document approval user task");
		DocumentApproval documentApproval = documentApprovalService.findOneByPid(documentApprovalPid);
		if (documentApproval != null) {

			DocumentApprovedUser documentApprovalUserTask = new DocumentApprovedUser();
			documentApprovalUserTask.setDocumentApproval(documentApproval);
			documentApprovalUserTask.setApprovalStatus(approvalStatus);
			documentApprovalUserTask.setRemarks(remarks);
			documentApprovalUserTask.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
			documentApprovalUserTaskRepository.save(documentApprovalUserTask);

			if (approvalStatus.equals(ApprovalStatus.REJECTED)) {
				documentApproval.setCompleted(true);
				documentApproval.setApprovalStatus(approvalStatus);
				documentApprovalService.updateDocumentApproval(documentApproval);
			} else {
				Long approvedCount = documentApprovalUserTaskRepository.countByDocumentApprovalPid(documentApprovalPid);
				if (approvedCount != null
						&& approvedCount == documentApproval.getDocumentApprovalLevel().getApproverCount()) {
					documentApproval.setCompleted(true);
					documentApproval.setApprovalStatus(approvalStatus);
					documentApprovalService.updateDocumentApproval(documentApproval);
				}
			}
		} else {
			log.debug("Invalid document approval pid");
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/document-approval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentApprovalDTO>> getDocumentApproval() throws URISyntaxException {
		log.debug("Web request to create document approval");
		List<DocumentApprovalDTO> result = documentApprovalService.findDocumentApprovalsByCurrentUser();
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
