package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.PriceTrendProductGroup;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;

/**
 * Service Interface for managing PriceTrendProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface PriceTrendProductGroupService {

	String PID_PREFIX = "PTPG-";

	/**
	 * Save a priceTrendProductGroup.
	 * 
	 * @param priceTrendProductGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PriceTrendProductGroupDTO save(PriceTrendProductGroupDTO priceTrendProductGroupDTO);

	void saveAssignedProducts(String pid, String assignedProducts);

	/**
	 * Update a priceTrendProductGroup.
	 * 
	 * @param priceTrendProductGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PriceTrendProductGroupDTO update(PriceTrendProductGroupDTO priceTrendProductGroupDTO);

	/**
	 * Get all the priceTrendProductGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceTrendProductGroup> findAll(Pageable pageable);

	/**
	 * Get all the priceTrendProductGroups.
	 * 
	 * @return the list of entities
	 */
	List<PriceTrendProductGroupDTO> findAllByCompany();

	/**
	 * Get all the priceTrendProductGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceTrendProductGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" priceTrendProductGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	PriceTrendProductGroupDTO findOne(Long id);

	/**
	 * Get the priceTrendProductGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PriceTrendProductGroupDTO> findOneByPid(String pid);

	/**
	 * Get the priceTrendProductGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<PriceTrendProductGroupDTO> findByName(String name);

	/**
	 * Delete the "id" priceTrendProductGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	/**
	 * Update  the PriceTrendProductGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	PriceTrendProductGroupDTO updatePriceTrendProductGroupStatus(String pid,boolean active);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all priceTrendProductGroupDTO from PriceTrendProductGroup by status and company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	List<PriceTrendProductGroupDTO> findAllByCompanyIdAndPriceTrendProductGroupActivated(boolean active);
	
	/**
	 *  @author Fahad
	 * @since Feb 14, 2017
	 * 
	 * find all active company 
	 * 
	 * @param pageable
	 * 				 the pageable of the entity	
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<PriceTrendProductGroupDTO> findAllByCompanyAndPriceTrendProductGroupActivatedOrderByName(Pageable pageable,boolean active);

	List<PriceTrendProductGroupDTO> findAllByCompanyIdAndPriceTrendProductGroupActivatedAndlastModifiedDate(
			boolean active, LocalDateTime lastModifiedDate);
	
}
