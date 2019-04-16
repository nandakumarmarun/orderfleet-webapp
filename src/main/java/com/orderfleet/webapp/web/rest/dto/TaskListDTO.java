package com.orderfleet.webapp.web.rest.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.TaskList;

public class TaskListDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private Set<TaskDTO> tasks = new HashSet<>();

	public TaskListDTO() {
		super();
	}

	public TaskListDTO(TaskList taskList) {
		this(taskList.getPid(), taskList.getName(), taskList.getAlias(), taskList.getDescription(), taskList.getTasks()
				.stream().map(TaskDTO::new).collect(Collectors.toSet()));
	}

	public TaskListDTO(String pid, String name, String alias, String description, Set<TaskDTO> tasks) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.tasks = tasks;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<TaskDTO> getTasks() {
		return tasks;
	}

	public void setTasks(Set<TaskDTO> tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TaskListDTO taskHeaderDTO = (TaskListDTO) o;

		if (!Objects.equals(pid, taskHeaderDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "TaskListDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", tasks=" + tasks + "]";
	}

}
