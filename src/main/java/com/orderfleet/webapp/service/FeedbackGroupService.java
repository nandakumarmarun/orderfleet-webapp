package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.FeedbackGroupDTO;

/**
 * Service Interface for managing FeedbackGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
public interface FeedbackGroupService {

	String PID_PREFIX = "FBG-";

	/**
	 * Save a feedbackGroup.
	 * 
	 * @param feedbackGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	FeedbackGroupDTO save(FeedbackGroupDTO feedbackGroupDTO);

	/**
	 * Update a feedbackGroup.
	 * 
	 * @param feedbackGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	FeedbackGroupDTO update(FeedbackGroupDTO feedbackGroupDTO);

	/**
	 * Get all the feedbackGroups.
	 * 
	 * @return the list of entities
	 */
	List<FeedbackGroupDTO> findAllByCompany();

	/**
	 * Get the feedbackGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FeedbackGroupDTO> findOneByPid(String pid);

	/**
	 * Get the feedbackGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<FeedbackGroupDTO> findByName(String name);

	/**
	 * Delete the "id" feedbackGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
