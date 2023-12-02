package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.UserTaskListTaskDTO;

import java.util.List;

/**
 * Service Interface for managing UserTaskAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public interface UserTaskListAssociationService {

	String PID_PREFIX = "UTLA-";

	UserTaskListTaskDTO save(UserTaskListTaskDTO userTaskListAssignmentDTO);

	List<UserTaskListTaskDTO> getAllTasksByEmployee(String userPid);

	void delete(String pid);

	
}
