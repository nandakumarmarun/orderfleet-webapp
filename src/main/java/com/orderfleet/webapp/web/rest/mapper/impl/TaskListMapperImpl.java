package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;
import com.orderfleet.webapp.web.rest.mapper.TaskListMapper;
import com.orderfleet.webapp.web.rest.mapper.TaskMapper;

@Component
public class TaskListMapperImpl extends TaskListMapper {

	@Autowired
	private TaskMapper taskMapper;

	@Override
	public TaskListDTO taskListToTaskListDTO(TaskList taskList) {
		if (taskList == null) {
			return null;
		}

		TaskListDTO taskListDTO = new TaskListDTO();

		taskListDTO.setPid(taskList.getPid());
		taskListDTO.setName(taskList.getName());
		taskListDTO.setAlias(taskList.getAlias());
		taskListDTO.setDescription(taskList.getDescription());
		Set<TaskDTO> set = taskSetToTaskDTOSet(taskList.getTasks());
		if (set != null) {
			taskListDTO.setTasks(set);
		}

		return taskListDTO;
	}

	public TaskListDTO taskListToTaskListDTODescription(TaskList taskList) {
		if (taskList == null) {
			return null;
		}

		TaskListDTO taskListDTO = new TaskListDTO();

		taskListDTO.setPid(taskList.getPid());
		taskListDTO.setName(taskList.getDescription() != null && !taskList.getDescription().equalsIgnoreCase("common")
				? taskList.getDescription()
				: taskList.getName());
		taskListDTO.setAlias(taskList.getAlias());
		taskListDTO.setDescription(taskList.getDescription());
		Set<TaskDTO> set = taskSetToTaskDTOSet(taskList.getTasks());
		if (set != null) {
			taskListDTO.setTasks(set);
		}

		return taskListDTO;
	}

	@Override
	public List<TaskListDTO> taskListsToTaskListDTOs(List<TaskList> taskLists) {
		if (taskLists == null) {
			return null;
		}

		List<TaskListDTO> list = new ArrayList<TaskListDTO>();
		if (getCompanyCofig()) {
			for (TaskList taskList : taskLists) {
				list.add(taskListToTaskListDTODescription(taskList));
			}
		} else {
			for (TaskList taskList : taskLists) {
				list.add(taskListToTaskListDTO(taskList));
			}
		}

		return list;
	}

	@Override
	public TaskList taskListDTOToTaskList(TaskListDTO taskListDTO) {
		if (taskListDTO == null) {
			return null;
		}

		TaskList taskList = new TaskList();

		taskList.setPid(taskListDTO.getPid());
		taskList.setName(taskListDTO.getName());
		taskList.setAlias(taskListDTO.getAlias());
		taskList.setDescription(taskListDTO.getDescription());
		Set<Task> set = taskDTOSetToTaskSet(taskListDTO.getTasks());
		if (set != null) {
			taskList.setTasks(set);
		}

		return taskList;
	}

	@Override
	public List<TaskList> taskListDTOsToTaskLists(List<TaskListDTO> taskListDTOs) {
		if (taskListDTOs == null) {
			return null;
		}

		List<TaskList> list = new ArrayList<TaskList>();
		for (TaskListDTO taskListDTO : taskListDTOs) {
			list.add(taskListDTOToTaskList(taskListDTO));
		}

		return list;
	}

	protected Set<TaskDTO> taskSetToTaskDTOSet(Set<Task> set) {
		if (set == null) {
			return null;
		}

		Set<TaskDTO> set_ = new HashSet<TaskDTO>();
		for (Task task : set) {
			set_.add(taskMapper.taskToTaskDTO(task));
		}

		return set_;
	}

	protected Set<Task> taskDTOSetToTaskSet(Set<TaskDTO> set) {
		if (set == null) {
			return null;
		}

		Set<Task> set_ = new HashSet<Task>();
		for (TaskDTO taskDTO : set) {
			set_.add(taskMapper.taskDTOToTask(taskDTO));
		}

		return set_;
	}

	private String taskListName(TaskList taskList) {
		if (taskList.getDescription() != null && getCompanyCofig() && !taskList.getDescription().equals("common")) {
			return taskList.getDescription();
		}

		return taskList.getName();
	}

}
