package com.orderfleet.webapp.web.rest.dto;

public class MonthlyCustomerGroupTargetDTO {

	private String userPid;
	private String stagePid;
	private String stageName;
	private String userCustomerGroupTragetPid;
	private Long target;
	String monthAndYear;

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getStagePid() {
		return stagePid;
	}

	public void setStagePid(String stagePid) {
		this.stagePid = stagePid;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getUserCustomerGroupTragetPid() {
		return userCustomerGroupTragetPid;
	}

	public void setUserCustomerGroupTragetPid(String userCustomerGroupTragetPid) {
		this.userCustomerGroupTragetPid = userCustomerGroupTragetPid;
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
