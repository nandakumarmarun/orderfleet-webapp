package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

/**
 * Service Interface for managing Task.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public interface TaskService {

	String PID_PREFIX = "TASKD-";

	/**
	 * Save a task.
	 * 
	 * @param taskDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	List<TaskDTO> save(TaskDTO taskDTO);

	/**
	 * Update a task.
	 * 
	 * @param taskDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	TaskDTO update(TaskDTO taskDTO);

	/**
	 * Get all the tasks.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Task> findAll(Pageable pageable);

	/**
	 * Get all the tasks of a company.
	 * 
	 * @return the list of entities
	 */
	List<TaskDTO> findAllByCompany();

	/**
	 * Get all the tasks of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" task.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	TaskDTO findOne(Long id);

	/**
	 * Get the task by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<TaskDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" task.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	List<TaskDTO> findByActivityPids(List<String> activgityPids);
	List<TaskDTO> findByAccountProfilePids(List<String> accProfilePids);
	List<TaskDTO> findByActivityPidsAndAccountProfilePids(List<String> activgityPids, List<String> accProfilePids);

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * Get all the AccountProfiles of a company By AccountType and Activity.
	 * 
	 * @param activityPid
	 *            the activityPid of the entity
	 * @param accountTypePid
	 *            the accountTypePid of the entity          
	 *            
	 * @return the list of entities
	 */
	List<AccountProfileDTO> findAccountProfileByAccountTypePidAndActivityPid(String activityPid ,String accountTypePid);
	
	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * Update task By Pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity          
	 *            
	 * @return the list of entities
	 */
	TaskDTO updateTaskStatus(String pid,boolean activate);
	
	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 *        find all the Tasks by company activated.
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 * @return the list of entities
	 */
	List<TaskDTO> findAllByCompanyAndActivated(boolean active);
	
	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * find all the Tasks by ActivityPids And Activated.
	 * 
	 * @param activityPids
	 *            the activityPids of the entity
	 *            
	 * @return the list of entities
	 */
	List<TaskDTO> findByActivityPidsAndActivated(List<String> activityPids);
	
	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * find all the Tasks by AccountTypePids And Activated.
	 * 
	 * @param accountTypePids
	 *            the accountTypePids of the entity
	 *            
	 * @return the list of entities
	 */
	List<TaskDTO> findByAccountTypePidsAndActivated(List<String> accountTypePids);
	
	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * find all the Tasks by AccountTypePids And ActivityPids.
	 * 
	 * @param accountTypePids
	 *            the accountTypePids of the entity
	 *   
	 * @param activityPids
	 *            the activityPids of the entity
	 *            
	 * @return the list of entities
	 */
	List<TaskDTO> findByActivityPidsAndAccountTypePidsAndActivated(List<String> activityPids,
			List<String> accountTypePids);
}
