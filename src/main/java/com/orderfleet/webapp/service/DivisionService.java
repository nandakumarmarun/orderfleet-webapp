package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.DivisionDTO;

/**
 * Service Interface for managing Division.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
public interface DivisionService {

	String PID_PREFIX = "DIV-";

	/**
	 * Save a division.
	 * 
	 * @param divisionDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DivisionDTO save(DivisionDTO divisionDTO);

	/**
	 * Update a division.
	 * 
	 * @param divisionDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DivisionDTO update(DivisionDTO divisionDTO);

	/**
	 * Get all the divisions.
	 * 
	 * @return the list of entities
	 */
	List<DivisionDTO> findAllByCompany();

	/**
	 * Get all the divisions of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<DivisionDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" division.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DivisionDTO findOne(Long id);

	/**
	 * Get the division by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DivisionDTO> findOneByPid(String pid);

	/**
	 * Get the divisionDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DivisionDTO> findByName(String name);

	/**
	 * Delete the "id" division.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Get the divisionDTO by "alias".
	 * 
	 * @param alias
	 *            the alias of the entity
	 * @return the entity
	 */
	Optional<DivisionDTO> findByAlias(String alias);

	/**
	 * Update the Division status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	DivisionDTO updateDivisionStatus(String pid, boolean activate);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	Page<DivisionDTO> findAllByCompanyAndActivatedDivisionOrderByName(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<DivisionDTO> findAllByCompanyAndDeactivatedDivision(boolean deactive);
}
