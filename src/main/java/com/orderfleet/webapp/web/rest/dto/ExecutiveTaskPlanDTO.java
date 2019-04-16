package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;

/**
 * A DTO for the ExecutiveTaskPlan entity.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
public class ExecutiveTaskPlanDTO {

	private String pid;

	private LocalDateTime createdDate;

	private String createdBy;

	private TaskPlanStatus taskPlanStatus;

	private String remarks;

	private String userRemarks;

	private LocalDateTime plannedDate;

	@NotNull
	private String activityPid;

	private String activityName;

	private String accountTypePid;

	private String accountTypeName;

	@NotNull
	private String accountProfilePid;

	private String accountProfileName;

	@NotNull
	private TaskCreatedType taskCreatedType;

	private String taskPid;

	private String taskGroupPid;

	private String taskListPid;
	
	private String taskListAlias;

	private int sortOrder;

	public ExecutiveTaskPlanDTO() {
		super();
	}

	public ExecutiveTaskPlanDTO(ExecutiveTaskPlan executiveTaskPlan) {
		this(executiveTaskPlan.getPid(), executiveTaskPlan.getCreatedDate(), executiveTaskPlan.getCreatedBy(),
				executiveTaskPlan.getTaskPlanStatus(), executiveTaskPlan.getRemarks(),
				executiveTaskPlan.getPlannedDate(), executiveTaskPlan.getActivity().getPid(),
				executiveTaskPlan.getActivity().getName(), executiveTaskPlan.getAccountType().getPid(),
				executiveTaskPlan.getAccountType().getName(), executiveTaskPlan.getAccountProfile().getPid(),
				executiveTaskPlan.getAccountProfile().getName(),
				executiveTaskPlan.getTask() == null ? null : executiveTaskPlan.getTask().getPid(),
				executiveTaskPlan.getTaskGroup() == null ? null : executiveTaskPlan.getTaskGroup().getPid(),
				executiveTaskPlan.getTaskList() == null ? null : executiveTaskPlan.getTaskList().getPid(),
				executiveTaskPlan.getSortOrder(), executiveTaskPlan.getUserRemarks(),executiveTaskPlan.getTaskList() == null ? null : executiveTaskPlan.getTaskList().getAlias());
	}

	public ExecutiveTaskPlanDTO(String pid, LocalDateTime createdDate, String createdBy, TaskPlanStatus taskPlanStatus,
			String remarks, LocalDateTime plannedDate, String activityPid, String activityName, String accountTypePid,
			String accountTypeName, String accountProfilePid, String accountProfileName, String taskPid,
			String taskGroupPid, String taskListPid, int sortOrder, String userRemarks,String taskListAlias) {
		super();
		this.pid = pid;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.taskPlanStatus = taskPlanStatus;
		this.remarks = remarks;
		this.plannedDate = plannedDate;
		this.activityPid = activityPid;
		this.activityName = activityName;
		this.accountTypePid = accountTypePid;
		this.accountTypeName = accountTypeName;
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.taskPid = taskPid;
		this.taskGroupPid = taskGroupPid;
		this.taskListPid = taskListPid;
		this.sortOrder = sortOrder;
		this.userRemarks = userRemarks;
		this.taskListAlias=taskListAlias;
	}

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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public TaskPlanStatus getTaskPlanStatus() {
		return taskPlanStatus;
	}

	public void setTaskPlanStatus(TaskPlanStatus taskPlanStatus) {
		this.taskPlanStatus = taskPlanStatus;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getAccountTypePid() {
		return accountTypePid;
	}

	public void setAccountTypePid(String accountTypePid) {
		this.accountTypePid = accountTypePid;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public TaskCreatedType getTaskCreatedType() {
		return taskCreatedType;
	}

	public void setTaskCreatedType(TaskCreatedType taskCreatedType) {
		this.taskCreatedType = taskCreatedType;
	}

	public String getTaskPid() {
		return taskPid;
	}

	public void setTaskPid(String taskPid) {
		this.taskPid = taskPid;
	}

	public String getTaskGroupPid() {
		return taskGroupPid;
	}

	public void setTaskGroupPid(String taskGroupPid) {
		this.taskGroupPid = taskGroupPid;
	}

	public String getTaskListPid() {
		return taskListPid;
	}

	public void setTaskListPid(String taskListPid) {
		this.taskListPid = taskListPid;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getUserRemarks() {
		return userRemarks;
	}

	public void setUserRemarks(String userRemarks) {
		this.userRemarks = userRemarks;
	}

	
	
	public String getTaskListAlias() {
		return taskListAlias;
	}

	public void setTaskListAlias(String taskListAlias) {
		this.taskListAlias = taskListAlias;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExecutiveTaskPlanDTO activityDTO = (ExecutiveTaskPlanDTO) o;

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
		return "ExecutiveTaskPlanDTO [pid=" + pid + ", createdDate=" + createdDate + ", createdBy=" + createdBy
				+ ", taskPlanStatus=" + taskPlanStatus + ", remarks=" + remarks + ", userRemarks=" + userRemarks
				+ ", plannedDate=" + plannedDate + ", activityPid=" + activityPid + ", activityName=" + activityName
				+ ", accountTypePid=" + accountTypePid + ", accountTypeName=" + accountTypeName + ", accountProfilePid="
				+ accountProfilePid + ", accountProfileName=" + accountProfileName + ", taskCreatedType="
				+ taskCreatedType + ", taskPid=" + taskPid + ", taskGroupPid=" + taskGroupPid + ", taskListPid="
				+ taskListPid + ", taskListAlias=" + taskListAlias + ", sortOrder=" + sortOrder + "]";
	}

}
