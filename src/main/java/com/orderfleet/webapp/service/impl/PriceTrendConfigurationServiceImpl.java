package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PriceTrendConfiguration;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceTrendConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;

/**
 * Service Implementation for managing PriceTrendConfiguration.
 * 
 * @author Muhammed Riyas T
 * @since Sep 03, 2016
 */
@Service
@Transactional
public class PriceTrendConfigurationServiceImpl implements PriceTrendConfigurationService {

	private final Logger log = LoggerFactory.getLogger(PriceTrendConfigurationServiceImpl.class);

	@Inject
	private PriceTrendConfigurationRepository priceTrendConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(List<PriceTrendConfigurationDTO> priceTrendConfigurationDTOs) {

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<PriceTrendConfiguration> priceTrendConfigurations = new ArrayList<>();
		for (PriceTrendConfigurationDTO priceTrendConfigurationDTO : priceTrendConfigurationDTOs) {
			PriceTrendConfiguration priceTrendConfiguration = new PriceTrendConfiguration();
			priceTrendConfiguration.setName(priceTrendConfigurationDTO.getName());
			priceTrendConfiguration.setValue(priceTrendConfigurationDTO.getValue());
			priceTrendConfiguration.setCompany(company);
			priceTrendConfigurations.add(priceTrendConfiguration);
		}
		priceTrendConfigurationRepository.deleteByCompanyId(company.getId());
		priceTrendConfigurationRepository.save(priceTrendConfigurations);
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PriceTrendConfigurationDTO> findAllByCompany() {
		log.debug("Request to get all PriceTrendConfiguration");
		List<PriceTrendConfiguration> priceTrendConfigurations = priceTrendConfigurationRepository.findAllByCompanyId();
		List<PriceTrendConfigurationDTO> result = priceTrendConfigurations.stream().map(PriceTrendConfigurationDTO::new)
				.collect(Collectors.toList());
		return result;
	}
	
	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PriceTrendConfigurationDTO> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		log.debug("Request to get all PriceTrendConfiguration");
		List<PriceTrendConfiguration> priceTrendConfigurations = priceTrendConfigurationRepository.findAllByCompanyAndLastModifiedDate(lastModifiedDate);
		List<PriceTrendConfigurationDTO> result = priceTrendConfigurations.stream().map(PriceTrendConfigurationDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
