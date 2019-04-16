package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;

/**
 * Mapper for the entity TaskGroup and its DTO TaskGroupDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Mapper(componentModel = "spring", uses = { TaskMapper.class, })
public abstract class TaskGroupMapper {

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Mapping(source = "activityGroup.pid", target = "activityGroupPid")
	@Mapping(source = "activityGroup.name", target = "activityGroupName")
	public abstract TaskGroupDTO taskGroupToTaskGroupDTO(TaskGroup taskGroup);

	public abstract List<TaskGroupDTO> taskGroupsToTaskGroupDTOs(List<TaskGroup> taskGroups);

	@Mapping(source = "activityGroupPid", target = "activityGroup")
	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract TaskGroup taskGroupDTOToTaskGroup(TaskGroupDTO taskGroupDTO);

	public abstract List<TaskGroup> taskGroupDTOsToTaskGroups(List<TaskGroupDTO> taskGroupDTOs);

	public ActivityGroup activityGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return activityGroupRepository.findOneByPid(pid).map(activityGroup -> activityGroup).orElse(null);
	}

}
