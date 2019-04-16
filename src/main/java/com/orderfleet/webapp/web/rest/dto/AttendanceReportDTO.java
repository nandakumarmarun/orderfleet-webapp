package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceReportDTO {

	private LocalDate attendanceDay;
	
	private String employeeName;
	
	private String attendanceStatus;
	
	private String attendanceSubGroupName;

	private LocalDateTime plannedDate;
	
	private LocalDateTime createdDate;
	
	private boolean completed;
	
	private String remarks;
	
	private String location;
	
	private String towerLocation;
	
	public LocalDate getAttendanceDay() {
		return attendanceDay;
	}

	public void setAttendanceDay(LocalDate attendanceDay) {
		this.attendanceDay = attendanceDay;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}
	
	public String getAttendanceSubGroupName() {
		return attendanceSubGroupName;
	}

	public void setAttendanceSubGroupName(String attendanceSubGroupName) {
		this.attendanceSubGroupName = attendanceSubGroupName;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTowerLocation() {
		return towerLocation;
	}

	public void setTowerLocation(String towerLocation) {
		this.towerLocation = towerLocation;
	}
	
}
