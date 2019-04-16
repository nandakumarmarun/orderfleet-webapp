package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

public class ExecutiveTaskExecutionView {

	private String pid;

	private LocalDateTime createdDate;

	private LocalDateTime plannedDate;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String timeSpend;

	private String remarks;

	private String userName;

	private String activityName;

	private String accountTypeName;

	private String accountProfileName;

	private String accountProfileLocation;

	private BigDecimal accountProfileLatitude;

	private BigDecimal accountProfileLongitude;

	private LocationType locationType;

	private boolean isGpsOff;

	private boolean isMobileDataOff;

	private String location;

	private LocationType startLocationType;

	private boolean startIsGpsOff;

	private boolean startIsMobileDataOff;

	private String startLocation;

	private ActivityStatus activityStatus;

	private String rejectReasonRemark;

	private List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews;

	private BigDecimal latitude;

	private BigDecimal longitude;
	
	private BigDecimal towerLatitude;

	private BigDecimal towerLongitude;

	private String employeeName;
	
	private String towerLocation;

	public ExecutiveTaskExecutionView() {
		super();
	}

	public ExecutiveTaskExecutionView(String pid, LocalDateTime createdDate, LocalDateTime plannedDate, String remarks,
			String userName, String activityName, String accountTypeName, String accountProfileName,
			LocationType locationType, String location, boolean isGpsOff, boolean isMobileDataOff,
			LocalDateTime startTime, LocalDateTime endTime, boolean startIsGpsOff, boolean startIsMobileDataOff,
			LocationType startLocationType, String startLocation, String timeSpend, String accountProfileLocation,
			ActivityStatus activityStatus, String rejectReasonRemark) {
		super();
		this.pid = pid;
		this.createdDate = createdDate;
		this.plannedDate = plannedDate;
		this.remarks = remarks;
		this.userName = userName;
		this.activityName = activityName;
		this.accountTypeName = accountTypeName;
		this.accountProfileName = accountProfileName;
		this.accountProfileLocation = accountProfileLocation;
		this.locationType = locationType;
		this.location = location;
		this.isGpsOff = isGpsOff;
		this.isMobileDataOff = isMobileDataOff;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startIsGpsOff = startIsGpsOff;
		this.startIsMobileDataOff = startIsMobileDataOff;
		this.startLocationType = startLocationType;
		this.startLocation = startLocation;
		this.timeSpend = timeSpend;
		this.activityStatus = activityStatus;
		this.rejectReasonRemark = rejectReasonRemark;
	}

