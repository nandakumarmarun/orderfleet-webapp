package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.DocumentApprovalLevelDTO;

/**
 * Service Interface for managing DocumentApprovalLevel.
 * 
 * @author Muhammed Riyas T
 * @since November 19, 2016
 */
public interface DocumentApprovalLevelService {

	String PID_PREFIX = "DCAL-";

	/**
	 * Save a documentApprovalLevel.
	 * 
	 * @param documentApprovalLevelDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DocumentApprovalLevelDTO save(DocumentApprovalLevelDTO documentApprovalLevelDTO);

	void saveAssignedUsers(String pid, String assignedUsers);

	/**
	 * Update a documentApprovalLevel.
	 * 
	 * @param documentApprovalLevelDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DocumentApprovalLevelDTO update(DocumentApprovalLevelDTO documentApprovalLevelDTO);

	/**
	 * Get all the documentApprovalLevels of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<DocumentApprovalLevelDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" documentApprovalLevel.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DocumentApprovalLevelDTO findOne(Long id);

	/**
	 * Get the documentApprovalLevel by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DocumentApprovalLevelDTO> findOneByPid(String pid);

	/**
	 * Get the documentApprovalLevelDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DocumentApprovalLevelDTO> findByName(String name);

	/**
	 * Delete the "id" documentApprovalLevel.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<DocumentApprovalLevelDTO> findAllByDocumentPid(String documentPid);

	DocumentApprovalLevelDTO saveApprovalOrder(DocumentApprovalLevelDTO documentApprovalLevelDTO);
	
	List<DocumentApprovalLevelDTO> findAllByCompany();

}
