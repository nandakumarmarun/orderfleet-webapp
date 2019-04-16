package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;

/**
 * Service Interface for managing TaskList.
 * 
 * @author Sarath
 * @since July 13, 2016
 */
public interface TaskListService {

	String PID_PREFIX = "TASKLT-";

	/**
	 * Save a taskList.
	 * 
	 * @param taskListDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	TaskListDTO save(TaskListDTO taskListDTO);

	TaskListDTO saveAssignedTasks(String pid, String assignedTasks);

	/**
	 * Update a taskList.
	 * 
	 * @param taskListDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	TaskListDTO update(TaskListDTO taskListDTO);

	/**
	 * Get all the taskLists.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskList> findAll(Pageable pageable);

	/**
	 * Get all the taskLists.
	 * 
	 * @return the list of entities
	 */
	List<TaskListDTO> findAllByCompany();

	/**
	 * Get all the taskLists of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskListDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" taskList.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	TaskListDTO findOne(Long id);

	/**
	 * Get the taskList by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<TaskListDTO> findOneByPid(String pid);

	/**
	 * Get the taskListDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	/**
	 * @param name
	 * @return
	 */
	Optional<TaskListDTO> findByName(String name);

	/**
	 * Delete the "id" taskList.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	Optional<TaskListDTO> findByNameAndPid(String taskListName, String pid);
	
	List<TaskDTO>findByTaskListPid(String pid);
}
