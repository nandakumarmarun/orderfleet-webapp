package com.orderfleet.webapp.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.web.rest.dto.UserAccountProfileDTO;

/**
 * Service Interface for managing DashboardUser.
 *
 * @author Sarath
 * @since Oct 24, 2016
 */
public interface UserAccountProfileService {

	Page<UserAccountProfile> findAllByCompany(Pageable pageable);

	UserAccountProfileDTO saveUserAccountProfiles(String accountProfilePid, String userPid);

	Optional<UserAccountProfile> findByAccountProfilePid(String accountProfilePid);

	Optional<UserAccountProfile> findOneByUserPid(String userPid);

	UserAccountProfileDTO updateUserAccountProfiles(String accountProfilePid, String userPid);

}
