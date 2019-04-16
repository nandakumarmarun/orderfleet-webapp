package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;

/**
 * Service Interface for managing UserTaskAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public interface UserTaskAssignmentService {

	String PID_PREFIX = "USTA-";

	/**
	 * Save a userTaskAssignment.
	 * 
	 * @param userTaskAssignmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	UserTaskAssignmentDTO save(UserTaskAssignmentDTO userTaskAssignmentDTO);

	void save(List<UserTaskAssignmentDTO> userTaskAssignmentDTOs);

	void closeUserTaskAssignment(String userTaskAssignmentPid);

	/**
	 * Update a userTaskAssignment.
	 * 
	 * @param userTaskAssignmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	UserTaskAssignmentDTO update(UserTaskAssignmentDTO userTaskAssignmentDTO);

	/**
	 * Get all the user task assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserTaskAssignment> findAll(Pageable pageable);

	/**
	 * Get all the user task assignments.
	 * 
	 * @return the list of entities
	 */
	List<UserTaskAssignmentDTO> findAllByCompany();

	List<UserTaskAssignmentDTO> findUserTasksByUserPid(String userPid);

	/**
	 * Get all the user task assignments of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<UserTaskAssignmentDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" userTaskAssignment.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	UserTaskAssignmentDTO findOne(Long id);

	/**
	 * Get the userTaskAssignment by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<UserTaskAssignmentDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" userTaskAssignment.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
