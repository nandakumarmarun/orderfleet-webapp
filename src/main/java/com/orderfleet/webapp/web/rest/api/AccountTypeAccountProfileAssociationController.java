package com.orderfleet.webapp.web.rest.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATA_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get associated accType by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<String> associatedAccountTypePids = accountTypeAssociationRepository
				.findAssociatedAccountTypeByPid(accountTypePid);
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
		 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "APA_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get associated account profile by Pid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<String> associatedAccountProfilePids = accountProfileAssociationRepository
				.findAssociatedAccountProfileByPid(accountProfilePid);
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "APA_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="delete by accountProfilePid and compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountProfileAssociationRepository.deleteByAccountProfilePidAndCompanyId(
				associatedAccountTypesAndAccountsDTO.getAccountProfilePid(), SecurityUtils.getCurrentUsersCompanyId());
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
	                DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id1 = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description1 ="get one by pid";
	        		LocalDateTime startLCTime1 = LocalDateTime.now();
	        		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
	        		String startDate1 = startLCTime1.format(DATE_FORMAT1);
	        		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<AccountProfile> opAccountProfile = accountProfileRepository
				.findOneByPid(associatedAccountTypesAndAccountsDTO.getAccountProfilePid());
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
		List<AccountProfile> associatedAccountProfiles = new ArrayList<>();

		if (!associatedAccountTypesAndAccountsDTO.getAssociatedAccountProfilePids().isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id11 = "AP_QUERY_139" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description11 ="get all by accProfilePids";
				LocalDateTime startLCTime11 = LocalDateTime.now();
				String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
				String startDate11 = startLCTime11.format(DATE_FORMAT11);
				logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			associatedAccountProfiles = accountProfileRepository.findAllByAccountProfilePids(
					associatedAccountTypesAndAccountsDTO.getAssociatedAccountProfilePids());
			 String flag11 = "Normal";
				LocalDateTime endLCTime11 = LocalDateTime.now();
				String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
				String endDate11 = startLCTime11.format(DATE_FORMAT11);
				Duration duration11 = Duration.between(startLCTime11, endLCTime11);
				long minutes11 = duration11.toMinutes();
				if (minutes11 <= 1 && minutes11 >= 0) {
					flag11 = "Fast";
				}
				if (minutes11 > 1 && minutes11 <= 2) {
					flag11 = "Normal";
				}
				if (minutes11 > 2 && minutes11 <= 10) {
					flag11 = "Slow";
				}
				if (minutes11 > 10) {
					flag11 = "Dead Slow";
				}
		                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
						+ description11);

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