package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyInvoiceConfiguration;
import com.orderfleet.webapp.repository.CompanyInvoiceConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.service.CompanyInvoiceConfigurationService;
import com.orderfleet.webapp.web.rest.dto.CompanyInvoiceConfigurationDTO;

/**
 * Service Implementation for managing CompanyInvoiceConfiguration.
 *
 * @author Sarath
 * @since Apr 13, 2018
 *
 */
@Transactional
@Service
public class CompanyInvoiceConfigurationServiceImpl implements CompanyInvoiceConfigurationService {

	private final Logger log = LoggerFactory.getLogger(CompanyInvoiceConfigurationServiceImpl.class);

	@Inject
	private CompanyInvoiceConfigurationRepository companyInvoiceConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a CompanyInvoiceConfiguration.
	 * 
	 * @param CompanyInvoiceConfigurationDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public CompanyInvoiceConfigurationDTO save(CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO) {
		log.debug("Request to save Company Invoice Configuration : {}", companyInvoiceConfigurationDTO);

		Optional<Company> opCompany = companyRepository.findOneByPid(companyInvoiceConfigurationDTO.getCompanyPid());

		if (opCompany.isPresent()) {
			CompanyInvoiceConfiguration companyInvoiceConfiguration = new CompanyInvoiceConfiguration();

			companyInvoiceConfiguration.setAddress(companyInvoiceConfigurationDTO.getAddress());
			companyInvoiceConfiguration.setAmountPerUser(companyInvoiceConfigurationDTO.getAmountPerUser());
			companyInvoiceConfiguration.setCompany(opCompany.get());

			companyInvoiceConfiguration = companyInvoiceConfigurationRepository.save(companyInvoiceConfiguration);
			CompanyInvoiceConfigurationDTO result = new CompanyInvoiceConfigurationDTO(companyInvoiceConfiguration);
			return result;
		}
		return null;

	}

	@Override
	public CompanyInvoiceConfigurationDTO update(CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO) {
		log.debug("Request to Update Company Invoice Configuration : {}", companyInvoiceConfigurationDTO);
		return companyInvoiceConfigurationRepository.findOneById(companyInvoiceConfigurationDTO.getId())
				.map(companyInvoiceConfiguration -> {
					companyInvoiceConfiguration.setAddress(companyInvoiceConfigurationDTO.getAddress());
					companyInvoiceConfiguration.setAmountPerUser(companyInvoiceConfigurationDTO.getAmountPerUser());

					companyInvoiceConfiguration = companyInvoiceConfigurationRepository
							.save(companyInvoiceConfiguration);
					CompanyInvoiceConfigurationDTO result = new CompanyInvoiceConfigurationDTO(
							companyInvoiceConfiguration);
					return result;
				}).orElse(null);
	}

	@Override
	public Optional<CompanyInvoiceConfigurationDTO> findOneByCompanyPid(String companyPid) {
		log.debug("Web request to get ProductProfile by pid : {}", companyPid);
		return companyInvoiceConfigurationRepository.findOneByCompanyPid(companyPid).map(companyInvoiceConfig -> {
			CompanyInvoiceConfigurationDTO result = new CompanyInvoiceConfigurationDTO(companyInvoiceConfig);
			return result;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<CompanyInvoiceConfigurationDTO> findAll() {
		log.debug("Request to get all RealtimeAPIs");
		List<CompanyInvoiceConfiguration> companyInvoiceConfiguration = companyInvoiceConfigurationRepository.findAll();
		List<CompanyInvoiceConfigurationDTO> result = companyInvoiceConfiguration.stream()
				.map(CompanyInvoiceConfigurationDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CompanyInvoiceConfigurationDTO> findOneById(Long id) {
		log.debug("Request to get Company Invoice Configuration by id : {}", id);
		return companyInvoiceConfigurationRepository.findOneById(id).map(companyInvoiceConfiguration -> {
			CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO = new CompanyInvoiceConfigurationDTO(
					companyInvoiceConfiguration);
			return companyInvoiceConfigurationDTO;
		});
	}

	@Override
	public void delete(Long id) {
		log.debug("Request to delete Company Invoice Configuration : {}", id);
		companyInvoiceConfigurationRepository.findOneById(id).ifPresent(companyInvoiceConfiguration -> {
			companyInvoiceConfigurationRepository.delete(companyInvoiceConfiguration.getId());
		});
	}

}
