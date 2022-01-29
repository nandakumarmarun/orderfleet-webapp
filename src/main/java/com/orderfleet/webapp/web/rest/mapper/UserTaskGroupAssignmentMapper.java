package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.TaskGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.UserTaskGroupAssignmentDTO;

/**
 * Mapper for the entity UserTaskGroupAssignment and its DTO
 * UserTaskGroupAssignmentDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
@Component
public abstract class UserTaskGroupAssignmentMapper {

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskGroupRepository taskGroupRepository;

//	@Mapping(source = "executiveUser.pid", target = "executiveUserPid")
//	@Mapping(source = "executiveUser.firstName", target = "executiveUserName")
//	@Mapping(source = "taskGroup.pid", target = "taskGroupPid")
//	@Mapping(source = "taskGroup.name", target = "taskGroupName")
//	@Mapping(source = "user.pid", target = "userPid")
//	@Mapping(source = "user.firstName", target = "userName")
	public abstract UserTaskGroupAssignmentDTO userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(
			UserTaskGroupAssignment userTaskGroupAssignment);

	public abstract List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentsToUserTaskGroupAssignmentDTOs(
			List<UserTaskGroupAssignment> userTaskGroupAssignments);

//	@Mapping(source = "executiveUserPid", target = "executiveUser")
//	@Mapping(source = "taskGroupPid", target = "taskGroup")
//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "user", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract UserTaskGroupAssignment userTaskGroupAssignmentDTOToUserTaskGroupAssignment(
			UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO);

	public abstract List<UserTaskGroupAssignment> userTaskGroupAssignmentDTOsToUserTaskGroupAssignments(
			List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentDTOs);

	public User userFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return userRepository.findOneByPid(pid).map(user -> user).orElse(null);
	}

	public TaskGroup taskGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return taskGroupRepository.findOneByPid(pid).map(taskGroup -> taskGroup).orElse(null);
	}
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}

}
