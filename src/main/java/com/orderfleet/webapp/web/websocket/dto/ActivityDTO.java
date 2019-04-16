package com.orderfleet.webapp.web.websocket.dto;

/**
 * DTO for storing a user's activity.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public class ActivityDTO {

	private String userPid;

	private String sessionId;

	private String userLogin;

	private String ipAddress;

	private String page;

	private String time;

	private String remarks;
	
	private Long dashboardItemId;
	
	private String attendanceSubGroupName;
	
	private String attendanceSubGroupCode;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getDashboardItemId() {
		return dashboardItemId;
	}

	public void setDashboardItemId(Long dashboardItemId) {
		this.dashboardItemId = dashboardItemId;
	}

	public String getAttendanceSubGroupName() {
		return attendanceSubGroupName;
	}

	public void setAttendanceSubGroupName(String attendanceSubGroupName) {
		this.attendanceSubGroupName = attendanceSubGroupName;
	}

	public String getAttendanceSubGroupCode() {
		return attendanceSubGroupCode;
	}

	public void setAttendanceSubGroupCode(String attendanceSubGroupCode) {
		this.attendanceSubGroupCode = attendanceSubGroupCode;
	}

	@Override
	public String toString() {
		return "ActivityDTO{" + "sessionId='" + sessionId + '\'' + ", userLogin='" + userLogin + '\'' + ", ipAddress='"
				+ ipAddress + '\'' + ", page='" + page + '\'' + ", time='" + time + '\'' + '}';
	}
}