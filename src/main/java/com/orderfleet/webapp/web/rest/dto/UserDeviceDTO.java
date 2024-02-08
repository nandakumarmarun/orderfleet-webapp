package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.UserDevice;

/**
 * A DTO for the UserDevice entity.
 * 
 * @author Sarath
 * @since Sep 19, 2016
 */
public class UserDeviceDTO {

	private String pid;
	private String deviceKey;
	private String deviceName;
	private String fcmKey;
	private String companyPid;
	private String companyName;
	private String userLoginName;
	private String userPid;
	private String userFirstName;
	private String userPhone;
	private LocalDateTime createdDate;
	private boolean activated;
	private LocalDateTime lastModifiedDate;
	private String lastAcessedLogin;
	private String lastAcessedDeviceKey;
	private String employeeName;
	private String buildVersion;

	public UserDeviceDTO() {
		super();
	}

	public UserDeviceDTO(String pid, String deviceKey, String deviceName, String fcmKey, String companyPid,
			String companyName, String userLoginName, String userPid) {
		super();
		this.pid = pid;
		this.deviceKey = deviceKey;
		this.deviceName = deviceName;
		this.fcmKey = fcmKey;
		this.companyPid = companyPid;
		this.companyName = companyName;
		this.userLoginName = userLoginName;
		this.userPid = userPid;
	}

	public UserDeviceDTO(UserDevice userDevice) {
		super();
		this.pid = userDevice.getPid();
		this.deviceKey = userDevice.getDeviceKey();
		this.deviceName = userDevice.getDeviceName();
		this.fcmKey = userDevice.getFcmKey();
		this.companyPid = userDevice.getCompany().getPid();
		this.companyName = userDevice.getCompany().getLegalName();
		this.userLoginName = userDevice.getUser().getLogin();
		this.userFirstName = userDevice.getUser().getFirstName();
		this.userPid = userDevice.getUser().getPid();
		this.userPhone = userDevice.getUser().getMobile();
		this.activated = userDevice.isActivated();
		this.createdDate = userDevice.getCreatedDate();
		this.lastModifiedDate = userDevice.getLastModifiedDate();
		this.lastAcessedDeviceKey = userDevice.getLastAcessedDeviceKey();
		this.lastAcessedLogin = userDevice.getLastAcessedLogin();
		this.buildVersion = userDevice.getBuildVersion();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFcmKey() {
		return fcmKey;
	}

	public void setFcmKey(String fcmKey) {
		this.fcmKey = fcmKey;
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

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLastAcessedLogin() {
		return lastAcessedLogin;
	}

	public void setLastAcessedLogin(String lastAcessedLogin) {
		this.lastAcessedLogin = lastAcessedLogin;
	}

	public String getLastAcessedDeviceKey() {
		return lastAcessedDeviceKey;
	}

	public void setLastAcessedDeviceKey(String lastAcessedDeviceKey) {
		this.lastAcessedDeviceKey = lastAcessedDeviceKey;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	@Override
	public String toString() {
		return "UserDeviceDTO [pid=" + pid + ", deviceKey=" + deviceKey + ", deviceName=" + deviceName + ", fcmKey="
				+ fcmKey + ", companyPid=" + companyPid + ", companyName=" + companyName + ", userLoginName="
				+ userLoginName + ", userPid=" + userPid + ", userFirstName=" + userFirstName + ", userPhone="
				+ userPhone + ", createdDate=" + createdDate + ", activated=" + activated + ", lastModifiedDate="
				+ lastModifiedDate + ", lastAcessedLogin=" + lastAcessedLogin + ", lastAcessedDeviceKey="
				+ lastAcessedDeviceKey + ", employeeName=" + employeeName +"]";
	}

}
