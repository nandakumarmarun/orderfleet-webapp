package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private BestPerformanceConfigurationRepository bestPerformanceConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Override
	public void save(String assignedDocuments, DocumentType documentType, BestPerformanceType bestPerformanceType) {
		log.debug("Request to save  bestperformanceconfigurations");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BPC_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by compId and docType";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<BestPerformanceConfiguration> oldBestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findByCompanyIdAndDocumentType(documentType);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BPC_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get by compId and docType";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<BestPerformanceConfiguration> bestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findByCompanyIdAndDocumentType(documentType);
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

		List<BestPerformanceConfigurationDTO> result = bestPerformanceConfigurations.stream()
				.map(BestPerformanceConfigurationDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<BestPerformanceConfigurationDTO> findByCompanyId() {
		log.debug("Request to get  bestperformanceconfigurations by company");
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "BPC_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compId ";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<BestPerformanceConfiguration> bestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findByCompanyId();
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
		List<BestPerformanceConfigurationDTO> result = bestPerformanceConfigurations.stream()
				.map(BestPerformanceConfigurationDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<DocumentDTO> findAllDocumentsByBestPerformanceType(BestPerformanceType bestPerformanceType) {
		log.debug("Request to get  find All Documents By BestPerformanceType ", bestPerformanceType);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "BPC_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all doc by best performance type";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Document> documents = bestPerformanceConfigurationRepository
				.findAllDocumentsByBestPerformanceType(bestPerformanceType);
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

		List<DocumentDTO> result = documents.stream().map(DocumentDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public void deleteByBestPerformanceType(BestPerformanceType bestPerformanceType) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "BPC_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by best performance type";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<BestPerformanceConfiguration> oldBestPerformanceConfigurations = bestPerformanceConfigurationRepository
				.findAllByBestPerformanceType(bestPerformanceType);
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

		if (!oldBestPerformanceConfigurations.isEmpty() && oldBestPerformanceConfigurations.size() > 0) {
			bestPerformanceConfigurationRepository.delete(oldBestPerformanceConfigurations);
		}

	}

}
