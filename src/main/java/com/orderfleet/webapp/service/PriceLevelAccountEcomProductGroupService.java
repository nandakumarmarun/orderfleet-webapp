package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountEcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

public interface PriceLevelAccountEcomProductGroupService {
	String PID_PREFIX = "PLAPG-";

	/**
	 * Save a priceLevelAccountProductGroup.
	 * 
	 * @param priceLevelAccountProductGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PriceLevelAccountEcomProductGroupDTO save(PriceLevelAccountEcomProductGroupDTO priceLevelAccountProductGroupDTO);

	/**
	 * Update a priceLevelAccountProductGroup.
	 * 
	 * @param priceLevelAccountProductGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PriceLevelAccountEcomProductGroupDTO update(PriceLevelAccountEcomProductGroupDTO priceLevelAccountProductGroupDTO);

	/**
	 * Get all the priceLevelAccountProductGroups.
	 * 
	 * @return the list of entities
	 */
	List<PriceLevelAccountEcomProductGroupDTO> findAllByCompany();

	/**
	 * Get the "pid" priceLevelAccountProductGroup.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PriceLevelAccountEcomProductGroupDTO> findOneByPid(String pid);
	
	/**
	 * Delete the "pid" priceLevelAccountProductGroup.
	 * 
	 * @param pid
	 *            the pid of the entity
	 */
	void delete(String pid);
	
	void saveMultiPriceLevelAccountProductGroup(String accountPids,String productGroupPids,String priceLevelPid);
	
	List<PriceLevelDTO>findAllByUserLogin();
}
