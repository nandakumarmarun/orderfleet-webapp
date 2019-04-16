package com.orderfleet.webapp.web.rest.api.dto;

import java.time.LocalDate;

public class TaskGroupResponse {

	private String activityGroupPid;
	private String activityGroupName;
	private long target;
	private long achieved;
	private LocalDate startDate;
	private LocalDate endDate;

	public String getActivityGroupPid() {
		return activityGroupPid;
	}

	public void setActivityGroupPid(String activityGroupPid) {
		this.activityGroupPid = activityGroupPid;
	}

	public String getActivityGroupName() {
		return activityGroupName;
	}

	public void setActivityGroupName(String activityGroupName) {
		this.activityGroupName = activityGroupName;
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
}
