package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserDeviceDTO;

/**
 * Web controller for managing UserDevice.
 *
 * @author Sarath
 * @since Mar 7, 2018
 *
 */
@RequestMapping("/web")
@Controller
public class UserBuildVersionResource {

	private final Logger log = LoggerFactory.getLogger(UserBuildVersionResource.class);

	@Inject
	private UserDeviceService userDeviceService;

	@Inject
	private CompanyService companyService;

	@Inject
	private UserService userService;

	@GetMapping("/company-user-build-version")
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getUserDevicesFrom(Model model) {
		model.addAttribute("companies", companyService.findAllCompanySortedByName());
		return "site_admin/user-build-version";
	}

	@RequestMapping(value = "/company-user-build-version/listAllDevice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDeviceDTO> getAllUserDevices() throws URISyntaxException {
		log.debug("Web request to Get user device : {}");
		List<UserDeviceDTO> result = userDeviceService.findAllActivated(true);
		return result;
	}

	@RequestMapping(value = "/company-user-build-version/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDTO> getAllUsersByCompany(@Valid @PathVariable("companypid") String companypid)
			throws URISyntaxException {
		log.debug("Web request to Get Users by companyPid: {}", companypid);
		List<UserDTO> users = userService.findAllByCompanyPid(companypid);
		return users;
	}

	@RequestMapping(value = "/company-user-build-version/getAllCompanyUserDeviceBySort", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<UserDeviceDTO> getAllUserDevicesBySort(@RequestParam String companyPid, @RequestParam String userPid)
			throws URISyntaxException {
		log.debug("Web request to Get UserDevice : {}", companyPid);
		List<UserDeviceDTO> deviceDTOs = new ArrayList<UserDeviceDTO>();
		if (companyPid != null && userPid.equals("no")) {
			deviceDTOs = userDeviceService.findAllUserDeviceByCompanyPidAndActivated(companyPid, true);
		} else if (companyPid != null && userPid != null) {
			deviceDTOs = userDeviceService.findAllUserDeviceByCompanyPidAndUserPidAndActivated(companyPid, userPid,
					true);
		}
		return deviceDTOs;
	}
}
