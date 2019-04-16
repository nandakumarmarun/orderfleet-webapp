package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
import com.orderfleet.webapp.service.DocumentApprovalLevelService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.DocumentApprovalLevelDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing DocumentApprovalLevel.
 * 
 * @author Muhammed Riyas T
 * @since November 19, 2016
 */
@Controller
@RequestMapping("/web")
public class DocumentApprovalLevelResource {

	private final Logger log = LoggerFactory.getLogger(DocumentApprovalLevelResource.class);

	@Inject
	private DocumentApprovalLevelService documentApprovalLevelService;

	@Inject
	private DocumentService documentService;

	@Inject
	private UserService userService;

	/**
	 * POST /documentApprovalLevels : Create a new documentApprovalLevel.
	 *
	 * @param documentApprovalLevelDTO
	 *            the documentApprovalLevelDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new documentApprovalLevelDTO, or with status 400 (Bad Request) if
	 *         the documentApprovalLevel has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/documentApprovalLevels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DocumentApprovalLevelDTO> createDocumentApprovalLevel(
			@Valid @RequestBody DocumentApprovalLevelDTO documentApprovalLevelDTO) throws URISyntaxException {
		log.debug("Web request to save DocumentApprovalLevel : {}", documentApprovalLevelDTO);
		if (documentApprovalLevelDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentApprovalLevel",
					"idexists", "A new documentApprovalLevel cannot already have an ID")).body(null);
		}
		if (documentApprovalLevelService.findByName(documentApprovalLevelDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("documentApprovalLevel", "nameexists", "Level name already in use"))
					.body(null);
		}
		DocumentApprovalLevelDTO result = documentApprovalLevelService.save(documentApprovalLevelDTO);
		return ResponseEntity.created(new URI("/web/documentApprovalLevels/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("documentApprovalLevel", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /documentApprovalLevels : Updates an existing documentApprovalLevel.
	 *
	 * @param documentApprovalLevelDTO
	 *            the documentApprovalLevelDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         documentApprovalLevelDTO, or with status 400 (Bad Request) if the
	 *         documentApprovalLevelDTO is not valid, or with status 500
	 *         (Internal Server Error) if the documentApprovalLevelDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/documentApprovalLevels", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DocumentApprovalLevelDTO> updateDocumentApprovalLevel(
			@Valid @RequestBody DocumentApprovalLevelDTO documentApprovalLevelDTO) throws URISyntaxException {
		log.debug("Web request to update DocumentApprovalLevel : {}", documentApprovalLevelDTO);
		if (documentApprovalLevelDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentApprovalLevel",
					"idNotexists", "DocumentApprovalLevel must have an ID")).body(null);
		}
		Optional<DocumentApprovalLevelDTO> existingDocumentApprovalLevel = documentApprovalLevelService
				.findByName(documentApprovalLevelDTO.getName());
		if (existingDocumentApprovalLevel.isPresent()
				&& (!existingDocumentApprovalLevel.get().getPid().equals(documentApprovalLevelDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentApprovalLevel",
					"nameexists", "DocumentApprovalLevel already in use")).body(null);
		}
		DocumentApprovalLevelDTO result = documentApprovalLevelService.update(documentApprovalLevelDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentApprovalLevel",
					"idNotexists", "Invalid DocumentApprovalLevel ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("documentApprovalLevel",
				documentApprovalLevelDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /documentApprovalLevels : get all the documentApprovalLevels.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         documentApprovalLevels in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/documentApprovalLevels", method = RequestMethod.GET)
	public String getAllDocumentApprovalLevels(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of DocumentApprovalLevels");
		model.addAttribute("documentApprovalLevel", documentApprovalLevelService.findAllByCompany());
		model.addAttribute("documents", documentService.findAllByCompany());
		model.addAttribute("users", userService.findAllByCompany());
		return "company/documentApprovalLevels";
	}

	/**
	 * GET /documentApprovalLevels/:id : get the "id" documentApprovalLevel.
	 *
	 * @param id
	 *            the id of the documentApprovalLevelDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         documentApprovalLevelDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/documentApprovalLevels/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DocumentApprovalLevelDTO> getDocumentApprovalLevel(@PathVariable String pid) {
		log.debug("Web request to get DocumentApprovalLevel by pid : {}", pid);
		return documentApprovalLevelService.findOneByPid(pid)
				.map(documentApprovalLevelDTO -> new ResponseEntity<>(documentApprovalLevelDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /documentApprovalLevels/:id : delete the "id"
	 * documentApprovalLevel.
	 *
	 * @param id
	 *            the id of the documentApprovalLevelDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/documentApprovalLevels/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDocumentApprovalLevel(@PathVariable String pid) {
		log.debug("REST request to delete DocumentApprovalLevel : {}", pid);
		documentApprovalLevelService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("documentApprovalLevel", pid.toString())).build();
	}

	@RequestMapping(value = "/documentApprovalLevels/assignUsers", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedUsers(@RequestParam String pid, @RequestParam String assignedUsers) {
		log.debug("REST request to save assigned Users : {}", pid);
		documentApprovalLevelService.saveAssignedUsers(pid, assignedUsers);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-approval-levels-order", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentStockLocationDestinationAssignmentOrder(Pageable pageable, Model model)
			throws URISyntaxException {
		log.debug("Web request to get a page of Document Stock Location Destination Assignment Order");
		List<DocumentDTO> documents = documentService.findAllByCompany();
		model.addAttribute("documents", documents);
		return "company/documentApprovalLevelOrder";
	}

	@Timed
	@RequestMapping(value = "/document-approval-levels-order/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentApprovalLevelDTO>> getDocumentApprovalLevelOrder(@PathVariable String pid) {
		log.debug("Web request to get Document Approval Level  by document pid : {}", pid);
		List<DocumentApprovalLevelDTO> approvalLevelDTOs = documentApprovalLevelService.findAllByDocumentPid(pid);
		return new ResponseEntity<>(approvalLevelDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/document-approval-levels-order/saveOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentApprovalLevelDTO>> SaveDocumentApprovalLevelOrder(
			@RequestBody DocumentApprovalLevelDTO[] documentApprovalLevelDTOs) {
		log.debug("Web request to save Document Approval Level  by document order : {}",
				documentApprovalLevelDTOs.length);
		for (DocumentApprovalLevelDTO documentApprovalLevelDTO : documentApprovalLevelDTOs) {
			Optional<DocumentApprovalLevelDTO> approvalLevelDTO = documentApprovalLevelService
					.findOneByPid(documentApprovalLevelDTO.getPid());
			if (approvalLevelDTO.isPresent()) {
				approvalLevelDTO.get().setApprovalOrder(documentApprovalLevelDTO.getApprovalOrder());
				documentApprovalLevelService.saveApprovalOrder(approvalLevelDTO.get());
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
