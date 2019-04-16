package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;

/**
 * Service Interface for managing UserTaskExecutionLog.
 * 
 * @author Muhammed Riyas T
 * @since 11 Novembor, 2016
 */
public interface UserTaskExecutionLogService {

	List<UserTaskAssignmentDTO> findUserTaskExecutionLogsByUserPid(String userPid);

}
