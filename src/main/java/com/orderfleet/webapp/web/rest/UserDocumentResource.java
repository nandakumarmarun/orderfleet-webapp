package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserDocumentDTO;

@Controller
@RequestMapping("/web")
public class UserDocumentResource {

	private final Logger log = LoggerFactory.getLogger(UserDocumentResource.class);

	@Inject
	private UserService userService;

	@Inject
	private DocumentService documentService;

	@Inject
	private UserDocumentService userDocumentService;

	@RequestMapping(value = "/user-documents", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Documents");
		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		model.addAttribute("documents", documentService.findAllByCompany());
		return "company/userDocuments";
	}

	@RequestMapping(value = "/user-documents/save/{userPid}", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@PathVariable String userPid,
			@RequestBody List<UserDocumentDTO> assignedDocuments) {
		log.debug("Web request to save a list of User Documents");
		userDocumentService.save(userPid, assignedDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-documents/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserDocumentDTO>> getUserDocuments(@PathVariable String userPid) {
		log.debug("Web request to get UserDocuments by userPid : {}", userPid);
		return new ResponseEntity<>(userDocumentService.findByUserPid(userPid), HttpStatus.OK);
	}
}