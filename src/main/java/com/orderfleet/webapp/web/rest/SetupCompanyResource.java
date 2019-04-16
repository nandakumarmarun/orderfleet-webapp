package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.SetupCompanyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.CopyCompanyDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for set up and configuring company. This is for site admin
 * 
 * @author Shaheer
 * @since November 14, 2016
 */
@Controller
@RequestMapping("/web")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class SetupCompanyResource {

	private final Logger log = LoggerFactory.getLogger(SetupCompanyResource.class);

	private SetupCompanyService setupCompanyService;

	private CompanyService companyService;

	private UserService userService;

	private UserRepository userRepository;

	@Inject
	public SetupCompanyResource(SetupCompanyService setupCompanyService, CompanyService companyService,
			UserService userService, UserRepository userRepository) {
		super();
		this.setupCompanyService = setupCompanyService;
		this.companyService = companyService;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	/**
	 * GET /copy-company
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/copy-company", method = RequestMethod.GET)
	public String getAttendanceReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of attendance Report");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/copy-company";
	}

	/**
	 * POST /copy-company : Create new company and copy all data from a company
	 * to new company.
	 *
	 * @param userDTO
	 *            the current user information
	 * @return the ResponseEntity with status 200 (OK), or status 400 (Bad
	 *         Request) or 500 (Internal Server Error) if the company couldn't
	 *         be copied
	 */
	@Timed
	@Transactional
	@PostMapping("/copy-company")
	public ResponseEntity<?> copyCompanyAndData(@Valid @RequestBody CopyCompanyDTO copyCompanyDTO) {

		// check user name and email already exists
		for (UserDTO user : copyCompanyDTO.getUsers()) {
			if (userRepository.findOneByLogin(user.getLogin().toLowerCase()).isPresent()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("copyCompany", "userexists",
						"User name '" + user.getLogin() + "' already in use")).body(null);
			} else if (userRepository.findOneByEmail(user.getEmail()).isPresent()) {
				return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("copyCompany", "emailexists",
						"Email '" + user.getEmail() + "' already in use")).body(null);
			}
		}

		// save new company
		Company savedCompany = setupCompanyService.cloneCompany(copyCompanyDTO.getFromCompanyPid(),
				copyCompanyDTO.getLegalName(), copyCompanyDTO.getEmail());
		if (savedCompany == null) {
			new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// save new company users
		for (UserDTO user : copyCompanyDTO.getUsers()) {
			setupCompanyService.cloneUser(user.getPid(), user.getLogin(), user.getEmail(), savedCompany);
		}

		// copy product profiles, productgroup_productprofile
		setupCompanyService.cloneProductProfiles(copyCompanyDTO.getFromCompanyPid(), savedCompany);

		// copy account profiles
		setupCompanyService.cloneAccountProfiles(copyCompanyDTO.getFromCompanyPid(), savedCompany);

		// copy employee profiles
		setupCompanyService.cloneEmployeeProfilesAndLocations(copyCompanyDTO.getFromCompanyPid(), savedCompany);

		// copy documents
		setupCompanyService.cloneDocuments(copyCompanyDTO.getFromCompanyPid(), savedCompany);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/copy-company/load-users/{companyPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<UserDTO> getCompanyUsers(@PathVariable("companyPid") String companyPid) {
		return userService.findAllByCompanyPid(companyPid);
	}

}
