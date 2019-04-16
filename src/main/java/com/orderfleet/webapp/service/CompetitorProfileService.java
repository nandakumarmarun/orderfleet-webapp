package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;

/**
 * Service Interface for managing CompetitorProfile.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface CompetitorProfileService {

	String PID_PREFIX = "CMTP-";

	/**
	 * Save a competitorProfile.
	 * 
	 * @param competitorProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	CompetitorProfileDTO save(CompetitorProfileDTO competitorProfileDTO);

	/**
	 * Update a competitorProfile.
	 * 
	 * @param competitorProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	CompetitorProfileDTO update(CompetitorProfileDTO competitorProfileDTO);

	/**
	 * Get all the competitorProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<CompetitorProfile> findAll(Pageable pageable);

	/**
	 * Get all the competitorProfiles.
	 * 
	 * @return the list of entities
	 */
	List<CompetitorProfileDTO> findAllByCompany();

	/**
	 * Get all the competitorProfiles of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<CompetitorProfileDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" competitorProfile.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	CompetitorProfileDTO findOne(Long id);

	/**
	 * Get the competitorProfile by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<CompetitorProfileDTO> findOneByPid(String pid);

	/**
	 * Get the competitorProfileDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<CompetitorProfileDTO> findByName(String name);

	/**
	 * Delete the "id" competitorProfile.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	
	/**
	 *  update the competitorProfileDTO by "pid".
	 * 
	 * @param pid
	 * 				the id of the entity
	 * @param active
	 * 				the active of the entity
	 * @return the entity
	 */
	CompetitorProfileDTO updateCompetitorProfileStatus(String pid,boolean active);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all competitorProfileDTO from CompetitorProfile by status and company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	List<CompetitorProfileDTO> findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(boolean active);
	
	/**
	 *  @author Fahad
	 * @since Feb 11, 2017
	 * 
	 * find all active company 
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<CompetitorProfileDTO> findAllByCompanyAndCompetitorProfileActivatedOrderByName(Pageable pageable,boolean active);

	List<CompetitorProfileDTO> findAllByCompanyIdAndCompetitorProfileActivatedAndLastModifiedDate(boolean active,
			LocalDateTime lastModifiedDate);
	
}
