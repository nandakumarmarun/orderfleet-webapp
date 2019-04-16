package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;

/**
 * Service Interface for managing ActivityGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public interface ActivityGroupUserTargetService {

	String PID_PREFIX = "ACGUT-";

	/**
	 * Save a activityGroupUserTarget.
	 * 
	 * @param activityGroupUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ActivityGroupUserTargetDTO save(ActivityGroupUserTargetDTO activityGroupUserTargetDTO);

	/**
	 * Update a activityGroupUserTarget.
	 * 
	 * @param activityGroupUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ActivityGroupUserTargetDTO update(ActivityGroupUserTargetDTO activityGroupUserTargetDTO);

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityGroupUserTarget> findAll(Pageable pageable);

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<ActivityGroupUserTargetDTO> findAllByCompany();

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityGroupUserTargetDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" activityGroupUserTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ActivityGroupUserTargetDTO findOne(Long id);

	/**
	 * Get the activityGroupUserTarget by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ActivityGroupUserTargetDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" activityGroupUserTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
