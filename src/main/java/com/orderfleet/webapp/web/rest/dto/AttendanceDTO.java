package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * A DTO for the Attendance entity.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
public class AttendanceDTO {

	private String pid;
	private String clientTransactionKey;
	private String login;
	private String userPid;
	private String userName;
	private LocalDateTime createdDate;
	private boolean completed;
	private String remarks;
	private LocalDateTime plannedDate;
	private AttendanceStatus attendanceStatus;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String mnc;
	private String mcc;
	private String cellId;
	private String lac;
	private LocationType locationType;
	private String location;
	private Long attendanceSubGroupId;
	private String attendanceSubGroupName;
	private String attendanceSubGroupCode;
	private String taskListAlias;
	private String employeeName;
	private String towerLocation;
	private BigDecimal towerLatitude;
	private BigDecimal towerLongitude;
	private double oodoMeter;
	private String distanceFarePid;
	private String imageRefNo;
	private String vehicleType;
	private Boolean imageButtonVisible = Boolean.FALSE;;

	public AttendanceDTO() {
		super();
	}

	public AttendanceDTO(String pid, String login, String userPid, String userName, LocalDateTime createdDate,
			boolean completed, String remarks, LocalDateTime plannedDate, AttendanceStatus attendanceStatus,
			BigDecimal latitude, BigDecimal longitude, String mnc, String mcc, String cellId, String lac,
			LocationType locationType, String location, String attendanceSubGroupName, String towerLocation,
			BigDecimal towerLatitude, BigDecimal towerLongitude) {
		super();
		this.pid = pid;
		this.login = login;
		this.userPid = userPid;
		this.userName = userName;
		this.createdDate = createdDate;
		this.completed = completed;
		this.remarks = remarks;
		this.plannedDate = plannedDate;
		this.attendanceStatus = attendanceStatus;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mnc = mnc;
		this.mcc = mcc;
		this.cellId = cellId;
		this.lac = lac;
		this.locationType = locationType;
		this.location = location;
		this.attendanceSubGroupName = attendanceSubGroupName;
		this.towerLocation = towerLocation;
		this.towerLatitude = towerLatitude;
		this.towerLongitude = towerLongitude;
	}

	public AttendanceDTO(Attendance attendance) {
		super();
		this.pid = attendance.getPid();
		this.login = attendance.getUser().getLogin();
		this.userPid = attendance.getUser().getPid();
		this.userName = attendance.getUser().getFirstName();
		this.createdDate = attendance.getCreatedDate();
		this.completed = attendance.getIsCompleted();
		this.remarks = attendance.getRemarks();
		this.plannedDate = attendance.getPlannedDate();
		this.attendanceStatus = attendance.getAttendanceStatus();
		this.latitude = attendance.getLatitude();
		this.longitude = attendance.getLongitude();
		this.mnc = attendance.getMnc();
		this.mcc = attendance.getMcc();
		this.cellId = attendance.getCellId();
		this.lac = attendance.getLac();
		this.locationType = attendance.getLocationType();
		this.location = attendance.getLocation();
		this.towerLocation = attendance.getTowerLocation();
		this.towerLatitude = attendance.getTowerLatitude();
		this.towerLongitude = attendance.getTowerLongitude();
		this.imageRefNo = attendance.getImageRefNo();
		this.oodoMeter = attendance.getOodoMeter();
		if(attendance.getDistanceFare() != null) {
			this.distanceFarePid = attendance.getDistanceFare().getPid();
		}
		if(attendance.getDistanceFare() != null) {
			this.vehicleType = attendance.getDistanceFare().getVehicleType();
		}
		
		if (attendance.getAttendanceStatusSubgroup() != null) {
			this.attendanceSubGroupId = attendance.getAttendanceStatusSubgroup().getId();
			this.attendanceSubGroupName = attendance.getAttendanceStatusSubgroup().getName();
			this.attendanceSubGroupCode = attendance.getAttendanceStatusSubgroup().getCode();
		}

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

	public String getClientTransactionKey() {
		return clientTransactionKey;
	}

	public void setClientTransactionKey(String clientTransactionKey) {
		this.clientTransactionKey = clientTransactionKey;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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

	public AttendanceStatus getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
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

	public String getMnc() {
		return mnc;
	}

	public void setMnc(String mnc) {
		this.mnc = mnc;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getLac() {
		return lac;
	}

	public void setLac(String lac) {
		this.lac = lac;
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

	public Long getAttendanceSubGroupId() {
		return attendanceSubGroupId;
	}

	public void setAttendanceSubGroupId(Long attendanceSubGroupId) {
		this.attendanceSubGroupId = attendanceSubGroupId;
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

	public String getTaskListAlias() {
		return taskListAlias;
	}

	public void setTaskListAlias(String taskListAlias) {
		this.taskListAlias = taskListAlias;
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
	
	

	public double getOodoMeter() {
		return oodoMeter;
	}

	public void setOodoMeter(double oodoMeter) {
		this.oodoMeter = oodoMeter;
	}

	public String getDistanceFarePid() {
		return distanceFarePid;
	}

	public void setDistanceFarePid(String distanceFarePid) {
		this.distanceFarePid = distanceFarePid;
	}

	
	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	@Override
	public String toString() {
		return "AttendanceDTO [pid=" + pid + ", login=" + login + ", userName=" + userName + ", plannedDate="
				+ plannedDate + ", createdDate=" + createdDate + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", location=" + location + ", employeeName=" + employeeName + "]";
	}

}
