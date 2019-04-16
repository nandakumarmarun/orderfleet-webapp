package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

/**
 * Mapper for the entity Task and its DTO TaskDTO.
 * 
 * @author Myhammed Riyas T
 * @since June 04, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class TaskMapper {

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Mapping(target = "accountPids", ignore = true)
	@Mapping(source = "activity.pid", target = "activityPid")
	@Mapping(source = "activity.name", target = "activityName")
	@Mapping(source = "accountType.pid", target = "accountTypePid")
	@Mapping(source = "accountType.name", target = "accountTypeName")
	@Mapping(source = "accountProfile.pid", target = "accountProfilePid")
	@Mapping(source = "accountProfile.name", target = "accountProfileName")
	public abstract TaskDTO taskToTaskDTO(Task task);

	public abstract List<TaskDTO> tasksToTaskDTOs(List<Task> tasks);

	@Mapping(source = "activityPid", target = "activity")
	@Mapping(source = "accountTypePid", target = "accountType")
	@Mapping(source = "accountProfilePid", target = "accountProfile")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract Task taskDTOToTask(TaskDTO taskDTO);

	public abstract List<Task> taskDTOsToTasks(List<TaskDTO> taskDTOs);

	public Activity activityFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return activityRepository.findOneByPid(pid).map(activity -> activity).orElse(null);
	}

	public AccountType accountTypeFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return accountTypeRepository.findOneByPid(pid).map(accountType -> accountType).orElse(null);
	}

	public AccountProfile accountProfileFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return accountProfileRepository.findOneByPid(pid).map(accountType -> accountType).orElse(null);
	}
}
