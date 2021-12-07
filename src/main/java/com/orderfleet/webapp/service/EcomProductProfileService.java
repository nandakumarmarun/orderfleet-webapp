package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;

/**
 * Service Interface for managing EcomProductGroup.
 *
 * @author Sarath
 * @since Sep 23, 2016
 */

public interface EcomProductProfileService {

	String PID_PREFIX = "ECOMPG-";

	/**
	 * Save a ecomProductGroup.
	 *
	 * @param ecomProductGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	EcomProductProfileDTO save(EcomProductProfileDTO ecomProductGroupDTO);

	/**
	 * Update a ecomProductGroup.
	 *
	 * @param ecomProductGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	EcomProductProfileDTO update(EcomProductProfileDTO ecomProductGroupDTO);

	/**
	 * Get all the ecomProductGroups.
	 *
	 * @return the list of entities
	 */
	List<EcomProductProfileDTO> findAllByCompany();

	/**
	 * Get all the ecomProductGroups.
	 *
	 * @return the list of entities
	 */
	List<EcomProductProfileDTO> findByCurrentUser();
	List<EcomProductProfileDTO> findByCurrentUserForModern();

	/**
	 * Get all the ecomProductGroups of a company.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<EcomProductProfileDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" ecomProductGroup.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	EcomProductProfileDTO findOne(Long id);

	/**
	 * Get the ecomProductGroup by "pid".
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<EcomProductProfileDTO> findOneByPid(String pid);

	/**
	 * Get the ecomProductGroupDTO by "name".
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<EcomProductProfileDTO> findByName(String name);

	/**
	 * Delete the "id" ecomProductGroup.
	 *
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        Update the EcomProductProfile status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	EcomProductProfileDTO updateEcomProductProfileStatus(String pid, boolean activate);

	/**
	 * @author Fahad
	 * @since Feb 18, 2017
	 *
	 *        Get all the Activated ecomProductGroups of a company.
	 *
	 * @param pageable
	 *            the pagination information
	 *
	 * @param active
	 *            the active =true
	 *
	 * @return the entities
	 */
	Page<EcomProductProfileDTO> findAllByCompanyAndActivatedEcomProductProfileOrderByName(Pageable pageable,
			boolean active);

	/**
	 * @author Fahad
	 * @since Feb 18, 2017
	 *
	 *        find all deactive company
	 *
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<EcomProductProfileDTO> findAllByCompanyAndActivatedOrDeactivatedEcomProductProfile(boolean deactive);

	List<EcomProductProfileDTO> findByCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);
	List<EcomProductProfileDTO> findByproductgrupPId(String productgroupPid);
	List<EcomProductProfileDTO> findByCurrentUserAndLastModifiedDateForModern(LocalDateTime lastModifiedDate);

	EcomProductProfileDTO findOneByProductPid(String productPid);
	
}
