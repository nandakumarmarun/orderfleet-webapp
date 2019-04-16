package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

public interface PriceLevelAccountProductGroupService {
	String PID_PREFIX = "PLAPG-";

	/**
	 * Save a priceLevelAccountProductGroup.
	 * 
	 * @param priceLevelAccountProductGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PriceLevelAccountProductGroupDTO save(PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO);

	/**
	 * Update a priceLevelAccountProductGroup.
	 * 
	 * @param priceLevelAccountProductGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PriceLevelAccountProductGroupDTO update(PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO);

	/**
	 * Get all the priceLevelAccountProductGroups.
	 * 
	 * @return the list of entities
	 */
	List<PriceLevelAccountProductGroupDTO> findAllByCompany();

	/**
	 * Get the "pid" priceLevelAccountProductGroup.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PriceLevelAccountProductGroupDTO> findOneByPid(String pid);
	
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
