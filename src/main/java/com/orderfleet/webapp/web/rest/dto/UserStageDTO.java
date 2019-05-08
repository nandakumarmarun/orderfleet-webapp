package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AccountGroupAccountProfile;
import com.orderfleet.webapp.domain.UserStage;

public class UserStageDTO {

	private String stagePid;
	private String stageName;
	private String userPid;
	private String userName;
	private LocalDateTime lastModifiedDate;

	public UserStageDTO() {
		super();
	}

	public UserStageDTO(String stagePid, String stageName, String userPid, String userName) {
		super();
		this.stagePid = stagePid;
		this.stageName = stageName;
		this.userPid = userPid;
		this.userName = userName;

	}

	public UserStageDTO(UserStage userStage) {
		this.stagePid = userStage.getStage().getPid();
		this.stageName = userStage.getStage().getName();
		this.userPid = userStage.getUser().getPid();
		this.userName = userStage.getUser().getFirstName() + " " + userStage.getUser().getLastName();
		this.lastModifiedDate = userStage.getLastModifiedDate();
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

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
