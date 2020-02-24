package com.orderfleet.webapp.web.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileAssociation;
import com.orderfleet.webapp.repository.AccountProfileAssociationRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeAssociationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AssociatedAccountTypesAndAccountsDTO;

/**
 * REST controller for managing the current user's account.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
@RestController
@RequestMapping("/api")
public class AccountTypeAccountProfileAssociationController {

	private final Logger log = LoggerFactory.getLogger(AccountTypeAccountProfileAssociationController.class);

	@Inject
	private AccountTypeAssociationRepository accountTypeAssociationRepository;

	@Inject
	private AccountProfileAssociationRepository accountProfileAssociationRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@RequestMapping(value = "/associatedAccountTypesAndAccounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AssociatedAccountTypesAndAccountsDTO> getAllUsers(@RequestParam String accountTypePid,
			@RequestParam String accountProfilePid) {

		
		log.debug("REST request to get  assigned associated AccountTypes And Accounts : {}",accountTypePid+"---"+accountProfilePid);
				
		AssociatedAccountTypesAndAccountsDTO associatedAccountTypesAndAccountsDTO = new AssociatedAccountTypesAndAccountsDTO();

		List<String> associatedAccountTypePids = accountTypeAssociationRepository
				.findAssociatedAccountTypeByPid(accountTypePid);
		List<String> associatedAccountProfilePids = accountProfileAssociationRepository
				.findAssociatedAccountProfileByPid(accountProfilePid);

		associatedAccountTypesAndAccountsDTO.setAccountProfilePid("");
		associatedAccountTypesAndAccountsDTO.setAssociatedAccountTypePids(associatedAccountTypePids);
		associatedAccountTypesAndAccountsDTO.setAssociatedAccountProfilePids(associatedAccountProfilePids);

		return new ResponseEntity<>(associatedAccountTypesAndAccountsDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/associatedAccountTypesAndAccounts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> validateLicenseKey(
			@RequestBody AssociatedAccountTypesAndAccountsDTO associatedAccountTypesAndAccountsDTO) {

		log.debug("REST request to save assigned account profiles : {}",
				associatedAccountTypesAndAccountsDTO.getAccountProfilePid());

		accountProfileAssociationRepository.deleteByAccountProfilePidAndCompanyId(
				associatedAccountTypesAndAccountsDTO.getAccountProfilePid(), SecurityUtils.getCurrentUsersCompanyId());

		Optional<AccountProfile> opAccountProfile = accountProfileRepository
				.findOneByPid(associatedAccountTypesAndAccountsDTO.getAccountProfilePid());

		List<AccountProfile> associatedAccountProfiles = new ArrayList<>();

		if (!associatedAccountTypesAndAccountsDTO.getAssociatedAccountProfilePids().isEmpty()) {
			associatedAccountProfiles = accountProfileRepository.findAllByAccountProfilePids(
					associatedAccountTypesAndAccountsDTO.getAssociatedAccountProfilePids());
		}

		for (String associatedAccountProfilePid : associatedAccountTypesAndAccountsDTO
				.getAssociatedAccountProfilePids()) {

			AccountProfileAssociation accountProfileAssociation = new AccountProfileAssociation();

			Optional<AccountProfile> opAssociatedAccountProfile = associatedAccountProfiles.stream()
					.filter(aap -> aap.getPid().equalsIgnoreCase(associatedAccountProfilePid)).findAny();

			if (opAccountProfile.isPresent() && opAssociatedAccountProfile.isPresent()) {
				accountProfileAssociation.setAccountProfile(opAccountProfile.get());
				accountProfileAssociation.setCompany(opAccountProfile.get().getCompany());
				accountProfileAssociation.setAssociatedAccountProfile(opAssociatedAccountProfile.get());
			}

			accountProfileAssociationRepository.save(accountProfileAssociation);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);

	}

}