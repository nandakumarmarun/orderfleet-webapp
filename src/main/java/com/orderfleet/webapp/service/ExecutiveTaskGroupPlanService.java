package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ExecutiveTaskGroupPlan;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskGroupPlanDTO;

/**
 * Service Interface for managing ExecutiveTaskGroupPlan.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public interface ExecutiveTaskGroupPlanService {

	String PID_PREFIX = "EXTGP-";

	/**
	 * Save a executiveTaskGroupPlan.
	 * 
	 * @param executiveTaskGroupPlanDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ExecutiveTaskGroupPlanDTO save(ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO);

	/**
	 * Update a executiveTaskGroupPlan.
	 * 
	 * @param executiveTaskGroupPlanDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ExecutiveTaskGroupPlanDTO update(ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO);

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskGroupPlan> findAll(Pageable pageable);

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<ExecutiveTaskGroupPlanDTO> findAllByCompany();

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskGroupPlanDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" executiveTaskGroupPlan.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ExecutiveTaskGroupPlanDTO findOne(Long id);

	/**
	 * Get the executiveTaskGroupPlan by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ExecutiveTaskGroupPlanDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" executiveTaskGroupPlan.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
