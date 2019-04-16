package com.orderfleet.webapp.web.rest.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the TaskGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public class TaskGroupDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	@NotNull
	private String activityGroupPid;

	private String activityGroupName;

	private Set<TaskDTO> tasks = new HashSet<>();
	
	public TaskGroupDTO() {
		super();
	}
	
	public TaskGroupDTO(com.orderfleet.webapp.domain.TaskGroup taskGroup) {
		this(taskGroup.getPid(), taskGroup.getName(), taskGroup.getAlias(), taskGroup.getDescription(), taskGroup.getActivityGroup().getPid(), taskGroup.getActivityGroup().getName(), taskGroup.getTasks().stream().map(TaskDTO::new).collect(Collectors.toSet()));
	}
	
	public TaskGroupDTO(String pid, String name, String alias, String description, String activityGroupPid,
			String activityGroupName, Set<TaskDTO> tasks) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.activityGroupPid = activityGroupPid;
		this.activityGroupName = activityGroupName;
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

	public String getActivityGroupPid() {
		return activityGroupPid;
	}

	public void setActivityGroupPid(String activityGroupPid) {
		this.activityGroupPid = activityGroupPid;
	}

	public String getActivityGroupName() {
		return activityGroupName;
	}

	public void setActivityGroupName(String activityGroupName) {
		this.activityGroupName = activityGroupName;
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

		TaskGroupDTO taskHeaderDTO = (TaskGroupDTO) o;

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
        return "TaskGroupDTO{" +
            ", pid='" + pid + "'" +
            ", name='" + name + "'" +
            ", alias='" + alias + "'" +
            ", description='" + description + "'" +
            '}';
    }
    
}