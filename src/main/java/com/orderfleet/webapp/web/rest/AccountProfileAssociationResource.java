package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountActivityTaskConfig;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileAssociation;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.AccountTypeAssociation;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.ReceiverSupplierType;
import com.orderfleet.webapp.repository.AccountActivityTaskConfigRepository;
import com.orderfleet.webapp.repository.AccountProfileAssociationRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeAssociationRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountActivityTaskConfigService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.UserMenuItemService;
import com.orderfleet.webapp.web.rest.dto.AccountActivityTaskConfigDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeAssociationDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.AssociatedAccountTypesAndAccountsDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing AccountType.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */

@Controller
@RequestMapping("/web")
public class AccountProfileAssociationResource {

	private final Logger log = LoggerFactory.getLogger(AccountProfileAssociationResource.class);

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private ActivityService activityService;

	@Inject
	private AccountActivityTaskConfigService activityAccountTypeConfigService;

	@Inject
	private AccountActivityTaskConfigRepository activityConfigRepository;

	@Inject
	private UserMenuItemService userMenuItemService;

	@Inject
	private AccountTypeAssociationRepository accountTypeAssociationRepository;

	@Inject
	private AccountProfileAssociationRepository accountProfileAssociationRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@RequestMapping(value = "/account-profile-influencer-association", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountTypes(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Account Profile Association");

		List<AccountTypeAssociation> accountTypeAssociations = accountTypeAssociationRepository.findAll();

		// List<AccountType> accountTypes=new ArrayList<>();

		Set<AccountType> accountTypes = new LinkedHashSet<>();

		for (AccountTypeAssociation accountTypeAssociation : accountTypeAssociations) {
			accountTypes.add(accountTypeAssociation.getAccountType());
		}

		List<AccountTypeDTO> accountTypeDTOs = new ArrayList<>();

		for (AccountType accountType : accountTypes) {
			accountTypeDTOs.add(new AccountTypeDTO(accountType));
		}

		model.addAttribute("accountTypes", accountTypeDTOs);

		return "company/accountProfileInfluencerAssociation";
	}

	@RequestMapping(value = "/account-profile-influencer-association/load-account-profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getAccountProfile(@RequestParam String accountTypePid) {
		log.debug("Web request to get Account Profile by Account Type pid : {}", accountTypePid);

		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountType(accountTypePid);

		accountProfileDTOs.sort((AccountProfileDTO a1, AccountProfileDTO a2) -> a1.getName().compareTo(a2.getName()));

		return new ResponseEntity<List<AccountProfileDTO>>(accountProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/account-profile-influencer-association/load-associated-account-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountTypeDTO>> getAssociatedAccountType(@RequestParam String accountTypePid) {
		log.debug("Web request to get Account Profile by Account Type pid : {}", accountTypePid);

		List<AccountTypeAssociation> accountTypeAssociations = accountTypeAssociationRepository
				.findAllAssociatedAccountTypeByAccountTypePid(accountTypePid);

		Set<AccountType> accountTypes = new LinkedHashSet<>();

		for (AccountTypeAssociation accountTypeAssociation : accountTypeAssociations) {
			accountTypes.add(accountTypeAssociation.getAssociatedAccountType());
		}

		List<AccountTypeDTO> accountTypeDTOs = new ArrayList<>();

		for (AccountType accountType : accountTypes) {
			accountTypeDTOs.add(new AccountTypeDTO(accountType));
		}

		return new ResponseEntity<List<AccountTypeDTO>>(accountTypeDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/account-profile-influencer-association/load-all-associated-account-profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AssociatedAccountTypesAndAccountsDTO> getAllAssociatedAccountProfiles(
			@RequestParam String accountPid, @RequestParam String associatedAccountTypePid,
			@RequestParam boolean loadAssociatedAccountsOnly, @RequestParam String influencerAccountTypePid) {
		log.debug("Web request to get All Associated Account Profile by Account Type pid : {}",
				accountPid + "--" + associatedAccountTypePid + "--" + loadAssociatedAccountsOnly);

		List<String> accountTypePids = new ArrayList<>();
		if (associatedAccountTypePid.equalsIgnoreCase("no")) {
			accountTypePids = accountTypeAssociationRepository.findAssociatedAccountTypeByPid(influencerAccountTypePid);
		} else {
			accountTypePids.add(associatedAccountTypePid);
		}

		List<AccountProfileDTO> accountProfileDtos = new ArrayList<>();

		List<String> associatedAccountPids = accountProfileAssociationRepository
				.findAssociatedAccountProfileByPid(accountPid);

		if (loadAssociatedAccountsOnly) {

			List<AccountProfileAssociation> accountProfileAssociations = accountProfileAssociationRepository
					.findAllAssociatedAccountProfileByAccountProfilePid(accountPid, accountTypePids);

			System.out.println(accountTypePids.size() + "-----------------------------------------");

			Set<AccountProfileDTO> accountProfilesDtoSet = new LinkedHashSet<>();

			for (AccountProfileAssociation accountTypeAssociation : accountProfileAssociations) {

				accountProfilesDtoSet.add(accountProfileMapper
						.accountProfileToAccountProfileDTO(accountTypeAssociation.getAssociatedAccountProfile()));
			}
			accountProfileDtos = accountProfilesDtoSet.stream().collect(Collectors.toList());

		} else {
			accountProfileDtos = accountProfileService
					.findAccountProfileByAccountTypePidInAndActivated(accountTypePids);
		}

		accountProfileDtos.sort((AccountProfileDTO a1, AccountProfileDTO a2) -> a1.getName().compareTo(a2.getName()));
		AssociatedAccountTypesAndAccountsDTO associatedAccountTypesAndAccountsDTO = new AssociatedAccountTypesAndAccountsDTO();
		associatedAccountTypesAndAccountsDTO.setAccountProfileDtos(accountProfileDtos);
		associatedAccountTypesAndAccountsDTO.setAssociatedAccountProfilePids(associatedAccountPids);

		return new ResponseEntity<AssociatedAccountTypesAndAccountsDTO>(associatedAccountTypesAndAccountsDTO,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/account-profile-influencer-association/assignAssociatedAccountProfiles", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(@RequestParam String pid,
			@RequestParam String assignedAccountProfiles) {

		log.debug("REST request to save assigned account types : {}", pid);
		accountProfileAssociationRepository.deleteByAccountProfilePidAndCompanyId(pid,
				SecurityUtils.getCurrentUsersCompanyId());
		String[] associatedAccountProfile = assignedAccountProfiles.split(",");
		for (String associatedAccountProfilePid : associatedAccountProfile) {
			String aappid = associatedAccountProfilePid.split("~")[0].toString();

			AccountProfileAssociation accountProfileAssociation = new AccountProfileAssociation();

			Optional<AccountProfile> opAccountProfile = accountProfileRepository.findOneByPid(pid);
			Optional<AccountProfile> opAssociatedAccountProfile = accountProfileRepository.findOneByPid(aappid);

			if (opAccountProfile.isPresent() && opAssociatedAccountProfile.isPresent()) {
				accountProfileAssociation.setAccountProfile(opAccountProfile.get());
				accountProfileAssociation.setCompany(opAccountProfile.get().getCompany());
				accountProfileAssociation.setAssociatedAccountProfile(opAssociatedAccountProfile.get());
			}

			accountProfileAssociationRepository.save(accountProfileAssociation);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

//	/**
//	 * POST /accountTypes : Create a new accountType.
//	 *
//	 * @param accountTypeDTO the accountTypeDTO to create
//	 * @return the ResponseEntity with status 201 (Created) and with body the new
//	 *         accountTypeDTO, or with status 400 (Bad Request) if the accountType
//	 *         has already an ID
//	 * @throws URISyntaxException if the Location URI syntax is incorrect
//	 */
//	@RequestMapping(value = "/accountTypes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	@Timed
//	public ResponseEntity<AccountTypeDTO> createAccountType(@Valid @RequestBody AccountTypeDTO accountTypeDTO)
//			throws URISyntaxException {
//		log.debug("Web request to save AccountType : {}", accountTypeDTO);
//		if (accountTypeDTO.getPid() != null) {
//			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountType", "idexists",
//					"A new accountType cannot already have an ID")).body(null);
//		}
//		if (accountTypeService.findByName(accountTypeDTO.getName()).isPresent()) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("accountType", "nameexists", "Account type already in use"))
//					.body(null);
//		}
//		accountTypeDTO.setActivated(true);
//		AccountTypeDTO result = accountTypeService.save(accountTypeDTO);
//		return ResponseEntity.created(new URI("/web/accountTypes/" + result.getPid()))
//				.headers(HeaderUtil.createEntityCreationAlert("accountType", result.getPid().toString())).body(result);
//	}
//
//	/**
//	 * PUT /accountTypes : Updates an existing accountType.
//	 *
//	 * @param accountTypeDTO the accountTypeDTO to update
//	 * @return the ResponseEntity with status 200 (OK) and with body the updated
//	 *         accountTypeDTO, or with status 400 (Bad Request) if the
//	 *         accountTypeDTO is not valid, or with status 500 (Internal Server
//	 *         Error) if the accountTypeDTO couldnt be updated
//	 * @throws URISyntaxException if the Location URI syntax is incorrect
//	 */
//	@RequestMapping(value = "/accountTypes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<AccountTypeDTO> updateAccountType(@Valid @RequestBody AccountTypeDTO accountTypeDTO)
//			throws URISyntaxException {
//		log.debug("Web request to update AccountType : {}", accountTypeDTO);
//		if (accountTypeDTO.getPid() == null) {
//			return ResponseEntity.badRequest()
//					.headers(
//							HeaderUtil.createFailureAlert("accountType", "idNotexists", "Account Type must have an ID"))
//					.body(null);
//		}
//		Optional<AccountTypeDTO> existingAccountType = accountTypeService.findByName(accountTypeDTO.getName());
//		if (existingAccountType.isPresent() && (!existingAccountType.get().getPid().equals(accountTypeDTO.getPid()))) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("accountType", "nameexists", "Account type already in use"))
//					.body(null);
//		}
//		AccountTypeDTO result = accountTypeService.update(accountTypeDTO);
//		if (result == null) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("accountType", "idNotexists", "Invalid account type ID"))
//					.body(null);
//		}
//		return ResponseEntity.ok()
//				.headers(HeaderUtil.createEntityUpdateAlert("accountType", accountTypeDTO.getPid().toString()))
//				.body(result);
//	}
//
//	/**
//	 * GET /accountTypes : get all the accountTypes.
//	 *
//	 * @param pageable the pagination information
//	 * @return the ResponseEntity with status 200 (OK) and the list of accountTypes
//	 *         in body
//	 * @throws URISyntaxException if there is an error to generate the pagination
//	 *                            HTTP headers
//	 */
//	@RequestMapping(value = "/accountTypes", method = RequestMethod.GET)
//	@Timed
//	@Transactional(readOnly = true)
//	public String getAllAccountTypes(Model model) throws URISyntaxException {
//		log.debug("Web request to get a page of AccountTypes");
//		model.addAttribute("accountTypes", accountTypeService.findAllCompanyAndAccountTypeActivated(true));
//		model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
//		model.addAttribute("deactivatedAccountTypes", accountTypeService.findAllCompanyAndAccountTypeActivated(false));
//		model.addAttribute("activities", activityService.findAllByCompany());
//		model.addAttribute("accountNameTypes", AccountNameType.values());
//		model.addAttribute("receiverSupplierTypes", ReceiverSupplierType.values());
//		model.addAttribute("menuItemLabel", userMenuItemService.findMenuItemLabelView("/web/accountTypes"));
//
//		return "company/accountTypes";
//	}
//
//	/**
//	 * GET /accountTypes/:id : get the "id" accountType.
//	 *
//	 * @param id the id of the accountTypeDTO to retrieve
//	 * @return the ResponseEntity with status 200 (OK) and with body the
//	 *         accountTypeDTO, or with status 404 (Not Found)
//	 */
//	@RequestMapping(value = "/accountTypes/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<AccountTypeDTO> getAccountType(@PathVariable String pid) {
//		log.debug("Web request to get AccountType by pid : {}", pid);
//		return accountTypeService.findOneByPid(pid)
//				.map(accountTypeDTO -> new ResponseEntity<>(accountTypeDTO, HttpStatus.OK))
//				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//	}
//
//	/**
//	 * DELETE /accountTypes/:id : delete the "id" accountType.
//	 *
//	 * @param id the id of the accountTypeDTO to delete
//	 * @return the ResponseEntity with status 200 (OK)
//	 */
//	@RequestMapping(value = "/accountTypes/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<Void> deleteAccountType(@PathVariable String pid) {
//		log.debug("REST request to delete AccountType : {}", pid);
//		accountTypeService.delete(pid);
//		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("accountType", pid.toString())).build();
//	}
//
//	@RequestMapping(value = "/accountTypes/assignAccounts", method = RequestMethod.POST)
//	@Timed
//	public ResponseEntity<Void> saveAssignedAccounts(@RequestParam String pid, @RequestParam String assignedAccounts) {
//		log.debug("REST request to save assigned account type : {}", pid);
//		String[] accounts = assignedAccounts.split(",");
//		for (String accountPid : accounts) {
//			AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountPid).get();
//			accountProfileDTO.setAccountTypePid(pid);
//			accountProfileService.update(accountProfileDTO);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/accountTypes/assignActivities", method = RequestMethod.POST)
//	@Timed
//	public ResponseEntity<Void> saveAssignedActivities(@RequestParam String pid,
//			@RequestParam String assignedActivities) {
//
//		log.debug("REST request to save assigned account type : {}", pid);
//		activityConfigRepository.deleteByAccountTypePidAndCompanyId(pid, SecurityUtils.getCurrentUsersCompanyId());
//		String[] activity = assignedActivities.split(",");
//		for (String activityPid : activity) {
//			String actypid = activityPid.split("~")[0].toString();
//			boolean assignNotification = activityPid.split("~")[1].toString().equals("true") ? true : false;
//			AccountActivityTaskConfigDTO activityAccountTaskDTO = new AccountActivityTaskConfigDTO();
//			activityAccountTaskDTO.setAccountTypePid(pid);
//			activityAccountTaskDTO.setActivityPid(actypid);
//			activityAccountTaskDTO.setAssignNotification(assignNotification);
//			activityAccountTypeConfigService.saveActivityAccountTypeConfig(activityAccountTaskDTO);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/accountTypes/assignAssociatedAccountTypes", method = RequestMethod.POST)
//	@Timed
//	public ResponseEntity<Void> saveAssignedAccountTypes(@RequestParam String pid,
//			@RequestParam String assignedAccountTypes) {
//
//		log.debug("REST request to save assigned account types : {}", pid);
//		accountTypeAssociationRepository.deleteByAccountTypePidAndCompanyId(pid,
//				SecurityUtils.getCurrentUsersCompanyId());
//		String[] associatedAccountType = assignedAccountTypes.split(",");
//		for (String associatedAccountTypePid : associatedAccountType) {
//			String aatpid = associatedAccountTypePid.split("~")[0].toString();
//
//			AccountTypeAssociation accountTypeAssociation = new AccountTypeAssociation();
//
//			Optional<AccountType> opAccountType = accountTypeRepository.findOneByPid(pid);
//			Optional<AccountType> opAssociatedAccountType = accountTypeRepository.findOneByPid(aatpid);
//
//			if (opAccountType.isPresent() && opAssociatedAccountType.isPresent()) {
//				accountTypeAssociation.setAccountType(opAccountType.get());
//				accountTypeAssociation.setCompany(opAccountType.get().getCompany());
//				accountTypeAssociation.setAssociatedAccountType(opAssociatedAccountType.get());
//			}
//
//			accountTypeAssociationRepository.save(accountTypeAssociation);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/accountTypes/findAccounts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountProfileDTO>> getAccounts(@PathVariable String pid) {
//		log.debug("REST request to get accounts by accountTypePid : {}", pid);
//		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByAccountType(pid);
//		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/accountTypes/findActivities/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountActivityTaskConfigDTO>> getActivities(@PathVariable String pid) {
//		log.debug("REST request to get accounts by accountTypePid : {}", pid);
//		List<Object[]> activityConfig = activityConfigRepository.findActivityPidByAccountTypePid(pid);
//		List<AccountActivityTaskConfigDTO> activityConfiglist = new ArrayList<>();
//
//		for (Object[] obj : activityConfig) {
//			AccountActivityTaskConfigDTO accActivityConfig = new AccountActivityTaskConfigDTO();
//			log.info(obj[0].toString() + "  ---- " + obj[1].toString());
//			accActivityConfig.setActivityPid(obj[0].toString());
//			accActivityConfig.setAssignNotification((boolean) obj[1]);
//			activityConfiglist.add(accActivityConfig);
//		}
//
//		return new ResponseEntity<>(activityConfiglist, HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/accountTypes/findAssociatedAccountTypes/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountTypeAssociationDTO>> getAssociatedAccountTypes(@PathVariable String pid) {
//		log.debug("REST request to get associated account types by accountTypePid : {}", pid);
//		// List<Object[]> activityConfig =
//		// activityConfigRepository.findActivityPidByAccountTypePid(pid);
//		List<String> associatedAccountTypes = accountTypeAssociationRepository.findAssociatedAccountTypeByPid(pid);
//
//		List<AccountTypeAssociationDTO> accountTypeAssociationDTOs = new ArrayList<>();
//
//		for (String associatedAccountType : associatedAccountTypes) {
//			AccountTypeAssociationDTO accountTypeAssociationDTO = new AccountTypeAssociationDTO();
//			log.info(associatedAccountType);
//			accountTypeAssociationDTO.setAssociatedAccountTypePid(associatedAccountType);
//
//			accountTypeAssociationDTOs.add(accountTypeAssociationDTO);
//		}
//
//		return new ResponseEntity<>(accountTypeAssociationDTOs, HttpStatus.OK);
//	}
//
//	/**
//	 * Update Status /accountTypes/changeStatus: Activate or Deactivate accountType.
//	 *
//	 * @param accountTypeDTO the accountTypeDTO to update
//	 * @return the ResponseEntity with status 200 (OK) and with body the
//	 *         accountTypeDTO
//	 */
//
//	@Timed
//	@RequestMapping(value = "/accountTypes/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<AccountTypeDTO> updateAccountTypeStatus(@Valid @RequestBody AccountTypeDTO accountTypeDTO) {
//		log.debug("REST request to update status of  accountType : {}", accountTypeDTO);
//		AccountTypeDTO res = accountTypeService.updateAccountTypeStatus(accountTypeDTO.getPid(),
//				accountTypeDTO.getActivated());
//		return new ResponseEntity<>(res, HttpStatus.OK);
//	}
//
//	/**
//	 * Update Status /accountTypes/activateAccountTypes: Activate accountType.
//	 *
//	 * @param accountTypeDTO the accountTypeDTO to update
//	 * @return the ResponseEntity with status 200 (OK) and with body the
//	 *         accountTypeDTO
//	 */
//
//	@Timed
//	@RequestMapping(value = "/accountTypes/activateAccountTypes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<AccountTypeDTO> updateActivateAccountTypeStatus(@Valid @RequestParam String accounttypes) {
//		log.debug("REST request to update status of  accountType : {}", accounttypes);
//		String[] accountTypes = accounttypes.split(",");
//		for (String accounttypePid : accountTypes) {
//			accountTypeService.updateAccountTypeStatus(accounttypePid, true);
//		}
//		return new ResponseEntity<>(HttpStatus.OK);
//	}
//
//	@RequestMapping(value = "/accountTypes/deactivatedAccounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountProfileDTO>> getDeActivatedAccounts() {
//		log.debug("REST request to get deactivated accounts ");
//		List<AccountProfileDTO> accountProfileDTOs = accountProfileService.findAllByCompanyAndActivated(false);
//		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
//	}
}
