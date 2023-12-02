package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.MobileConfiguration;
import com.orderfleet.webapp.domain.UserWiseMobileConfiguration;
import com.orderfleet.webapp.domain.enums.*;

/**
 * A DTO For MobileConfiguration.
 *
 * @author Resmi T R
 * @since Nov 27, 2023
 *
 */
public class UserWiseMobileConfigurationDTO {

	private String pid;
	private String companyPid;
	private String companyName;
	private String userPid;
	private String userName;
	private boolean liveRouting;


	public UserWiseMobileConfigurationDTO() {
		super();
	}

	public UserWiseMobileConfigurationDTO(UserWiseMobileConfiguration mobileConfiguration) {
		super();
		this.pid = mobileConfiguration.getPid();
		this.companyPid = mobileConfiguration.getCompany().getPid();
		this.companyName = mobileConfiguration.getCompany().getLegalName();
		this.userPid = mobileConfiguration.getUser().getPid();
		this.userName = mobileConfiguration.getUser().getFirstName();
		this.liveRouting = mobileConfiguration.isLiveRouting();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public boolean isLiveRouting() {
		return liveRouting;
	}

	public void setLiveRouting(boolean liveRouting) {
		this.liveRouting = liveRouting;
	}
}