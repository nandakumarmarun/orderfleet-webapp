package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.UserNotApplicableElementService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.UserNotApplicableElementDTO;

/**
 * Web controller for managing UserNotApplicableElement.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Controller
@RequestMapping("/web")
public class UserNotApplicableElementResource {

	private final Logger log = LoggerFactory.getLogger(UserNotApplicableElementResource.class);

	@Inject
	private UserNotApplicableElementService userNotApplicableElementService;

	@Inject
	private UserService userService;

	@Inject
	private DocumentService documentService;

	@Inject
	private DocumentFormsService documentFormsService;

	/**
	 * GET /userNotApplicableElements : get all the userNotApplicableElements.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userNotApplicableElements in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/userNotApplicableElements", method = RequestMethod.GET)
	public String getAllUserNotApplicableElements(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of UserNotApplicableElements");
		model.addAttribute("documentService", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
		model.addAttribute("users", userService.findAllByCompany());
		return "company/userNotApplicableElements";
	}

	@Timed
	@RequestMapping(value = "/userNotApplicableElements/loadForms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<FormDTO, List<FormElementDTO>>> getForms(@RequestParam String documentPid) {
		log.debug("REST request to get forms by userNotApplicableElementPid : {}", documentPid);
		Map<FormDTO, List<FormElementDTO>> result = documentFormsService.findFormFormElementByDocumentPid(documentPid);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/userNotApplicableElements/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserDTO>> getUsers(@RequestParam String documentPid, @RequestParam String formPid,
			@RequestParam String formElementPid) {
		log.debug("REST request to get users  : {}", formElementPid);
		List<UserDTO> result = userNotApplicableElementService.findUsersByDocumentAndFormAndFormElement(documentPid,
				formPid, formElementPid);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/userNotApplicableElements/users/{documentPid}", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedUsers(@PathVariable String documentPid,
			@RequestBody List<UserNotApplicableElementDTO> userNotApplicableElementDTOs) {
		log.debug("REST request to save assigned users }");
		userNotApplicableElementService.save(documentPid, userNotApplicableElementDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
