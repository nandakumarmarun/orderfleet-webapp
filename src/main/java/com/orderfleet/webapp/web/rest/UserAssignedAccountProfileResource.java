package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Web controller for managing InventoryVoucher.
 *
 * @author Sarath
 * @since Apr 21, 2017
 *
 */

@Controller
@RequestMapping("/web")
public class UserAssignedAccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(UserAssignedAccountProfileResource.class);

	@Inject
	private UserService userService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private EmployeeProfileLocationService employeeProfileLocationService;
	
	@Inject 
	private EmployeeProfileService employeeProfileService;

	@RequestMapping(value = "/user-assign-account-profile", method = RequestMethod.GET)
	@Timed
	public String getUserAssighnedAccountProfile(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of userAssignAccountProfiles");
		List<String> userPids = userService.findAllByCompanyId().stream().map(User::getPid).collect(Collectors.toList());
		model.addAttribute("users", employeeProfileService.findByEmployeeByUserPidIn(userPids));
		return "company/user-assign-account-profiles";
	}

	@RequestMapping(value = "/user-assign-account-profile/companyChange/{companyPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserDTO>> stateChange(@PathVariable String companyPid) {
		log.debug("Web request to get Company by CompanyPid : {}", companyPid);
		List<UserDTO> userDTOs = userService.findAllByCompanyPid(companyPid);
		return new ResponseEntity<>(userDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/user-assign-account-profile/getAccountProfiles/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getAccountProfiles(@PathVariable String userPid) {
		log.debug("Web request to get Company by userPid : {}", userPid);

		List<AccountProfileDTO> accountProfileDTOs=new ArrayList<>();
		List<LocationDTO> locationDTOs = employeeProfileLocationService.findLocationsByUserPid(userPid);
		if (locationDTOs.size() != 0) {
			List<String> locationPids = locationDTOs.stream().map(loc -> loc.getPid()).collect(Collectors.toList());
			accountProfileDTOs = locationAccountProfileService.findAccountProfileByLocationPidInAndActivated(locationPids,true);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

}
