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
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentStockLocationSourceService;
import com.orderfleet.webapp.web.rest.dto.DocumentStockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.mapper.StockLocationMapper;

/**
 * Service Implementation for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class DocumentStockLocationSourceServiceImpl implements DocumentStockLocationSourceService {

	private final Logger log = LoggerFactory.getLogger(DocumentStockLocationSourceServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private StockLocationMapper stockLocationMapper;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Override
	public void save(String documentPid, String assignedStockLocations, String defaultStockLocationPid) {

		log.debug("Request to save Document Stock Location Source");

		Document document = documentRepository.findOneByPid(documentPid).get();
		String[] stockLocations = assignedStockLocations.split(",");

		List<DocumentStockLocationSource> documentStockLocationSource = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		for (String stockLocationPid : stockLocations) {
			boolean isDefault = false;
			if (stockLocationPid.equals(defaultStockLocationPid)) {
				isDefault = true;
			}
			StockLocation stockLocation = stockLocationRepository.findOneByPid(stockLocationPid).get();
			documentStockLocationSource
					.add(new DocumentStockLocationSource(document, stockLocation, company, isDefault));
		}
		documentStockLocationSourceRepository.deleteByDocumentPid(documentPid);
		documentStockLocationSourceRepository.save(documentStockLocationSource);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StockLocationDTO> findStockLocationsByDocumentPid(String documentPid) {
		log.debug("Request to get all Document Stock Location Source under in a documents");
		List<StockLocation> stockLocationList = documentStockLocationSourceRepository
				.findStockLocationByDocumentPid(documentPid);
		List<StockLocationDTO> result = stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocationList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentStockLocationDTO> findByDocumentPid(String documentPid) {
		log.debug("Request to get all Document Stock Location Source under in a document");
		List<DocumentStockLocationSource> documentStockLocationSources = documentStockLocationSourceRepository
				.findByDocumentPid(documentPid);
		List<DocumentStockLocationDTO> result = documentStockLocationSources.stream().map(DocumentStockLocationDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<DocumentStockLocationDTO> findDocumentStockLocationSourceByDocuments() {
		List<Document> documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		return documentStockLocationSourceRepository.findByDocumentIn(documents).stream().parallel()
				.map(ds -> new DocumentStockLocationDTO(ds)).collect(Collectors.toList());
	}

	@Override
	public StockLocationDTO findDefaultStockLocationByDocumentPid(String documentPid) {
		DocumentStockLocationSource destination = documentStockLocationSourceRepository
				.findByDocumentPidAndIsDefaultTrue(documentPid);
		if (destination != null) {
			return stockLocationMapper.stockLocationToStockLocationDTO(destination.getStockLocation());
		}
		return null;
	}

	@Override
	public List<DocumentStockLocationDTO> findAllByCompany() {
		List<DocumentStockLocationSource> documentStockLocationSources = documentStockLocationSourceRepository
				.findAllByCompanyId();
		List<DocumentStockLocationDTO> result = documentStockLocationSources.stream().map(DocumentStockLocationDTO::new)
				.collect(Collectors.toList());
		return result;
	}
	
	@Override
	public List<DocumentStockLocationDTO> findAllByCompanyIdAndlastModifiedDate(LocalDateTime lastModifiedDate) {
		List<DocumentStockLocationSource> documentStockLocationSources = documentStockLocationSourceRepository
				.findAllByCompanyIdAndlastModifiedDate(lastModifiedDate);
		List<DocumentStockLocationDTO> result = documentStockLocationSources.stream().map(DocumentStockLocationDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
