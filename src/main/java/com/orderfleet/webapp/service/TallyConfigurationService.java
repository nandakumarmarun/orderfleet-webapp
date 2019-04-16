package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;

public interface TallyConfigurationService {

	String PID_PREFIX = "TC-";
	
	TallyConfiguration save(TallyConfigurationDTO tallyConfigurationDTO);
	
	TallyConfiguration update(TallyConfigurationDTO tallyConfigurationDTO);
	
	Optional<TallyConfigurationDTO> findOneByPid(String pid);

	TallyConfigurationDTO findByCompanyId(Long companyId);

	Optional<TallyConfigurationDTO> findOneByCompanyId(Long companyId);

	List<TallyConfigurationDTO> findAll();

	TallyConfigurationDTO saveTallyConfiguration(TallyConfigurationDTO tallyConfigurationDTO);
	
	TallyConfigurationDTO updateTallyConfiguration(TallyConfigurationDTO tallyConfigurationDTO);

	void deleteByPid(String pid);
	
	Optional<TallyConfigurationDTO> findOneByCompanyId();
}
