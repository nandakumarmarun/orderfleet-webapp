package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.UserFormService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserFormDTO;

/**
 * a userForm Resource.
 *
 * @author Sarath
 * @since Apr 19, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class UserFormResource {

	private final Logger log = LoggerFactory.getLogger(UserFormResource.class);

	@Inject
	private UserService userService;

	@Inject
	private UserFormService userFormService;

	@Inject
	private FormService formService;

	@RequestMapping(value = "/user-forms", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserForms(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Forms");
		Page<UserDTO> pageUser = userService.findAllByCompanyId(pageable);
		model.addAttribute("pageUser", pageUser);
		model.addAttribute("forms", formService.findAllByCompany());

		return "company/user-form";
	}

	@RequestMapping(value = "/user-forms/save/{userPid}", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@PathVariable String userPid, @RequestBody List<UserFormDTO> assignedUserForms) {
		log.debug("REST request to save assigned forms", userPid);
		userFormService.saveUserForm(userPid, assignedUserForms);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-forms/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserFormDTO>> getUserDocuments(@PathVariable String userPid) {
		log.debug("Web request to get get forms by user pid : {}", userPid);
		return new ResponseEntity<>(userFormService.findFormByUserPid(userPid), HttpStatus.OK);
	}
}
