package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

public class TimeUtilizationDTO {

	private String description;
	
	private LocalDateTime fromDateTime;
	
	private LocalDateTime toDateTime;
	
	private LocalDateTime date;
	
	private String timeSpent;
	
	private int sortValue;

	
	public TimeUtilizationDTO() {
		super();
	}

	public TimeUtilizationDTO(ExecutiveTaskExecutionView executiveTaskExecutionView) {
		super();
		this.description = executiveTaskExecutionView.getActivityName();
		this.fromDateTime = executiveTaskExecutionView.getStartTime();
		this.toDateTime = executiveTaskExecutionView.getEndTime();
		this.date=executiveTaskExecutionView.getPlannedDate();
		this.timeSpent = executiveTaskExecutionView.getTimeSpend();
	}
	
	public TimeUtilizationDTO(String description, LocalDateTime fromDateTime, LocalDateTime toDateTime, LocalDateTime date, String timeSpent,int sortValue) {
		super();
		this.description = description;
		this.fromDateTime = fromDateTime;
		this.toDateTime = toDateTime;
		this.date=date;
		this.timeSpent = timeSpent;
		this.sortValue=sortValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(LocalDateTime fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	public LocalDateTime getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(LocalDateTime toDateTime) {
		this.toDateTime = toDateTime;
	}

	public int getSortValue() {
		return sortValue;
	}

	public void setSortValue(int sortValue) {
		this.sortValue = sortValue;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}

	@Override
	public String toString() {
		return "TimeUtilizationDTO [description=" + description + ", fromDateTime=" + fromDateTime + ", toDateTime="
				+ toDateTime + ", date=" + date + ", timeSpent=" + timeSpent + ", sortValue=" + sortValue + "]";
	}
}
