package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileAssociation;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.AccountTypeAssociation;
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
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.AssociatedAccountTypesAndAccountsDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

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
	private AccountProfileService accountProfileService;

	@Inject
	private AccountTypeAssociationRepository accountTypeAssociationRepository;

	@Inject
	private AccountProfileAssociationRepository accountProfileAssociationRepository;

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
			@RequestParam String assignedAccountProfiles,@RequestParam String associatedAccountTypePid,@RequestParam String influencerAccountTypePid) {

		log.debug("REST request to save assigned account profiles : {}", pid);
		
		List<String> accountTypePids = new ArrayList<>();
		if (associatedAccountTypePid.equalsIgnoreCase("no")) {
			accountTypePids = accountTypeAssociationRepository.findAssociatedAccountTypeByPid(influencerAccountTypePid);
		} else {
			accountTypePids.add(associatedAccountTypePid);
		}
		
		accountProfileAssociationRepository.deleteByAccountProfilePidAndCompanyIdAndAssociatedAccountProfileAccountTypePidIn(pid,
				SecurityUtils.getCurrentUsersCompanyId(),accountTypePids);
		
		
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

}
