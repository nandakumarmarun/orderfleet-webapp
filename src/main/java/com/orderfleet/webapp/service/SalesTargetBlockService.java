package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;

/**
 * Service Interface for managing SalesTargetBlock.
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
public interface SalesTargetBlockService {

	String PID_PREFIX = "STBLOK-";

	/**
	 * Save a salesTargetBlock.
	 * 
	 * @param salesTargetBlockDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	SalesTargetBlockDTO save(SalesTargetBlockDTO salesTargetBlockDTO);

	/**
	 * Update a salesTargetBlock.
	 * 
	 * @param salesTargetBlockDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	SalesTargetBlockDTO update(SalesTargetBlockDTO salesTargetBlockDTO);

	/**
	 * Get all the salesTargetBlocks.
	 * 
	 * @return the list of entities
	 */
	List<SalesTargetBlockDTO> findAllByCompany();

	/**
	 * Get the salesTargetBlock by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<SalesTargetBlockDTO> findOneByPid(String pid);

	/**
	 * Get the salesTargetBlockDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<SalesTargetBlockDTO> findByName(String name);

	/**
	 * Delete the "id" salesTargetBlock .
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<SalesTargetBlockDTO> findAllByCompanyIdAndtargetSettingType(BestPerformanceType targetSettingType);

}
