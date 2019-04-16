package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.web.rest.dto.UserTaskGroupAssignmentDTO;

/**
 * Service Interface for managing UserTaskGroupAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public interface UserTaskGroupAssignmentService {

	String PID_PREFIX = "USTGA-";

	/**
	 * Save a userTaskGroupAssignment.
	 * 
	 * @param userTaskGroupAssignmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	UserTaskGroupAssignmentDTO save(UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO);

	void save(List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentDTOs);

	/**
	 * Update a userTaskGroupAssignment.
	 * 
	 * @param userTaskGroupAssignmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	UserTaskGroupAssignmentDTO update(UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO);

	/**
	 * Get all the user task group assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserTaskGroupAssignment> findAll(Pageable pageable);

	/**
	 * Get all the user task group assignments.
	 * 
	 * @return the list of entities
	 */
	List<UserTaskGroupAssignmentDTO> findAllByCompany();

	/**
	 * Get all the user task group assignments.
	 * 
	 * @param userPid
	 * @return the list of entities
	 */
	List<UserTaskGroupAssignmentDTO> findUserTaskGroupsByUserPid(String userPid);

	/**
	 * Get all the user task group assignments of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserTaskGroupAssignmentDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" userTaskGroupAssignment.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	UserTaskGroupAssignmentDTO findOne(Long id);

	/**
	 * Get the userTaskGroupAssignment by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<UserTaskGroupAssignmentDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" userTaskGroupAssignment.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	UserTaskGroupAssignmentDTO findAllByTaskGroupPidAndStartDate(String taskGroupPid);

}