	public ExecutiveTaskExecutionView(ExecutiveTaskExecution executiveTaskExecution) {
		super();
		this.pid = executiveTaskExecution.getPid();
		this.createdDate = executiveTaskExecution.getCreatedDate();
		this.plannedDate = executiveTaskExecution.getDate();
		this.remarks = executiveTaskExecution.getRemarks();
		this.userName = executiveTaskExecution.getUser().getFirstName();
		this.activityName = executiveTaskExecution.getActivity().getName();
		this.accountTypeName = executiveTaskExecution.getAccountType().getName();
		this.accountProfileName = executiveTaskExecution.getAccountProfile().getName();
		this.accountProfileLocation = executiveTaskExecution.getAccountProfile().getLocation();
		this.accountProfileLatitude = executiveTaskExecution.getAccountProfile().getLatitude();
		this.accountProfileLongitude = executiveTaskExecution.getAccountProfile().getLongitude();
		this.locationType = executiveTaskExecution.getLocationType();
		this.location = executiveTaskExecution.getLocation();
		this.isGpsOff = executiveTaskExecution.getIsGpsOff();
		this.isMobileDataOff = executiveTaskExecution.getIsMobileDataOff();
		this.startTime = executiveTaskExecution.getStartTime();
		this.endTime = executiveTaskExecution.getEndTime();
		this.startIsGpsOff = executiveTaskExecution.getStartIsGpsOff();
		this.startIsMobileDataOff = executiveTaskExecution.getStartIsMobileDataOff();
		this.startLocationType = executiveTaskExecution.getStartLocationType();
		this.startLocation = executiveTaskExecution.getStartLocation();
		this.activityStatus = executiveTaskExecution.getActivityStatus();
		this.rejectReasonRemark = executiveTaskExecution.getRejectReasonRemark();
		this.latitude = executiveTaskExecution.getLatitude();
		this.longitude = executiveTaskExecution.getLongitude();
		this.towerLatitude = executiveTaskExecution.getTowerLatitude();
		this.towerLongitude = executiveTaskExecution.getTowerLongitude();
		this.towerLocation = executiveTaskExecution.getTowerLocation();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public BigDecimal getAccountProfileLatitude() {
		return accountProfileLatitude;
	}

	public void setAccountProfileLatitude(BigDecimal accountProfileLatitude) {
		this.accountProfileLatitude = accountProfileLatitude;
	}

	public BigDecimal getAccountProfileLongitude() {
		return accountProfileLongitude;
	}

	public void setAccountProfileLongitude(BigDecimal accountProfileLongitude) {
		this.accountProfileLongitude = accountProfileLongitude;
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

	public boolean getIsGpsOff() {
		return isGpsOff;
	}

	public void setIsGpsOff(boolean isGpsOff) {
		this.isGpsOff = isGpsOff;
	}

	public boolean getIsMobileDataOff() {
		return isMobileDataOff;
	}

	public void setIsMobileDataOff(boolean isMobileDataOff) {
		this.isMobileDataOff = isMobileDataOff;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocationType getStartLocationType() {
		return startLocationType;
	}

	public void setStartLocationType(LocationType startLocationType) {
		this.startLocationType = startLocationType;
	}

	public boolean getStartIsGpsOff() {
		return startIsGpsOff;
	}

	public void setStartIsGpsOff(boolean startIsGpsOff) {
		this.startIsGpsOff = startIsGpsOff;
	}

	public boolean getStartIsMobileDataOff() {
		return startIsMobileDataOff;
	}

	public void setStartIsMobileDataOff(boolean startIsMobileDataOff) {
		this.startIsMobileDataOff = startIsMobileDataOff;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public String getTimeSpend() {
		return timeSpend;
	}

	public void setTimeSpend(String timeSpend) {
		this.timeSpend = timeSpend;
	}

	public List<ExecutiveTaskExecutionDetailView> getExecutiveTaskExecutionDetailViews() {
		return executiveTaskExecutionDetailViews;
	}

	public void setExecutiveTaskExecutionDetailViews(
			List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews) {
		this.executiveTaskExecutionDetailViews = executiveTaskExecutionDetailViews;
	}

	public String getAccountProfileLocation() {
		return accountProfileLocation;
	}

	public void setAccountProfileLocation(String accountProfileLocation) {
		this.accountProfileLocation = accountProfileLocation;
	}

	public ActivityStatus getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(ActivityStatus activityStatus) {
		this.activityStatus = activityStatus;
	}

	public String getRejectReasonRemark() {
		return rejectReasonRemark;
	}

	public void setRejectReasonRemark(String rejectReasonRemark) {
		this.rejectReasonRemark = rejectReasonRemark;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public void setGpsOff(boolean isGpsOff) {
		this.isGpsOff = isGpsOff;
	}

	public void setMobileDataOff(boolean isMobileDataOff) {
		this.isMobileDataOff = isMobileDataOff;
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

	@Override
	public String toString() {
		return "ExecutiveTaskExecutionView [pid=" + pid + ", createdDate=" + createdDate + ", plannedDate="
				+ plannedDate + ", startTime=" + startTime + ", endTime=" + endTime + ", timeSpend=" + timeSpend
				+ ", remarks=" + remarks + ", userName=" + userName + ", activityName=" + activityName
				+ ", accountTypeName=" + accountTypeName + ", accountProfileName=" + accountProfileName
				+ ", accountProfileLocation=" + accountProfileLocation + ", locationType=" + locationType
				+ ", isGpsOff=" + isGpsOff + ", isMobileDataOff=" + isMobileDataOff + ", location=" + location
				+ ", startLocationType=" + startLocationType + ", startIsGpsOff=" + startIsGpsOff
				+ ", startIsMobileDataOff=" + startIsMobileDataOff + ", startLocation=" + startLocation
				+ ", activityStatus=" + activityStatus + ", rejectReasonRemark=" + rejectReasonRemark
				+ ", executiveTaskExecutionDetailViews=" + executiveTaskExecutionDetailViews + "]";
	}

}
