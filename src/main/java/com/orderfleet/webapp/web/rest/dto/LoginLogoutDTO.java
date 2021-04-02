package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.PunchOut;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * A DTO for the Attendance entity.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
public class LoginLogoutDTO {

	private String pid;
	private LocalDateTime createdDate;
	private boolean completed;
	private String remarks;
	private LocalDateTime plannedDate;
	private String attendanceStatus;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private LocationType locationType;
	private String location;
	private String employeeName;
	private String towerLocation;
	private BigDecimal towerLatitude;
	private BigDecimal towerLongitude;
	private String imageRefNo;
	private Boolean imageButtonVisible = Boolean.FALSE;;
	private LocalDateTime punchoutServerDate;
	private LocalDateTime punchoutClientDate;
	private LocalDate attendanceDay;
	private String punchoutRemarks;
	private String punchoutStatus;

	public LoginLogoutDTO() {
		super();
	}

	public LoginLogoutDTO(String pid, String login, String userPid, String userName, LocalDateTime createdDate,
			boolean completed, String remarks, LocalDateTime plannedDate, String attendanceStatus, BigDecimal latitude,
			BigDecimal longitude, String mnc, String mcc, String cellId, String lac, LocationType locationType,
			String location, String attendanceSubGroupName, String towerLocation, BigDecimal towerLatitude,
			BigDecimal towerLongitude, LocalDateTime punchoutServerDate, LocalDateTime punchoutClientDate,
			String punchoutRemarks) {
		super();
		this.pid = pid;
		this.createdDate = createdDate;
		this.completed = completed;
		this.remarks = remarks;
		this.plannedDate = plannedDate;
		this.attendanceStatus = attendanceStatus;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationType = locationType;
		this.location = location;
		this.towerLocation = towerLocation;
		this.towerLatitude = towerLatitude;
		this.towerLongitude = towerLongitude;
		this.punchoutServerDate = punchoutServerDate;
		this.punchoutClientDate = punchoutClientDate;
		this.punchoutRemarks = punchoutRemarks;
	}

	public LoginLogoutDTO(Attendance attendance, PunchOut punchout) {
		super();
		this.pid = attendance.getPid();
		this.createdDate = attendance.getCreatedDate();
		this.completed = attendance.getIsCompleted();
		this.remarks = attendance.getRemarks();
		this.plannedDate = attendance.getPlannedDate();
		this.attendanceStatus = attendance.getAttendanceStatus().toString();
		this.latitude = attendance.getLatitude();
		this.longitude = attendance.getLongitude();
		this.locationType = attendance.getLocationType();
		this.location = attendance.getLocation();
		this.towerLocation = attendance.getTowerLocation();
		this.towerLatitude = attendance.getTowerLatitude();
		this.towerLongitude = attendance.getTowerLongitude();
		this.imageRefNo = attendance.getImageRefNo();
		this.punchoutServerDate = punchout.getCreatedDate();
		this.punchoutClientDate = punchout.getPunchOutDate();

		if (attendance.getFiles().size() > 0) {
			this.imageButtonVisible = true;
		}
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public boolean getIsCompleted() {
		return completed;
	}

	public void setIsCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(LocalDateTime plannedDate) {
		this.plannedDate = plannedDate;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getTowerLocation() {
		return towerLocation;
	}

	public void setTowerLocation(String towerLocation) {
		this.towerLocation = towerLocation;
	}

	public BigDecimal getTowerLatitude() {
		return towerLatitude;
	}

	public void setTowerLatitude(BigDecimal towerLatitude) {
		this.towerLatitude = towerLatitude;
	}

	public BigDecimal getTowerLongitude() {
		return towerLongitude;
	}

	public void setTowerLongitude(BigDecimal towerLongitude) {
		this.towerLongitude = towerLongitude;
	}

	public String getImageRefNo() {
		return imageRefNo;
	}

	public void setImageRefNo(String imageRefNo) {
		this.imageRefNo = imageRefNo;
	}

	public Boolean getImageButtonVisible() {
		return imageButtonVisible;
	}

	public void setImageButtonVisible(Boolean imageButtonVisible) {
		this.imageButtonVisible = imageButtonVisible;
	}

	public LocalDateTime getPunchoutServerDate() {
		return punchoutServerDate;
	}

	public void setPunchoutServerDate(LocalDateTime punchoutServerDate) {
		this.punchoutServerDate = punchoutServerDate;
	}

	public LocalDateTime getPunchoutClientDate() {
		return punchoutClientDate;
	}

	public void setPunchoutClientDate(LocalDateTime punchoutClientDate) {
		this.punchoutClientDate = punchoutClientDate;
	}

	public String getPunchoutRemarks() {
		return punchoutRemarks;
	}

	public void setPunchoutRemarks(String punchoutRemarks) {
		this.punchoutRemarks = punchoutRemarks;
	}

	public LocalDate getAttendanceDay() {
		return attendanceDay;
	}

	public void setAttendanceDay(LocalDate attendanceDay) {
		this.attendanceDay = attendanceDay;
	}
	

	public String getPunchoutStatus() {
		return punchoutStatus;
	}

	public void setPunchoutStatus(String punchoutStatus) {
		this.punchoutStatus = punchoutStatus;
	}

	@Override
	public String toString() {
		return "LoginLogoutDTO [pid=" + pid + ", createdDate=" + createdDate + ", completed=" + completed + ", remarks="
				+ remarks + ", plannedDate=" + plannedDate + ", attendanceStatus=" + attendanceStatus + ", latitude="
				+ latitude + ", longitude=" + longitude + ", locationType=" + locationType + ", location=" + location
				+ ", employeeName=" + employeeName + ", towerLocation=" + towerLocation + ", towerLatitude="
				+ towerLatitude + ", towerLongitude=" + towerLongitude + ", imageRefNo=" + imageRefNo
				+ ", imageButtonVisible=" + imageButtonVisible + ", punchoutServerDate=" + punchoutServerDate
				+ ", punchoutClientDate=" + punchoutClientDate + ", punchoutRemarks=" + punchoutRemarks + "]";
	}

}
