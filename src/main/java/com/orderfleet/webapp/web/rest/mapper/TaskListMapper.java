package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;

@Mapper(componentModel = "spring", uses = { TaskMapper.class, })
public abstract class TaskListMapper {

	public abstract TaskListDTO taskListToTaskListDTO(TaskList taskList);

	public abstract List<TaskListDTO> taskListsToTaskListDTOs(List<TaskList> taskLists);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract TaskList taskListDTOToTaskList(TaskListDTO taskListDTO);

	public abstract List<TaskList> taskListDTOsToTaskLists(List<TaskListDTO> taskListDTOs);

}
