package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryConfigDTO;

/**
 * Service Interface for managing PurchaseHistoryConfig.
 * 
 * @author Muhammed Riyas T
 * @since Feb 06, 2017
 */
public interface PurchaseHistoryConfigService {

	String PID_PREFIX = "PHC-";

	/**
	 * Save a purchaseHistoryConfig.
	 * 
	 * @param purchaseHistoryConfigDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PurchaseHistoryConfigDTO save(PurchaseHistoryConfigDTO purchaseHistoryConfigDTO);

	/**
	 * Update a purchaseHistoryConfig.
	 * 
	 * @param purchaseHistoryConfigDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PurchaseHistoryConfigDTO update(PurchaseHistoryConfigDTO purchaseHistoryConfigDTO);

	/**
	 * Get all the purchaseHistoryConfigs.
	 * 
	 * @return the list of entities
	 */
	List<PurchaseHistoryConfigDTO> findAllByCompany();

	/**
	 * Get the purchaseHistoryConfig by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PurchaseHistoryConfigDTO> findOneByPid(String pid);

	/**
	 * Get the purchaseHistoryConfigDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<PurchaseHistoryConfigDTO> findByName(String name);

	/**
	 * Delete the "id" purchaseHistoryConfig.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	/**
	 * Get all the purchaseHistoryConfigs.
	 * 
	 * @return the list of entities
	 */
	List<PurchaseHistoryConfigDTO> findAllByCompanyIdOrderBySortOrderDesc();
}
