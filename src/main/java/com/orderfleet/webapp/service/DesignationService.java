package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.web.rest.dto.DesignationDTO;

/**
 * Service Interface for managing Designation.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
public interface DesignationService {

	String PID_PREFIX = "DESG-";

	/**
	 * Save a designation.
	 * 
	 * @param designationDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DesignationDTO save(DesignationDTO designationDTO);

	/**
	 * Update a designation.
	 * 
	 * @param designationDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DesignationDTO update(DesignationDTO designationDTO);

	/**
	 * Get all the designations.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Designation> findAll(Pageable pageable);

	/**
	 * Get all the designations of a company.
	 * 
	 * @return the list of entities
	 */
	List<DesignationDTO> findAllByCompany();

	/**
	 * Get all the designations of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<DesignationDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" designation.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DesignationDTO findOne(Long id);

	/**
	 * Get the designation by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DesignationDTO> findOneByPid(String pid);

	/**
	 * Get the designationDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DesignationDTO> findByName(String name);

	/**
	 * Delete the "id" designation.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Update the Designation status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	DesignationDTO updateDesignationStatus(String pid, boolean activate);

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<DesignationDTO> findAllCompanyAndActivatedDesignation(Pageable pageable, boolean active);

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
	List<DesignationDTO> findAllCompanyAndDeactivatedDesignation(boolean deactive);
}
