package com.orderfleet.webapp.service.impl;

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
		Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
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
					Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByPid(accountProfilePid);
					userAccountProfile.setAccountProfile(accountProfile.get());
					userAccountProfile = userAccountProfileRepository.save(userAccountProfile);
					UserAccountProfileDTO result = new UserAccountProfileDTO(userAccountProfile);
					return result;
				}).orElse(null);
	}

}
