package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.BestPerformanceConfiguration;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.BestPerformanceConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BestPerformanceConfigurationService;
import com.orderfleet.webapp.web.rest.dto.BestPerformanceConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Implimentation for BestPerformance Configuration.
 *
 * @author Sarath
 * @since Mar 27, 2018
 *
 */
@Service
@Transactional
public class BestPerformanceConfigurationServiceImpl implements BestPerformanceConfigurationService {

	private final Logger log = LoggerFactory.getLogger(BestPerformanceConfigurationServiceImpl.class);

	@Inject
	private BestPerformanceConfigurationRepository bestPerformanceConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Override
	public void save(String assignedDocuments, DocumentType documentType, BestPerformanceType bestPerformanceType) {
		log.debug("Request to save  bestperformanceconfigurations");

		List<BestPerformanceConfiguration> oldBestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findByCompanyIdAndDocumentType(documentType);

		if (!oldBestPerformanceConfigurations.isEmpty() && oldBestPerformanceConfigurations.size() > 0) {
			bestPerformanceConfigurationRepository.delete(oldBestPerformanceConfigurations);
		}

		List<BestPerformanceConfiguration> bestPerformanceConfigurations = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] docPids = assignedDocuments.split(",");
		for (String docPid : docPids) {
			Optional<Document> opDocument = documentRepository.findOneByPid(docPid);
			if (opDocument.isPresent()) {
				BestPerformanceConfiguration bestPerformanceConfiguration = new BestPerformanceConfiguration();
				bestPerformanceConfiguration.setBestPerformanceType(bestPerformanceType);
				bestPerformanceConfiguration.setCompany(company);
				bestPerformanceConfiguration.setDocument(opDocument.get());
				bestPerformanceConfiguration.setDocumentType(documentType);
				bestPerformanceConfigurations.add(bestPerformanceConfiguration);
			}
		}
		if (!bestPerformanceConfigurations.isEmpty()) {
			bestPerformanceConfigurationRepository.save(bestPerformanceConfigurations);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<BestPerformanceConfigurationDTO> findByCompanyIdAndVoucherType(DocumentType documentType) {
		log.debug("Request to get  bestperformanceconfigurations by voucherType", documentType);
		List<BestPerformanceConfiguration> bestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findByCompanyIdAndDocumentType(documentType);
		List<BestPerformanceConfigurationDTO> result = bestPerformanceConfigurations.stream()
				.map(BestPerformanceConfigurationDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BestPerformanceConfigurationDTO> findByCompanyId() {
		log.debug("Request to get  bestperformanceconfigurations by company");
		List<BestPerformanceConfiguration> bestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findByCompanyId();
		List<BestPerformanceConfigurationDTO> result = bestPerformanceConfigurations.stream()
				.map(BestPerformanceConfigurationDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<DocumentDTO> findAllDocumentsByBestPerformanceType(BestPerformanceType bestPerformanceType) {
		log.debug("Request to get  find All Documents By BestPerformanceType ", bestPerformanceType);
		List<Document> documents = bestPerformanceConfigurationRepository
				.findAllDocumentsByBestPerformanceType(bestPerformanceType);
		List<DocumentDTO> result = documents.stream().map(DocumentDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public void deleteByBestPerformanceType(BestPerformanceType bestPerformanceType) {
		List<BestPerformanceConfiguration> oldBestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findAllByBestPerformanceType(bestPerformanceType);
		if (!oldBestPerformanceConfigurations.isEmpty() && oldBestPerformanceConfigurations.size() > 0) {
			bestPerformanceConfigurationRepository.delete(oldBestPerformanceConfigurations);
		}

	}

}
