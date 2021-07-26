package com.orderfleet.webapp.web.rest.api.dto;

import java.time.ZonedDateTime;
import java.util.Set;

import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DashboardUIType;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management
 * UI.
 * 
 * @author Shaheer
 * @since May 17, 2016
 */
public class ManagedUserDTO extends UserDTO {

	public static final int PASSWORD_MIN_LENGTH = 4;
	public static final int PASSWORD_MAX_LENGTH = 60;

	private String pid;

	private String licenseKey;

	private ZonedDateTime createdDate;

	private String lastModifiedBy;

	private ZonedDateTime lastModifiedDate;

	@Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
	private String password;

	public ManagedUserDTO() {
	}

	public ManagedUserDTO(User user) {
		super(user);
		this.pid = user.getPid();
		this.createdDate = user.getCreatedDate();
		this.lastModifiedBy = user.getLastModifiedBy();
		this.lastModifiedDate = user.getLastModifiedDate();
		this.password = null;
	}

	public ManagedUserDTO(Long id, String pid, String companyPid, String companyName, String login, String password,
			String firstName, String lastName, String email, String mobile, boolean activated, String langKey,
			Set<String> authorities, ZonedDateTime createdDate, String lastModifiedBy, ZonedDateTime lastModifiedDate,
			boolean showAllUserData, DashboardUIType dashboardUIType, boolean discontinued, String deviceKey,
			String gstNumber, String companyPhoneNo, String companyAddress, String companyEmail) {
		super(pid, companyPid, companyName, login, firstName, lastName, email, mobile, activated, langKey, authorities,
				showAllUserData, dashboardUIType, discontinued, deviceKey, gstNumber, companyPhoneNo, companyAddress,
				companyEmail);
		this.pid = pid;
		this.createdDate = createdDate;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
		this.password = password;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	public ZonedDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public ZonedDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "ManagedUserDTO{" + "pid=" + pid + ", createdDate=" + createdDate + ", lastModifiedBy='" + lastModifiedBy
				+ '\'' + ", lastModifiedDate=" + lastModifiedDate + "} " + super.toString();
	}
}
