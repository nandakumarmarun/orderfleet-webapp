package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.web.rest.dto.UserTaskListAssignmentDTO;

/**
 * Mapper for the entity UserTaskListAssignment and its DTO
 * UserTaskListAssignmentDTO.
 * 
 * @author Sarath
 * @since July 14, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class UserTaskListAssignmentMapper {

	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Mapping(source = "executiveUser.pid", target = "executiveUserPid")
	@Mapping(source = "executiveUser.firstName", target = "executiveUserName")
	@Mapping(source = "taskList.pid", target = "taskListPid")
	@Mapping(source = "taskList.name", target = "taskListName")
	@Mapping(source = "user.pid", target = "userPid")
	@Mapping(source = "user.firstName", target = "userName")
	public abstract UserTaskListAssignmentDTO userTaskListAssignmentToUserTaskListAssignmentDTO(
			UserTaskListAssignment userTaskListAssignment);

	public abstract List<UserTaskListAssignmentDTO> userTaskListAssignmentsToUserTaskListAssignmentDTOs(
			List<UserTaskListAssignment> userTaskListAssignments);

	@Mapping(source = "executiveUserPid", target = "executiveUser")
	@Mapping(source = "taskListPid", target = "taskList")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract UserTaskListAssignment userTaskListAssignmentDTOToUserTaskListAssignment(
			UserTaskListAssignmentDTO userTaskListAssignmentDTO);

	public abstract List<UserTaskListAssignment> userTaskListAssignmentDTOsToUserTaskListAssignments(
			List<UserTaskListAssignmentDTO> userTaskListAssignmentDTOs);

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}

	public TaskList taskListFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return taskListRepository.findOneByPid(pid).map(taskList -> taskList).orElse(null);
	}

}
