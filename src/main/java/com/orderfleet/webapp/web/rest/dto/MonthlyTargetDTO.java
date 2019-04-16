package com.orderfleet.webapp.web.rest.dto;

public class MonthlyTargetDTO {

	private String userPid;
	private String activityPid;
	private String activityName;
	private String userActivityTragetPid;
	private Long target;
	String monthAndYear;

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
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

	public String getUserActivityTragetPid() {
		return userActivityTragetPid;
	}

	public void setUserActivityTragetPid(String userActivityTragetPid) {
		this.userActivityTragetPid = userActivityTragetPid;
	}

	public Long getTarget() {
		return target;
	}

	public void setTarget(Long target) {
		this.target = target;
	}

	public String getMonthAndYear() {
		return monthAndYear;
	}

	public void setMonthAndYear(String monthAndYear) {
		this.monthAndYear = monthAndYear;
	}

}
