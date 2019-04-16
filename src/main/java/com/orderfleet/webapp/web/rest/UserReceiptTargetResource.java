package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.UserReceiptTargetDocumentService;
import com.orderfleet.webapp.service.UserReceiptTargetService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserReceiptTargetDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing UserReceiptTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Controller
@RequestMapping("/web")
public class UserReceiptTargetResource {

	private final Logger log = LoggerFactory.getLogger(UserReceiptTargetResource.class);

	@Inject
	private UserReceiptTargetService userReceiptTargetService;

	@Inject
	private UserService userService;

	@Inject
	private DocumentService documentService;

	@Inject
	private UserReceiptTargetDocumentService userReceiptTargetDocumentService;

	/**
	 * POST /user-receipt-targets : Create a new userReceiptTarget.
	 *
	 * @param userReceiptTargetDTO
	 *            the userReceiptTargetDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new userReceiptTargetDTO, or with status 400 (Bad Request) if the
	 *         userReceiptTarget has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-receipt-targets", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UserReceiptTargetDTO> createUserReceiptTarget(
			@Valid @RequestBody UserReceiptTargetDTO userReceiptTargetDTO) throws URISyntaxException {
		log.debug("Web request to save UserReceiptTarget : {}", userReceiptTargetDTO);
		if (userReceiptTargetDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userReceiptTarget", "idexists",
					"A new User Receipt Target cannot already have an ID")).body(null);
		}
		List<UserReceiptTarget> overlappingRecords = userReceiptTargetService.findUserAndDateWiseDuplicate(
				userReceiptTargetDTO.getUserPid(), userReceiptTargetDTO.getStartDate(),
				userReceiptTargetDTO.getEndDate());
		if (!overlappingRecords.isEmpty()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userReceiptTarget", "dateAlreadyExists", "Date range already exist"))
					.body(null);
		}
		UserReceiptTargetDTO result = userReceiptTargetService.save(userReceiptTargetDTO);
		return ResponseEntity.created(new URI("/web/user-receipt-targets/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("userReceiptTarget", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /user-receipt-targets : Updates an existing userReceiptTarget.
	 *
	 * @param userReceiptTargetDTO
	 *            the userReceiptTargetDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         userReceiptTargetDTO, or with status 400 (Bad Request) if the
	 *         userReceiptTargetDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the userReceiptTargetDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/user-receipt-targets", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserReceiptTargetDTO> updateUserReceiptTarget(
			@Valid @RequestBody UserReceiptTargetDTO userReceiptTargetDTO) throws URISyntaxException {
		log.debug("Web request to update UserReceiptTarget : {}", userReceiptTargetDTO);
		if (userReceiptTargetDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userReceiptTarget", "idNotexists",
					"User Receipt Target must have an ID")).body(null);
		}
		List<UserReceiptTarget> overlappingRecords = userReceiptTargetService.findUserAndDateWiseDuplicate(
				userReceiptTargetDTO.getUserPid(), userReceiptTargetDTO.getStartDate(),
				userReceiptTargetDTO.getEndDate());
		if (!overlappingRecords.isEmpty()
				&& (!overlappingRecords.get(0).getPid().equals(userReceiptTargetDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userReceiptTarget", "dateAlreadyExists", "Date range already exist"))
					.body(null);
		}
		UserReceiptTargetDTO result = userReceiptTargetService.update(userReceiptTargetDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("userReceiptTarget", "idNotexists", "Invalid activityUserTarget ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("userReceiptTarget", userReceiptTargetDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /user-receipt-targets : get all the userReceiptTargets.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userReceiptTargets in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-receipt-targets", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllUserReceiptTargets(Model model) throws URISyntaxException {

		log.debug("Web request to get a page of UserReceiptTargets");
		model.addAttribute("userReceiptTargets", userReceiptTargetService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.ACCOUNTING_VOUCHER));
		return "company/userReceiptTargets";
	}

	/**
	 * GET /user-receipt-targets/:id : get the "id" userReceiptTarget.
	 *
	 * @param id
	 *            the id of the userReceiptTargetDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         userReceiptTargetDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/user-receipt-targets/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserReceiptTargetDTO> getUserReceiptTarget(@PathVariable String pid) {
		log.debug("Web request to get UserReceiptTarget by pid : {}", pid);
		return userReceiptTargetService.findOneByPid(pid)
				.map(userReceiptTargetDTO -> new ResponseEntity<>(userReceiptTargetDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /user-receipt-targets/:pid : delete the "pid" userReceiptTarget.
	 *
	 * @param pid
	 *            the pid of the userReceiptTargetDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/user-receipt-targets/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteUserReceiptTarget(@PathVariable String pid) {
		log.debug("REST request to delete UserReceiptTarget : {}", pid);
		userReceiptTargetService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("userReceiptTarget", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/user-receipt-targets/getDocuments/{receiptTargetPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getReceiptTargetDocuments(@PathVariable String receiptTargetPid) {
		log.debug("Web request to get get Documents by user pid : {}", receiptTargetPid);
		return new ResponseEntity<>(
				userReceiptTargetDocumentService.findReceiptTargetDocumentsByReceiptTargetPid(receiptTargetPid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/user-receipt-targets/saveDocuments", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userReceiptTargetId, @RequestParam String assignedDocuments) {
		log.debug("REST request to save assigned documents", userReceiptTargetId);
		userReceiptTargetDocumentService.save(userReceiptTargetId, assignedDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
