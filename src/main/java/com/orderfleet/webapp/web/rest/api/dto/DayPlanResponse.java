package com.orderfleet.webapp.web.rest.api.dto;

import java.util.List;

public class DayPlanResponse {

	private String userPid;

	private List<TaskGroupResponse> taskGroupResponses;

	private String status;
	private String message;

	public List<TaskGroupResponse> getTaskGroupResponses() {
		return taskGroupResponses;
	}

	public void setTaskGroupResponses(List<TaskGroupResponse> taskGroupResponses) {
		this.taskGroupResponses = taskGroupResponses;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
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
