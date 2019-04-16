package com.orderfleet.webapp.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.repository.UserTaskAssignmentRepository;
import com.orderfleet.webapp.repository.UserTaskExecutionLogRepository;
import com.orderfleet.webapp.service.UserTaskExecutionLogService;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskAssignmentMapper;

/**
 * Service Implementation for managing UserTaskExecutionLog.
 * 
 * @author Muhammed Riyas T
 * @since 11 Novembor, 2016
 */
@Service
@Transactional
public class UserTaskExecutionLogServiceImpl implements UserTaskExecutionLogService {

	private final Logger log = LoggerFactory.getLogger(UserTaskExecutionLogServiceImpl.class);

	@Inject
	private UserTaskExecutionLogRepository userTaskExecutionLogRepository;

	@Inject
	private UserTaskAssignmentRepository userTaskAssignmentRepository;

	@Inject
	private UserTaskAssignmentMapper userTaskAssignmentMapper;

	@Override
	public List<UserTaskAssignmentDTO> findUserTaskExecutionLogsByUserPid(String userPid) {
		log.info("find User Task Execution Logs");
		List<UserTaskAssignment> userTaskAssignments = userTaskAssignmentRepository.findUserTasksByUserPid(userPid);
		List<UserTaskAssignmentDTO> result = userTaskAssignmentMapper
				.userTaskAssignmentsToUserTaskAssignmentDTOs(userTaskAssignments);
		for (UserTaskAssignmentDTO userTaskAssignmentDTO : result) {
			userTaskAssignmentDTO.setUserTaskExecutionLogs(userTaskExecutionLogRepository
					.findUserTaskExecutionLogByUserTaskAssignmentPid(userTaskAssignmentDTO.getPid()));
		}
		return result;
	}

}
