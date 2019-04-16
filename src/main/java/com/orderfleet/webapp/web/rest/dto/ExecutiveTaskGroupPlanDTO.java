package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the ExecutiveTaskGroupPlan entity.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
public class ExecutiveTaskGroupPlanDTO {

	private String pid;

	private LocalDateTime createdDate;

	private String remarks;

	private LocalDateTime plannedDate;

	private String taskGroupPid;

	private String taskGroupName;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getTaskGroupPid() {
		return taskGroupPid;
	}

	public void setTaskGroupPid(String taskGroupPid) {
		this.taskGroupPid = taskGroupPid;
	}

	public String getTaskGroupName() {
		return taskGroupName;
	}

	public void setTaskGroupName(String taskGroupName) {
		this.taskGroupName = taskGroupName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExecutiveTaskGroupPlanDTO activityDTO = (ExecutiveTaskGroupPlanDTO) o;

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
		return "ExecutiveTaskGroupPlanDTO{" + ", pid='" + pid + "'" + ", taskGroupName='" + taskGroupName + '}';
	}
}
