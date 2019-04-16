package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentStockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Spring Data JPA repository for the DocumentStockLocationDestination entity.
 * 
 * @author Sarath
 * @since July 19 2016
 */
public interface DocumentStockLocationDestinationService {

	void save(String documentPid, String assignedStockLocations, String defaultStockLocationPid);

	List<StockLocationDTO> findStockLocationsByDocumentPid(String documentPid);

	List<DocumentStockLocationDTO> findByDocumentPid(String documentPid);

	List<DocumentStockLocationDTO> findDocumentStockLocationDestinationByDocuments();

	StockLocationDTO findDefaultStockLocationByDocumentPid(String documentPid);
	
	List<DocumentStockLocationDTO> findAllByCompany();
	
	List<DocumentStockLocationDTO> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate);
}
