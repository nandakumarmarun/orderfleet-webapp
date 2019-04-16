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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPriceLevel;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentPriceLevelRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentPriceLevelService;
import com.orderfleet.webapp.web.rest.dto.DocumentPriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceLevelMapper;

/**
 * Service Implementation for managing DocumentPriceLevel.
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
@Service
@Transactional
public class DocumentPriceLevelServiceImpl implements DocumentPriceLevelService {
	private final Logger log = LoggerFactory.getLogger(DocumentPriceLevelServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentPriceLevelRepository documentPriceLevelRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private PriceLevelMapper priceLevelMapper;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void save(String documentPid, String assignedPriceLevel) {
		log.debug("Request to save Document PriceLevel");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Document document = documentRepository.findOneByPid(documentPid).get();
		String[] priceLevels = assignedPriceLevel.split(",");
		List<DocumentPriceLevel> documentPriceLevel = new ArrayList<>();
		for (String priceLevelPid : priceLevels) {
			PriceLevel priceLevel = priceLevelRepository.findOneByPid(priceLevelPid).get();
			documentPriceLevel.add(new DocumentPriceLevel(document, priceLevel, company));
		}
		documentPriceLevelRepository.deleteByDocumentPid(documentPid);
		documentPriceLevelRepository.save(documentPriceLevel);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceLevelDTO> findPriceLevelByDocumentPid(String documentPid) {
		log.debug("Request to get all PriceLevel under in a documents");
		List<PriceLevel> priceLevelList = documentPriceLevelRepository.findPriceLevelByDocumentPid(documentPid);
		List<PriceLevelDTO> result = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevelList);
		return result;
	}

	/**
	 * Get all the priceLevels.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentPriceLevel> findAllByCompany() {
		log.debug("Request to get all PriceLevel");
		List<DocumentPriceLevel> documentPriceLevel = documentPriceLevelRepository.findAllByCompanyId();
		return documentPriceLevel;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentPriceLevelDTO> findByUserDocumentsIsCurrentUser() {
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		List<DocumentPriceLevel> documentPriceLevels = documentPriceLevelRepository.findByDocumentIn(documents);
		List<DocumentPriceLevelDTO> result = documentPriceLevels.stream().map(DocumentPriceLevelDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentPriceLevelDTO> findByUserDocumentsIsCurrentUserAndLastModifiedDate(
			LocalDateTime lastModifiedDate) {
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		List<DocumentPriceLevel> documentPriceLevels = documentPriceLevelRepository
				.findByDocumentInAndLastModifiedDate(documents, lastModifiedDate);
		List<DocumentPriceLevelDTO> result = documentPriceLevels.stream().map(DocumentPriceLevelDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
