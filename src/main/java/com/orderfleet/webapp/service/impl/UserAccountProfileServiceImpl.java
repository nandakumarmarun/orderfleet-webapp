package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserAccountProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.UserAccountProfileDTO;

/**
 * Service Implementation for managing UserAccountProfile.
 *
 * @author Sarath
 * @since Oct 24, 2016
 */
@Service
@Transactional
public class UserAccountProfileServiceImpl implements UserAccountProfileService {

	private final Logger log = LoggerFactory.getLogger(UserAccountProfileServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private UserAccountProfileRepository userAccountProfileRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public Page<UserAccountProfile> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UserAccountProfiles");
		Page<UserAccountProfile> UserAccountProfiles = userAccountProfileRepository.findAllByCompanyId(pageable);
		return UserAccountProfiles;
	}

	@Override
	public UserAccountProfileDTO saveUserAccountProfiles(String accountProfilePid, String userPid) {
		log.debug("Request to get all saveUserAccountProfiles");
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
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
		Optional<User> user = userRepository.findOneByPid(userPid);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		UserAccountProfile userAccountProfile = new UserAccountProfile();
		if (accountProfile.isPresent() && user.isPresent()) {
			userAccountProfile = new UserAccountProfile(accountProfile.get(), user.get(), company);
			userAccountProfileRepository.save(userAccountProfile);
		}
		UserAccountProfileDTO accountProfileDTO = new UserAccountProfileDTO(userAccountProfile);
		return accountProfileDTO;
	}

	@Override
	public Optional<UserAccountProfile> findByAccountProfilePid(String accountProfilePid) {
		log.debug("Request to find By AccountProfile Pid : {}", accountProfilePid);
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository
				.findByCompanyIdAndAccountProfilePid(SecurityUtils.getCurrentUsersCompanyId(), accountProfilePid);

		return userAccountProfile;
	}

	@Override
	public Optional<UserAccountProfile> findOneByUserPid(String userPid) {
		log.debug("Request to find OneBy UserPid : {}", userPid);
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository.findByCompanyIdAndUserPid(
				SecurityUtils.getCurrentUsersCompanyId(), userPid);
		return userAccountProfile;
	}

	@Override
	public UserAccountProfileDTO updateUserAccountProfiles(String accountProfilePid, String userPid) {
		log.debug("Request to Update UserAccountProfiles : {}", userPid);
		return userAccountProfileRepository
				.findByCompanyIdAndUserPid(SecurityUtils.getCurrentUsersCompanyId(), userPid)
				.map(userAccountProfile -> {
					 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String description ="get one by pid";
						LocalDateTime startLCTime = LocalDateTime.now();
						String startTime = startLCTime.format(DATE_TIME_FORMAT);
						String startDate = startLCTime.format(DATE_FORMAT);
						logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
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
					userAccountProfile.setAccountProfile(accountProfile.get());
					userAccountProfile = userAccountProfileRepository.save(userAccountProfile);
					UserAccountProfileDTO result = new UserAccountProfileDTO(userAccountProfile);
					return result;
				}).orElse(null);
	}

}
