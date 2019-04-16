package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.web.rest.dto.UserTaskListAssignmentDTO;

/**
 * Service Interface for managing UserTaskList.
 * 
 * @author Sarath
 * @since July 13, 2016
 */
public interface UserTaskListAssignmentService {

	String PID_PREFIX = "USTLA-";

	/**
	 * Save a userTaskListAssignment.
	 * 
	 * @param userTaskListAssignmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	UserTaskListAssignmentDTO save(UserTaskListAssignmentDTO userTaskListAssignmentDTO);

	void save(List<UserTaskListAssignmentDTO> userTaskListAssignmentDTOs);

	/**
	 * Update a userTaskListAssignment.
	 * 
	 * @param userTaskListAssignmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	UserTaskListAssignmentDTO update(UserTaskListAssignmentDTO userTaskListAssignmentDTO);

	/**
	 * Get all the user task list assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserTaskListAssignment> findAll(Pageable pageable);

	/**
	 * Get all the user task list assignments.
	 * 
	 * @return the list of entities
	 */
	List<UserTaskListAssignmentDTO> findAllByCompany();

	List<UserTaskListAssignmentDTO> findUserTaskListByUserPid(String userPid);

	/**
	 * Get all the user task list assignments of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserTaskListAssignmentDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" userTaskListAssignment.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	UserTaskListAssignmentDTO findOne(Long id);

	/**
	 * Get the userTaskListAssignment by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<UserTaskListAssignmentDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" userTaskListAssignment.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
