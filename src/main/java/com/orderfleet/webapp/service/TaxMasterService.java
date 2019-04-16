package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;

/**
 * Service Interface for managing TaxMaster.
 *
 * @author Sarath
 * @since Aug 8, 2017
 *
 */
public interface TaxMasterService {

	String PID_PREFIX = "TXMSTR-";

	TaxMasterDTO saveTaxMaster(TaxMasterDTO taxMasterDTO);
	
	/**
	 * Get the taxMasterDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<TaxMasterDTO> findByName(String name);
	
	/**
	 * Update a taxMaster.
	 * 
	 * @param taxMasterDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	TaxMasterDTO updateTaxMaster(TaxMasterDTO taxMasterDTO);
	
	/**
	 * Get all the taxMasters.
	 * 
	 * @return the list of entities
	 */
	List<TaxMasterDTO> findAllByCompany();
	
	/**
	 * Get the taxMaster by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<TaxMasterDTO> findOneByPid(String pid);
	
	/**
	 * Delete the "id" taxMaster.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
}
