package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.GuidedSellingConfigDTO;

/**
 * Service Interface for managing GuidedSellingConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
public interface GuidedSellingConfigService {

	/**
	 * Save a guidedSellingConfigs.
	 * 
	 * @param guidedSellingConfigDTOs
	 *            the entity to save
	 */
	void save(GuidedSellingConfigDTO guidedSellingConfigDTO);

	/**
	 * Get the guidedSellingConfig.
	 * 
	 * @return the list of entities
	 */
	GuidedSellingConfigDTO findByCompany();

}
