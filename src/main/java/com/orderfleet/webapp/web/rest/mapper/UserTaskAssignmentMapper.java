package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;

/**
 * Mapper for the entity UserTaskAssignment and its DTO UserTaskAssignmentDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class UserTaskAssignmentMapper {

	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskRepository taskRepository;

	@Mapping(source = "executiveUser.pid", target = "executiveUserPid")
	@Mapping(source = "executiveUser.firstName", target = "executiveUserName")
	@Mapping(source = "task.pid", target = "taskPid")
	@Mapping(source = "task.activity.name", target = "taskActivityName")
	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "user.firstName", target = "userName")
	@Mapping(source = "task.accountProfile.name", target = "taskAccountName")
	@Mapping(target = "userTaskExecutionLogs", ignore = true)
	public abstract UserTaskAssignmentDTO userTaskAssignmentToUserTaskAssignmentDTO(
			UserTaskAssignment userTaskAssignment);

	public abstract List<UserTaskAssignmentDTO> userTaskAssignmentsToUserTaskAssignmentDTOs(
			List<UserTaskAssignment> userTaskAssignments);

	@Mapping(source = "executiveUserPid", target = "executiveUser")
	@Mapping(source = "taskPid", target = "task")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract UserTaskAssignment userTaskAssignmentDTOToUserTaskAssignment(
			UserTaskAssignmentDTO userTaskAssignmentDTO);

	public abstract List<UserTaskAssignment> userTaskAssignmentDTOsToUserTaskAssignments(
			List<UserTaskAssignmentDTO> userTaskAssignmentDTOs);

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}

	public Task taskFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return taskRepository.findOneByPid(pid).map(task -> task).orElse(null);
	}

}
