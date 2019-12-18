package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * Controller for MobileMasterUpdateResource
 *
 * @author Prashob
 * @since Dec 18,2019
 */
@RequestMapping("/web")
@Controller
public class MobileMasterUpdateResource {

	private final Logger log = LoggerFactory.getLogger(MobileMasterUpdateResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private UserService userService;

	@GetMapping("/mobile-master-update")
	@Timed
	public String getMobileMasterUpdate(Model model) {
		model.addAttribute("companies", companyService.findAllCompanySortedByName());
		return "site_admin/mobile-master-update";
	}

	@RequestMapping(value = "/load-company-users/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDTO> getAllCompanyUsersByCompany(@Valid @PathVariable("companypid") String companypid)
			throws URISyntaxException {
		log.debug("Web request to Get Users by companyPid: {}", companypid);
		List<UserDTO> users = userService.findAllByCompanyPid(companypid);
		return users;
	}

}
