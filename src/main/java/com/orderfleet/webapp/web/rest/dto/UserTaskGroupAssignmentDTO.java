package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.enums.PriorityStatus;

/**
 * A DTO for the UserTaskAssignment entity.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
public class UserTaskGroupAssignmentDTO {

	private String pid;

	private String userPid;

	private String userName;

	@NotNull
	private String executiveUserPid;

	private String executiveUserName;

	@NotNull
	private String taskGroupPid;

	private String taskGroupName;

	private PriorityStatus priorityStatus;

	@NotNull
	private LocalDate startDate;

	private String remarks;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getExecutiveUserPid() {
		return executiveUserPid;
	}

	public void setExecutiveUserPid(String executiveUserPid) {
		this.executiveUserPid = executiveUserPid;
	}

	public String getExecutiveUserName() {
		return executiveUserName;
	}

	public void setExecutiveUserName(String executiveUserName) {
		this.executiveUserName = executiveUserName;
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

	public PriorityStatus getPriorityStatus() {
		return priorityStatus;
	}

	public void setPriorityStatus(PriorityStatus priorityStatus) {
		this.priorityStatus = priorityStatus;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserTaskGroupAssignmentDTO activityDTO = (UserTaskGroupAssignmentDTO) o;

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
		return "UserTaskAssignmentDTO{" + ", pid='" + pid + "'" + ", taskGroupName='" + taskGroupName + "'"
				+ ", executiveUserName='" + executiveUserName + "'" + ", remarks='" + remarks + "'" + '}';
	}
}
