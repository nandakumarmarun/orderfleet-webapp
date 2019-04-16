package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingsDTO;

public interface InventoryClosingReportSettingsService {

	/**
	 * Save a inventoryClosingReport.
	 * 
	 * @param inventoryClosingReportDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	InventoryClosingReportSettingsDTO save(InventoryClosingReportSettingsDTO inventoryClosingReportDTO);
	
	/**
	 * Update a inventoryClosingReport.
	 * 
	 * @param inventoryClosingReportDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	InventoryClosingReportSettingsDTO update(InventoryClosingReportSettingsDTO inventoryClosingReportDTO);
	
	/**
	 * Get all the inventoryClosingReports of a company.
	 * 
	 * @return the list of entities
	 */
	List<InventoryClosingReportSettingsDTO> findAllByCompany();
	
	/**
	 * Get the "id" inventoryClosingReport.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	InventoryClosingReportSettingsDTO findOne(Long id);
	
	/**
	 * Get the inventoryClosingReport .
	 * 
	 * @param documentType
	 *            the documentType of the entity
	 * @param documentPid
	 *            the documentPid of the entity
	 * @return the entity
	 */
	Optional<InventoryClosingReportSettingsDTO> findByCompanyIdAndDocumentPid(String documentPid);
	
	/**
	 * Delete the "id" inventoryClosingReport.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);
}
