package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TaskSubmissionResponse {

	private String executiveTaskExecutionPid;
	private long target;
	private long achieved;
	private LocalDate startDate;
	private LocalDate endDate;
	@JsonIgnore
	private Long userId;
	@JsonIgnore
	private Long activityId;
	private String status;
	private String message;

	public TaskSubmissionResponse() {
	}

	public String getExecutiveTaskExecutionPid() {
		return executiveTaskExecutionPid;
	}

	public void setExecutiveTaskExecutionPid(String executiveTaskExecutionPid) {
		this.executiveTaskExecutionPid = executiveTaskExecutionPid;
	}

	public long getTarget() {
		return target;
	}

	public void setTarget(long target) {
		this.target = target;
	}

	public long getAchieved() {
		return achieved;
	}

	public void setAchieved(long achieved) {
		this.achieved = achieved;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
