package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;

/**
 * Service Interface for managing MobileConfiguration.
 *
 * @author Sarath
 * @since Jul 17, 2017
 *
 */
public interface MobileConfigurationService {

	String PID_PREFIX = "MBLCONF-";

	Optional<MobileConfigurationDTO> findOneByPid(String pid);

	MobileConfigurationDTO findByCompanyId(Long companyId);

	Optional<MobileConfigurationDTO> findOneByCompanyId(Long companyId);

	List<MobileConfigurationDTO> findAll();

	MobileConfigurationDTO saveMobileConfiguration(MobileConfigurationDTO mobileConfigurationDTO);
	
	MobileConfigurationDTO updateMobileConfiguration(MobileConfigurationDTO mobileConfigurationDTO);

	void deleteByPid(String pid);
	
	Optional<MobileConfigurationDTO> findOneByCompanyId();
}
