package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.enums.LocationType;
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
	private LocalDate TaskDate;
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
	
	private LocalDateTime serverDate;
	
	private String executiveTaskExecutionPid;
	
	private String geoLocation;
	
	private String geoTagLocation;
	
	private BigDecimal executionLatitude;
	
	private BigDecimal executionLongitude;
	
	private BigDecimal accountLatitude;
	
	private BigDecimal accountLongitude;
	
	private LocationType locationType;
	
	private String variance;

	public ExecutiveTaskPlanDTO() {
		super();
	}

	public ExecutiveTaskPlanDTO(ExecutiveTaskPlan executiveTaskPlan) {
		this(executiveTaskPlan.getPid(), executiveTaskPlan.getCreatedDate(), executiveTaskPlan.getCreatedBy(),
				executiveTaskPlan.getTaskPlanStatus(), executiveTaskPlan.getRemarks(),
				executiveTaskPlan.getPlannedDate(),executiveTaskPlan.getPlannedDate().toLocalDate(),executiveTaskPlan.getActivity().getPid(),
				executiveTaskPlan.getActivity().getName(), executiveTaskPlan.getAccountType().getPid(),
				executiveTaskPlan.getAccountType().getName(), executiveTaskPlan.getAccountProfile().getPid(),
				executiveTaskPlan.getAccountProfile().getName(),
				executiveTaskPlan.getTask() == null ? null : executiveTaskPlan.getTask().getPid(),
				executiveTaskPlan.getTaskGroup() == null ? null : executiveTaskPlan.getTaskGroup().getPid(),
				executiveTaskPlan.getTaskList() == null ? null : executiveTaskPlan.getTaskList().getPid(),
				executiveTaskPlan.getSortOrder(), executiveTaskPlan.getUserRemarks(),executiveTaskPlan.getTaskList() == null ? null : executiveTaskPlan.getTaskList().getAlias(),
						executiveTaskPlan.getAccountProfile().getLatitude(),executiveTaskPlan.getAccountProfile().getLongitude());
	}

	public ExecutiveTaskPlanDTO(String pid, LocalDateTime createdDate, String createdBy, TaskPlanStatus taskPlanStatus,
			String remarks, LocalDateTime plannedDate,LocalDate taskDate, String activityPid, String activityName, String accountTypePid,
			String accountTypeName, String accountProfilePid, String accountProfileName, String taskPid,
			String taskGroupPid, String taskListPid, int sortOrder, String userRemarks,String taskListAlias,BigDecimal accountLatitude,BigDecimal accountLongitude) {
		super();
		this.pid = pid;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.taskPlanStatus = taskPlanStatus;
		this.remarks = remarks;
		this.plannedDate = plannedDate;
		this.TaskDate = taskDate;
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
		this.accountLatitude = accountLatitude;
		this.accountLongitude = accountLongitude;
	}
	

	public ExecutiveTaskPlanDTO(String pid, LocalDateTime createdDate, String createdBy, TaskPlanStatus taskPlanStatus,
			String remarks, String userRemarks, LocalDateTime plannedDate, String activityPid, String activityName,
			String accountTypePid, String accountTypeName, String accountProfilePid, String accountProfileName,
			TaskCreatedType taskCreatedType, String taskPid, String taskGroupPid, String taskListPid,
			String taskListAlias, int sortOrder, LocalDateTime serverDate, String executiveTaskExecutionPid,
			String geoLocation, String geoTagLocation, BigDecimal executionLatitude, BigDecimal executionLongitude,
			BigDecimal accountLatitude, BigDecimal accountLongitude, LocationType locationType) {
		super();
		this.pid = pid;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.taskPlanStatus = taskPlanStatus;
		this.remarks = remarks;
		this.userRemarks = userRemarks;
		this.plannedDate = plannedDate;
		this.activityPid = activityPid;
		this.activityName = activityName;
		this.accountTypePid = accountTypePid;
		this.accountTypeName = accountTypeName;
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.taskCreatedType = taskCreatedType;
		this.taskPid = taskPid;
		this.taskGroupPid = taskGroupPid;
		this.taskListPid = taskListPid;
		this.taskListAlias = taskListAlias;
		this.sortOrder = sortOrder;
		this.serverDate = serverDate;
		this.executiveTaskExecutionPid = executiveTaskExecutionPid;
		this.geoLocation = geoLocation;
		this.geoTagLocation = geoTagLocation;
		this.executionLatitude = executionLatitude;
		this.executionLongitude = executionLongitude;
		this.accountLatitude = accountLatitude;
		this.accountLongitude = accountLongitude;
		this.locationType = locationType;
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

	public LocalDateTime getServerDate() {
		return serverDate;
	}

	public void setServerDate(LocalDateTime serverDate) {
		this.serverDate = serverDate;
	}

	public String getExecutiveTaskExecutionPid() {
		return executiveTaskExecutionPid;
	}

	public void setExecutiveTaskExecutionPid(String executiveTaskExecutionPid) {
		this.executiveTaskExecutionPid = executiveTaskExecutionPid;
	}
	

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getGeoTagLocation() {
		return geoTagLocation;
	}

	public void setGeoTagLocation(String geoTagLocation) {
		this.geoTagLocation = geoTagLocation;
	}

	
	public BigDecimal getExecutionLatitude() {
		return executionLatitude;
	}

	public void setExecutionLatitude(BigDecimal executionLatitude) {
		this.executionLatitude = executionLatitude;
	}

	public BigDecimal getExecutionLongitude() {
		return executionLongitude;
	}

	public void setExecutionLongitude(BigDecimal executionLongitude) {
		this.executionLongitude = executionLongitude;
	}

	public BigDecimal getAccountLatitude() {
		return accountLatitude;
	}

	public void setAccountLatitude(BigDecimal accountLatitude) {
		this.accountLatitude = accountLatitude;
	}

	public BigDecimal getAccountLongitude() {
		return accountLongitude;
	}

	public void setAccountLongitude(BigDecimal accountLongitude) {
		this.accountLongitude = accountLongitude;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public String getVariance() {
		return variance;
	}

	public void setVariance(String variance) {
		this.variance = variance;
	}

	public LocalDate getTaskDate() {
		return TaskDate;
	}

	public void setTaskDate(LocalDate taskDate) {
		TaskDate = taskDate;
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
		return "ExecutiveTaskPlanDTO [createdDate=" + createdDate + ", plannedDate=" + plannedDate + ", activityName="
				+ activityName + ", accountTypeName=" + accountTypeName + ", accountProfilePid=" + accountProfilePid
				+ ", accountProfileName=" + accountProfileName + ", sortOrder=" + sortOrder + ", serverDate="
				+ serverDate + ", executiveTaskExecutionPid=" + executiveTaskExecutionPid + ", geoLocation="
				+ geoLocation + ", geoTagLocation=" + geoTagLocation + ", executionLatitude=" + executionLatitude
				+ ", executionLongitude=" + executionLongitude + ", accountLatitude=" + accountLatitude
				+ ", accountLongitude=" + accountLongitude + ", locationType=" + locationType + "]";
	}

	

	

}
