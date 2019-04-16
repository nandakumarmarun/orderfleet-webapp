package com.orderfleet.webapp.web.rest.dto;

public class DayPlanDTO {

	private String name;

	private String taskType;

	private String status;

	public DayPlanDTO(String name, String taskType, String status) {
		super();
		this.name = name;
		this.taskType = taskType;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

}
