package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * Web controller for managing User DisconinuedStatus.
 *
 * @author Sarath
 * @since Jan 10, 2018
 *
 */

@Controller
@RequestMapping("/web")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class UserDisContinuedStatusChangeResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private UserService userService;

	@Inject
	private CompanyService companyService;

	@RequestMapping(value = "/user-discontinued-status", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserAccountProfiles(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Employee");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("users", userService.findAllByCompany());
		return "site_admin/user-discontinued-status";
	}

	@RequestMapping(value = "/user-discontinued-status/getCompanyUsers", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<UserDTO>> getCompanyUserAccountProfiles(@RequestParam String companyPid)
			throws URISyntaxException {
		log.debug("Web request to get Users by comapany");
		List<UserDTO> userDTOs = userService.findAllByCompanyPid(companyPid);
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}

	@PutMapping("/user-discontinued-status/{pid}")
	@Timed
	public ResponseEntity<ManagedUserDTO> updateUserDevicesFrom(@PathVariable("pid") String pid,
			@RequestParam boolean status) {
		ManagedUserDTO managedUserDTO = userService.changeDiscontinuedStatusUser(pid, status);
		return ResponseEntity.ok().body(managedUserDTO);
	}
}
