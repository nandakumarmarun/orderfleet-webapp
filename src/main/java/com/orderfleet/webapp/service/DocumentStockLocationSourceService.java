package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentStockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Spring Data JPA repository for the DocumentStockLocationSource entity.
 * 
 * @author Sarath
 * @since July 19 2016
 */
public interface DocumentStockLocationSourceService {

	void save(String documentPid, String assignedStockLocations, String defaultStockLocationPid);
	
	List<DocumentStockLocationDTO> findAllByCompany();
	
	List<StockLocationDTO> findStockLocationsByDocumentPid(String documentPid);
	
	List<DocumentStockLocationDTO> findByDocumentPid(String documentPid) ;

	List<DocumentStockLocationDTO> findDocumentStockLocationSourceByDocuments();
	
	StockLocationDTO findDefaultStockLocationByDocumentPid(String documentPid);

	List<DocumentStockLocationDTO> findAllByCompanyIdAndlastModifiedDate(LocalDateTime lastModifiedDate);

}
