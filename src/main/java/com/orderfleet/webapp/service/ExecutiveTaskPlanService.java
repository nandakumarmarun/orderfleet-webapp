package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;

/**
 * Service Interface for managing ExecutiveTaskPlan.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public interface ExecutiveTaskPlanService {

	String PID_PREFIX = "EXTP-";

	/**
	 * Save a executiveTaskPlan.
	 * 
	 * @param executiveTaskPlanDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ExecutiveTaskPlanDTO save(ExecutiveTaskPlanDTO executiveTaskPlanDTO);
	
	/**
	 * Save multiple executiveTaskPlan.
	 *
	 * @param executiveTaskPlanDTO
	 *            List of the entities to save
	 */
	void save(String userPid, List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs, LocalDate plannedDate);

	/**
	 * Update a executiveTaskPlan.
	 * 
	 * @param executiveTaskPlanDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ExecutiveTaskPlanDTO update(ExecutiveTaskPlanDTO executiveTaskPlanDTO);

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskPlan> findAll(Pageable pageable);

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @return the list of entities
	 */
	List<ExecutiveTaskPlanDTO> findAllByCompany();

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @return the list of entities
	 */
	List<ExecutiveTaskPlanDTO> findByUserIsCurrentUser();

	List<ExecutiveTaskPlanDTO> findByUserPidAndPlannedDate(String userPid, LocalDate plannedDate);
	
	List<ExecutiveTaskPlanDTO> findByUserPidAndPlannedDateBetween(String userPid, LocalDate startDate, LocalDate endDate);
	
	List<ExecutiveTaskPlanDTO> findByUserPidAndPlannedDateAndNotTaskPlanStatus(String userPid, LocalDate plannedDate, TaskPlanStatus taskPlanStatus);

	/**
	 * Get all the executiveTaskPlans of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskPlanDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" executiveTaskPlan.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ExecutiveTaskPlanDTO findOne(Long id);

	/**
	 * Get the executiveTaskPlan by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ExecutiveTaskPlanDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" executiveTaskPlan.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	List<ExecutiveTaskPlanDTO> findDistictPlannedDateByUserPidAndPlannedDateGreaterThanEqualToDate(String userPid, LocalDateTime date);
	
	void save(List<ExecutiveTaskPlan> newExecutiveTaskPlan);
	
	void saveDayPlanByRootPlanBased(TaskList taskList);
	
	void deleteDayPlanByUserPidAndTaskListPid(String userPid,String taskListPid);

}
