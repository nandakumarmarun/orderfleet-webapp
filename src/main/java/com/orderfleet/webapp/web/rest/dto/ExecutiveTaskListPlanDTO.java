package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;

/**
 * A DTO for the ExecutiveTaskListPlan entity.
 * 
 * @author Sarath
 * @since July 14, 2016
 */
public class ExecutiveTaskListPlanDTO {

	private String pid;

	@NotNull
	private LocalDateTime createdDate;

	private String remarks;

	@NotNull
	private LocalDateTime plannedDate;

	private String taskListPid;

	private String taskListName;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
	}

	public String getTaskListPid() {
		return taskListPid;
	}

	public void setTaskListPid(String taskListPid) {
		this.taskListPid = taskListPid;
	}

	public String getTaskListName() {
		return taskListName;
	}

	public void setTaskListName(String taskListName) {
		this.taskListName = taskListName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExecutiveTaskListPlanDTO activityDTO = (ExecutiveTaskListPlanDTO) o;

		if (!Objects.equals(pid, activityDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "ExecutiveTaskListPlanDTO{" + ", pid='" + pid + "'" + ", taskListName='" + taskListName + '}';
	}
}
