package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.UserAccountProfile;

/**
 * A DTO for the UserAccountProfile entity.
 *
 * @author Sarath
 * @since Oct 24, 2016
 */
public class UserAccountProfileDTO {

	private String userPid;
	private String userName;
	private String AccountProfilePid;
	private String AccountProfileName;

	public UserAccountProfileDTO() {
		super();
	}

	public UserAccountProfileDTO(String userPid, String userName, String accountProfilePid, String accountProfileName) {
		super();
		this.userPid = userPid;
		this.userName = userName;
		AccountProfilePid = accountProfilePid;
		AccountProfileName = accountProfileName;
	}

	public UserAccountProfileDTO(UserAccountProfile userAccountProfile) {
		super();
		this.userPid = userAccountProfile.getUser().getPid();
		this.userName = userAccountProfile.getUser().getFirstName();
		AccountProfilePid = userAccountProfile.getAccountProfile().getPid();
		AccountProfileName = userAccountProfile.getAccountProfile().getName();
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

	public String getAccountProfilePid() {
		return AccountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		AccountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return AccountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		AccountProfileName = accountProfileName;
	}

	@Override
	public String toString() {
		return "UserAccountProfileDTO [userPid=" + userPid + ", userName=" + userName + ", AccountProfilePid="
				+ AccountProfilePid + ", AccountProfileName=" + AccountProfileName + "]";
	}

}
