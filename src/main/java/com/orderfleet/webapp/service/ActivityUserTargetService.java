package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyTargetDTO;

/**
 * Service Interface for managing ActivityUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
public interface ActivityUserTargetService {

	String PID_PREFIX = "ACUT-";

	/**
	 * Save a activityUserTarget.
	 * 
	 * @param activityUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ActivityUserTargetDTO save(ActivityUserTargetDTO activityUserTargetDTO);

	ActivityUserTargetDTO saveMonthlyTarget(MonthlyTargetDTO monthlyTargetDTO, LocalDate startDate, LocalDate endDate);

	/**
	 * Update a activityUserTarget.
	 * 
	 * @param activityUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ActivityUserTargetDTO update(ActivityUserTargetDTO activityUserTargetDTO);

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityUserTarget> findAll(Pageable pageable);

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<ActivityUserTargetDTO> findAllByCompany();

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ActivityUserTargetDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" activityUserTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ActivityUserTargetDTO findOne(Long id);

	/**
	 * Get the activityUserTarget by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ActivityUserTargetDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" activityUserTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
