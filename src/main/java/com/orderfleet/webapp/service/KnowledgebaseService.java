package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;

/**
 * Service Interface for managing Knowledgebase.
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
public interface KnowledgebaseService {

	String PID_PREFIX = "KNBT-";

	/**
	 * Save a knowledgebase.
	 * 
	 * @param knowledgebaseDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	KnowledgebaseDTO save(KnowledgebaseDTO knowledgebaseDTO);

	/**
	 * Update a knowledgebase.
	 * 
	 * @param knowledgebaseDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	KnowledgebaseDTO update(KnowledgebaseDTO knowledgebaseDTO);

	/**
	 * Get all the knowledgebases.
	 * 
	 * @return the list of entities
	 */
	List<KnowledgebaseDTO> findAllByCompany();

	/**
	 * Get all the knowledgebases of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<KnowledgebaseDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" knowledgebase.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	KnowledgebaseDTO findOne(Long id);

	/**
	 * Get the knowledgebase by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<KnowledgebaseDTO> findOneByPid(String pid);

	/**
	 * Get the knowledgebaseDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<KnowledgebaseDTO> findByName(String name);

	/**
	 * Delete the "id" knowledgebase.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	/**
	 * Update  the Knowledgebase status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	KnowledgebaseDTO updateKnowledgebaseStatus(String pid,boolean active);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all knowledgebaseDTO from Knowledgebase by status and company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	List<KnowledgebaseDTO> findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(boolean active);
	
	/**
	 *  @author Fahad
	 * @since Feb 15, 2017
	 * 
	 * find all active company 
	 * 
	 *@param active
	 *            the active of the entity
	 *            
	 *@param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	Page<KnowledgebaseDTO> findAllByCompanyIdAndActivatedKnowledgebaseOrderByName(Pageable pageable ,boolean active);

	List<KnowledgebaseDTO> findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivatedAndLastModifiedDate(boolean active,
			LocalDateTime lastModifiedDate);
	
}
