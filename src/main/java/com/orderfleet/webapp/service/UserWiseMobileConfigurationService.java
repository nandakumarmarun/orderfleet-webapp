package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.UserWiseMobileConfiguration;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseMobileConfigurationDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing MobileConfiguration.
 *
 * @author Resmi T R
 * @since Nov 27, 2023
 *
 */
public interface UserWiseMobileConfigurationService {

	String PID_PREFIX = "UMBLCONF-";

	UserWiseMobileConfigurationDTO saveUserMobileConfiguration(UserWiseMobileConfigurationDTO mobileConfigurationDTO);

	Optional<UserWiseMobileConfigurationDTO> findOneByPid(String pid);

	UserWiseMobileConfigurationDTO findByCompanyId(Long companyId);

	Optional<UserWiseMobileConfigurationDTO> findOneByCompanyId(Long companyId);

	List<UserWiseMobileConfigurationDTO> findAll();

	UserWiseMobileConfigurationDTO updateMobileConfiguration(UserWiseMobileConfigurationDTO mobileConfigurationDTO);

	void deleteByPid(String pid);
	
	Optional<UserWiseMobileConfigurationDTO> findOneByCompanyId();

	Optional<UserWiseMobileConfigurationDTO> findOneByUserId(Long id);
}
