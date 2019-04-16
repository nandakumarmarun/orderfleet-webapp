package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ExecutiveTaskListPlan;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskListPlanDTO;

/**
 * Service Interface for managing ExecutiveTaskListPlan.
 * 
 * @author Sarath
 * @since July 14, 2016
 */
public interface ExecutiveTaskListPlanService {

	String PID_PREFIX = "EXTLTP-";

	/**
	 * Save a executiveTaskListPlan.
	 * 
	 * @param executiveTaskListPlanDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ExecutiveTaskListPlanDTO save(ExecutiveTaskListPlanDTO executiveTaskListPlanDTO);

	/**
	 * Update a executiveTaskListPlan.
	 * 
	 * @param executiveTaskListPlanDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ExecutiveTaskListPlanDTO update(ExecutiveTaskListPlanDTO executiveTaskListPlanDTO);

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskListPlan> findAll(Pageable pageable);

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<ExecutiveTaskListPlanDTO> findAllByCompany();

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskListPlanDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" executiveTaskListPlan.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ExecutiveTaskListPlanDTO findOne(Long id);

	/**
	 * Get the executiveTaskListPlan by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ExecutiveTaskListPlanDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" executiveTaskListPlan.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
