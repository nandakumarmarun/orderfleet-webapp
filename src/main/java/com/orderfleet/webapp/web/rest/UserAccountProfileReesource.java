package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.UserAccountProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.UserAccountProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing UserAccountProfile.
 *
 * @author Sarath
 * @since Oct 24, 2016
 */

@Controller
@RequestMapping("/web")
public class UserAccountProfileReesource {

	private final Logger log = LoggerFactory.getLogger(UserAccountProfileReesource.class);

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private UserService userService;

	@Inject
	private UserAccountProfileService userAccountProfileService;

	@RequestMapping(value = "/user-account-profile", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserAccountProfile(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Employee");
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("accountProfiles", accountProfileService.findAllByCompany());
		return "company/userAccountProfile";
	}

	/**
	 * * GET /user-account-profile/save : save employeeUser.
	 *
	 * @param userPid
	 * @param employeePid
	 * @return
	 */
	@RequestMapping(value = "/user-account-profile/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<?> save(@RequestParam String userPid, @RequestParam String accountProfilePid) {
		log.debug("REST request to save assigned  userPid", userPid);
		if (userAccountProfileService.findByAccountProfilePid(accountProfilePid).isPresent()) {
			return ResponseEntity
					.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("accountProfile", "nameexists",
									"account profile already in use")).body(null);
		}
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileService.findOneByUserPid(userPid);

		if (userAccountProfile.isPresent()) {
			userAccountProfileService.updateUserAccountProfiles(accountProfilePid, userPid);
		} else {
			userAccountProfileService.saveUserAccountProfiles(accountProfilePid, userPid);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@RequestMapping(value = "/user-account-profile/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserAccountProfileDTO> getEmployeeUsers(@PathVariable String userPid) {
		log.debug("Web request to get by user pid : {}", userPid);
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileService.findOneByUserPid(userPid);
		UserAccountProfileDTO userAccountProfileDTO = new UserAccountProfileDTO();
		if (userAccountProfile.isPresent()) {
			userAccountProfileDTO = new UserAccountProfileDTO(userAccountProfile.get());
		}
		return new ResponseEntity<>(userAccountProfileDTO, HttpStatus.OK);
	}
}
