package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserCustomerGroup;

public class UserCustomerGroupDTO {

	private String stagePid;
	private String stageName;
	private String userPid;
	private String userName;

	public UserCustomerGroupDTO() {
		super();
	}

	public UserCustomerGroupDTO(String stagePid, String stageName, String userPid, String userName) {
		super();
		this.stagePid = stagePid;
		this.stageName = stageName;
		this.userPid = userPid;
		this.userName = userName;

	}

	public UserCustomerGroupDTO(UserCustomerGroup userStage) {
		this.stagePid = userStage.getStage().getPid();
		this.stageName = userStage.getStage().getName();
		this.userPid = userStage.getUser().getPid();
		this.userName = userStage.getUser().getFirstName() + " " + userStage.getUser().getLastName();
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

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


}
