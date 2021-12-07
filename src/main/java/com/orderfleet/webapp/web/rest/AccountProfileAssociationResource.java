package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATA_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all associated accType by Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountTypeAssociation> accountTypeAssociations = accountTypeAssociationRepository
				.findAllAssociatedAccountTypeByAccountTypePid(accountTypePid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

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
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ATA_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get associated accType by Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			accountTypePids = accountTypeAssociationRepository.findAssociatedAccountTypeByPid(influencerAccountTypePid);
			String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		} else {
			accountTypePids.add(associatedAccountTypePid);
		}

		List<AccountProfileDTO> accountProfileDtos = new ArrayList<>();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "APA_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get associated account profile by Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<String> associatedAccountPids = accountProfileAssociationRepository
				.findAssociatedAccountProfileByPid(accountPid);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		if (loadAssociatedAccountsOnly) {
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "APA_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get all by accountProfilePid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<AccountProfileAssociation> accountProfileAssociations = accountProfileAssociationRepository
					.findAllAssociatedAccountProfileByAccountProfilePid(accountPid, accountTypePids);
			String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);
			Set<AccountProfileDTO> accountProfilesDtoSet = new LinkedHashSet<>();

			for (AccountProfileAssociation accountTypeAssociation : accountProfileAssociations) {

				accountProfilesDtoSet.add(accountProfileMapper
						.accountProfileToAccountProfileDTO(accountTypeAssociation.getAssociatedAccountProfile()));
			}
			accountProfileDtos = accountProfilesDtoSet.stream().collect(Collectors.toList());

		} else {
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ATA_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get associated accType by Pid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			accountProfileDtos = accountProfileService
					.findAccountProfileByAccountTypePidInAndActivated(accountTypePids);
			String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);

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
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountProfile> opAccountProfile = accountProfileRepository.findOneByPid(pid);
			  String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

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
