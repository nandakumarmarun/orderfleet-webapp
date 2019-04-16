package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.PriceLevelService;

import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing AccountProfile.
 * 
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
@Controller
@RequestMapping("/web")
public class AccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(AccountProfileResource.class);

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	/**
	 * POST /accountProfiles : Create a new accountProfile.
	 *
	 * @param accountProfileDTO
	 *            the accountProfileDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         accountProfileDTO, or with status 400 (Bad Request) if the
	 *         accountProfile has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/accountProfiles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<AccountProfileDTO> createAccountProfile(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) throws URISyntaxException {
		log.debug("Web request to save AccountProfile : {}", accountProfileDTO);
		if (accountProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfile", "idexists",
					"A new account profile cannot already have an ID")).body(null);
		}
		if (accountProfileService.findByName(accountProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}
		accountProfileDTO.setAccountStatus(AccountStatus.Verified);
		accountProfileDTO.setDataSourceType(DataSourceType.WEB);
		AccountProfileDTO result = accountProfileService.save(accountProfileDTO);
		return ResponseEntity.created(new URI("/web/accountProfiles/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("accountProfile", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /accountProfiles : Updates an existing accountProfile.
	 *
	 * @param accountProfileDTO
	 *            the accountProfileDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         accountProfileDTO, or with status 400 (Bad Request) if the
	 *         accountProfileDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the accountProfileDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/accountProfiles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> updateAccountProfile(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) {
		log.debug("Web request to update AccountProfile : {}", accountProfileDTO);
		if (accountProfileDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "idNotexists", "Account Profile must have an ID"))
					.body(null);
		}
		Optional<AccountProfileDTO> existingAccountProfile = accountProfileService
				.findByName(accountProfileDTO.getName());
		if (existingAccountProfile.isPresent()
				&& (!existingAccountProfile.get().getPid().equals(accountProfileDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}
		AccountProfileDTO result = accountProfileService.update(accountProfileDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "idNotexists", "Invalid Account Profile ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("accountProfile", accountProfileDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /accountProfiles : get all the accountProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         accountProfiles in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/accountProfiles", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Pageable pageable, Model model) {
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
		model.addAttribute("deactivatedAccountProfiles", accountProfileService.findAllByCompanyAndActivated(false));
		return "company/accountProfiles";
	}

	/**
	 * GET /accountProfiles/load :load accountProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/accountProfiles/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountProfileDTO>> loadAccountProfiles() {
		log.debug("Web request to load AccountProfiles* ");
		return new ResponseEntity<>(accountProfileService.findAllByCompanyAndActivated(true), HttpStatus.OK);
//		return new ResponseEntity<>(accountProfileService.findAllCustomByCompanyAndActivated(true), HttpStatus.OK);
	}

	/**
	 * GET /accountProfiles/:id : get the "id" accountProfile.
	 *
	 * @param id
	 *            the id of the accountProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/accountProfiles/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> getAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by pid : {}", pid);
		return accountProfileService.findOneByPid(pid)
				.map(accountProfileDTO -> new ResponseEntity<>(accountProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /accountProfiles/:pid : delete the "pid" accountProfile.
	 *
	 * @param pid
	 *            the pid of the accountProfileDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/accountProfiles/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteAccountProfile(@PathVariable String pid) {
		log.debug("REST request to delete AccountProfile : {}", pid);
		accountProfileService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("accountProfile", pid.toString()))
				.build();
	}

	/**
	 * GET /accountProfiles/user/:id : get the "id" accountProfile.
	 *
	 * @param id
	 *            the user's id of the accountProfileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountProfileDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/accountProfiles/user/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getUsersAccountProfile(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile by user pid : {}", pid);
		return new ResponseEntity<>(accountProfileService.findUsersAccountProfile(pid), HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 6, 2017
	 *
	 *        UPDATE STATUS /accountGroups/changeStatus :accountProfileDTO : update
	 *        status of accountProfile.
	 * 
	 * @param accountProfileDTO
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         accountProfileDTO
	 */
	@Timed
	@RequestMapping(value = "/accountProfiles/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesActivate(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) {
		log.debug("Web request to update AccountProfiles ", accountProfileDTO);
		AccountProfileDTO result = accountProfileService.updateAccountProfileStatus(accountProfileDTO.getPid(),
				accountProfileDTO.getActivated());

		return new ResponseEntity<>(result, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS /accountProfiles/activateAccountProfile : activate
	 *        status of accountProfile.
	 * 
	 * @param accountprofiles
	 *            the accountprofiles to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/accountProfiles/activateAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfileDeactivated(@Valid @RequestParam String accountprofiles) {
		log.debug("Web request to get Deactive AccountProfiles");
		String[] accountProfiles = accountprofiles.split(",");
		for (String accountprofile : accountProfiles) {
			accountProfileService.updateAccountProfileStatus(accountprofile, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/accountProfiles/filterByAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfilesByAccountTypes(
			@RequestParam String accountTypePids, @RequestParam String importedStatus) throws URISyntaxException {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		boolean imports;
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		// Not selected
		if (accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			if (userIds.isEmpty()) {
				//accountProfileDTOs = accountProfileService.findAllByCompanyAndActivated(true);
				accountProfileDTOs = accountProfileService.findAllByCompanyAndActivated(true);
			} else {
				accountProfileDTOs = locationAccountProfileService.findAccountProfilesByCurrentUserLocations();
			}
			
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}

		// Both selected
		if (!accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			if (importedStatus.equals("true")) {
				imports = true;
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(
							accountProfileService.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
									Arrays.asList(accountTypePids.split(",")), imports));
				} else {
					accountProfileDTOs.addAll(
							locationAccountProfileService.findAccountProfilesByCurrentUserLocationsAndAccountTypePidIn(
									Arrays.asList(accountTypePids.split(",")), imports));
				}
			} else if (importedStatus.equals("false")) {
				imports = false;
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(
							accountProfileService.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(
									Arrays.asList(accountTypePids.split(",")), imports));
				} else {
					accountProfileDTOs.addAll(
							locationAccountProfileService.findAccountProfilesByCurrentUserLocationsAndAccountTypePidIn(
									Arrays.asList(accountTypePids.split(",")), imports));
				}
			} else {
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService.findAccountProfileByAccountTypePidInAndActivated(
							Arrays.asList(accountTypePids.split(","))));
				} else {
					accountProfileDTOs
							.addAll(locationAccountProfileService.findAccountByCurrentUserLocationsAndAccountTypePidIn(
									Arrays.asList(accountTypePids.split(","))));
				}
			}

			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}

		// ImportStatus Selected
		if (accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			if (importedStatus.equals("true")) {
				imports = true;
				if (userIds.isEmpty()) {
					accountProfileDTOs
							.addAll(accountProfileService.findAllByCompanyAndAccountImportStatusAndActivated(imports));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountProfilesByCurrentUserLocationsAndImpotedStatus(imports));
				}
			} else if (importedStatus.equals("false")) {
				imports = false;
				if (userIds.isEmpty()) {
					accountProfileDTOs
							.addAll(accountProfileService.findAllByCompanyAndAccountImportStatusAndActivated(imports));
				} else {
					accountProfileDTOs.addAll(locationAccountProfileService
							.findAccountProfilesByCurrentUserLocationsAndImpotedStatus(imports));
				}
			} else {
				if (userIds.isEmpty()) {
					accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
				} else {
					accountProfileDTOs.addAll(
							locationAccountProfileService.findAccountByCurrentUserLocationsAndAllImpotedStatus());
				}
			}

			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}

		// AccountType Selected
		if (!accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			if (userIds.isEmpty()) {
				accountProfileDTOs.addAll(accountProfileService
						.findAccountProfileByAccountTypePidInAndActivated(Arrays.asList(accountTypePids.split(","))));
			} else {
				accountProfileDTOs
						.addAll(locationAccountProfileService.findAccountByCurrentUserLocationsAndAccountTypePidIn(
								Arrays.asList(accountTypePids.split(","))));
			}
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/accountProfiles/location/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationAccountProfileDTO>> getProductProfileLocations(@PathVariable String pid) {
		log.debug("Web request to get AccountProfile location by pid : {}", pid);
		return new ResponseEntity<>(locationAccountProfileService.findLocationByAccountProfilePid(pid), HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/accountProfiles/get-by-status-filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountProfileDTO>> getAccountProfilesByStatus(@RequestParam boolean active,
			@RequestParam boolean deactivate) {
		log.debug("Web request to get get activitys : {}");
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		if (active == true && deactivate == true) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
		} else if (active) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
		} else if (deactivate) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(false));
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}
	
	
	@Timed
	@RequestMapping(value = "/accountProfiles/verifyStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfilesVerifyStatus(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO) {
		log.debug("Web request to update AccountProfiles Verified Status ", accountProfileDTO);
		
		AccountProfileDTO result = accountProfileService.updateAccountProfileVerifiedStatus(
				accountProfileDTO.getPid(), 
				accountProfileDTO.getAccountStatus() == AccountStatus.Verified ? AccountStatus.Unverified : AccountStatus.Verified);

		return new ResponseEntity<>(result, HttpStatus.OK);

	}
}
