package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.DocumentPriceLevel;
import com.orderfleet.webapp.web.rest.dto.DocumentPriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

/**
 * Spring Data JPA repository for the DocumentPriceLevel entity.
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
public interface DocumentPriceLevelService {

	void save(String documentPid, String assignedPriceLevels);

	List<PriceLevelDTO> findPriceLevelByDocumentPid(String documentPid);

	List<DocumentPriceLevel> findAllByCompany();

	List<DocumentPriceLevelDTO> findByUserDocumentsIsCurrentUser();

	List<DocumentPriceLevelDTO> findByUserDocumentsIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);

}
