package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.domain.enums.TaskStatus;

/**
 * A DTO for the UserTaskAssignment entity.
 * 
 * @author Muhammed Riyas T
 * @since June 20, 2016
 */
public class UserTaskAssignmentDTO {

	private String pid;

	private String userPid;

	private String userName;

	@NotNull
	private String executiveUserPid;

	private String executiveUserName;

	@NotNull
	private String taskPid;

	private String taskActivityName;

	private PriorityStatus priorityStatus;

	@NotNull
	private LocalDate startDate;

	private String remarks;

	private String taskAccountName;

	private TaskStatus taskStatus;

	private List<LocalDateTime> userTaskExecutionLogs;

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

	public String getTaskPid() {
		return taskPid;
	}

	public void setTaskPid(String taskPid) {
		this.taskPid = taskPid;
	}

	public String getTaskActivityName() {
		return taskActivityName;
	}

	public void setTaskActivityName(String taskActivityName) {
		this.taskActivityName = taskActivityName;
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

	public String getTaskAccountName() {
		return taskAccountName;
	}

	public void setTaskAccountName(String taskAccountName) {
		this.taskAccountName = taskAccountName;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public List<LocalDateTime> getUserTaskExecutionLogs() {
		return userTaskExecutionLogs;
	}

	public void setUserTaskExecutionLogs(List<LocalDateTime> userTaskExecutionLogs) {
		this.userTaskExecutionLogs = userTaskExecutionLogs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UserTaskAssignmentDTO activityDTO = (UserTaskAssignmentDTO) o;

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
		return "UserTaskAssignmentDTO{" + ", pid='" + pid + "'" + ", taskActivityName='" + taskActivityName + "'"
				+ ", executiveUserName='" + executiveUserName + "'" + ", remarks='" + remarks + "'" + '}';
	}
}
