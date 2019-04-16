package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;

/**
 * Service Interface for managing TaskGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public interface TaskGroupService {

	String PID_PREFIX = "TSKGRP-";

	/**
	 * Save a taskGroup.
	 * 
	 * @param taskGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	TaskGroupDTO save(TaskGroupDTO taskGroupDTO);

	void saveAssignedTasks(String pid, String assignedTasks);

	/**
	 * Update a taskGroup.
	 * 
	 * @param taskGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	TaskGroupDTO update(TaskGroupDTO taskGroupDTO);

	/**
	 * Get all the taskGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskGroup> findAll(Pageable pageable);

	/**
	 * Get all the taskGroups.
	 * 
	 * @return the list of entities
	 */
	List<TaskGroupDTO> findAllByCompany();

	/**
	 * Get all the taskGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" taskGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	TaskGroupDTO findOne(Long id);

	/**
	 * Get the taskGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<TaskGroupDTO> findOneByPid(String pid);

	/**
	 * Get the taskGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<TaskGroupDTO> findByName(String name);

	/**
	 * Delete the "id" taskGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	Optional<TaskGroupDTO> findByNameAndPid(String taskGroupName, String pid);

}
