package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.DashboardUser;

public class DashboardUserDTO {

	String userPid;
	int sortOrder;

	public DashboardUserDTO() {
		super();
	}

	public DashboardUserDTO(DashboardUser dashboardUser) {
		this.userPid = dashboardUser.getUser().getPid();
		this.sortOrder = dashboardUser.getSortOrder();
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}
