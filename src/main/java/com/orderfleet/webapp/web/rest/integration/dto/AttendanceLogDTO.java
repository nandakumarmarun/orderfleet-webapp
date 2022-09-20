package com.orderfleet.webapp.web.rest.integration.dto;

import java.time.LocalDateTime;

public class AttendanceLogDTO {
	private String pid;
	private String employeeName;
	private LocalDateTime attendanceDate;
	private String orgEmpId;
	private LocalDateTime punchOutDate;
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public LocalDateTime getAttendanceDate() {
		return attendanceDate;
	}
	public void setAttendanceDate(LocalDateTime attendanceDate) {
		this.attendanceDate = attendanceDate;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getOrgEmpId() {
		return orgEmpId;
	}
	public void setOrgEmpId(String orgEmpId) {
		this.orgEmpId = orgEmpId;
	}
	public LocalDateTime getPunchOutDate() {
		return punchOutDate;
	}
	public void setPunchOutDate(LocalDateTime punchOutDate) {
		this.punchOutDate = punchOutDate;
	}
	
	
	
	
	

}
