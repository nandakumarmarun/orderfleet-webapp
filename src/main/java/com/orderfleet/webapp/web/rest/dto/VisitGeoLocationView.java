package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.LocationType;

public class VisitGeoLocationView {
	private String pid;

	private LocalDateTime createdDate;
	
	private LocalDateTime plannedDate;
	
	private String userName;
	
	private String employeeName;

	private String activityName;

	private String accountTypeName;
	
	private String accountProfilePid;
	
	private String accountProfileName;

	private LocationType locationType;
	
	private BigDecimal longitude;

	private BigDecimal latitude;

	private String location;
	
	private LocationType startLocationType;

	private BigDecimal startLongitude;

	private BigDecimal startLatitude;
	
	private String startLocation;
	private String description;
	
	private List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews;

	public VisitGeoLocationView(String pid, LocalDateTime createdDate,LocalDateTime plannedDate,  String userName,
			String activityName, String accountTypeName,String accountProfilePid, String accountProfileName, LocationType locationType,
			String location, BigDecimal longitude,BigDecimal latitude,BigDecimal startLongitude,BigDecimal startLatitude,LocationType startLocationType, String startLocation,String description) {
		super();
		this.pid = pid;
		this.createdDate = createdDate;
		this.plannedDate=plannedDate;
		this.userName = userName;
		this.activityName = activityName;
		this.accountTypeName = accountTypeName;
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.locationType = locationType;
		this.location = location;
		this.longitude = longitude;
		this.latitude = latitude;
		this.startLongitude=startLongitude;
		this.startLatitude=startLatitude;
		this.startLocationType=startLocationType;
		this.startLocation=startLocation;
		this.description=description;
	}

	public VisitGeoLocationView(ExecutiveTaskExecution executiveTaskExecution) {
		super();
		this.pid = executiveTaskExecution.getPid();
		this.createdDate = executiveTaskExecution.getCreatedDate();
		this.plannedDate=executiveTaskExecution.getDate();
		this.userName = executiveTaskExecution.getUser().getFirstName();
		this.activityName = executiveTaskExecution.getActivity().getName();
		this.accountTypeName = executiveTaskExecution.getAccountType().getName();
		this.accountProfilePid = executiveTaskExecution.getAccountProfile().getPid();
		this.accountProfileName = executiveTaskExecution.getAccountProfile().getName();
		this.locationType = executiveTaskExecution.getLocationType();
		this.location = executiveTaskExecution.getLocation();
		this.longitude = executiveTaskExecution.getLongitude();
		this.latitude = executiveTaskExecution.getLatitude();
		this.startLongitude=executiveTaskExecution.getStartLongitude();
		this.startLatitude=executiveTaskExecution.getStartLatitude();
		this.startLocationType=executiveTaskExecution.getStartLocationType();
		this.startLocation=executiveTaskExecution.getStartLocation();
		this.description=executiveTaskExecution.getAccountProfile().getDescription();
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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

	
	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
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

	public LocationType getStartLocationType() {
		return startLocationType;
	}

	public void setStartLocationType(LocationType startLocationType) {
		this.startLocationType = startLocationType;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(BigDecimal startLongitude) {
		this.startLongitude = startLongitude;
	}

	public BigDecimal getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(BigDecimal startLatitude) {
		this.startLatitude = startLatitude;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<ExecutiveTaskExecutionDetailView> getExecutiveTaskExecutionDetailViews() {
		return executiveTaskExecutionDetailViews;
	}

	public void setExecutiveTaskExecutionDetailViews(
			List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews) {
		this.executiveTaskExecutionDetailViews = executiveTaskExecutionDetailViews;
	}

	@Override
	public String toString() {
		return "AttachGeoLocationView [pid=" + pid + ", createdDate=" + createdDate + ", plannedDate=" + plannedDate
				+ ", userName=" + userName + ", activityName=" + activityName + ", accountTypeName=" + accountTypeName
				+ ", accountProfilePid=" + accountProfilePid + ", accountProfileName=" + accountProfileName
				+ ", locationType=" + locationType + ", longitude=" + longitude + ", latitude=" + latitude
				+ ", location=" + location + ", startLocationType=" + startLocationType + ", startLongitude="
				+ startLongitude + ", startLatitude=" + startLatitude + ", startLocation=" + startLocation
				+ ", executiveTaskExecutionDetailViews=" + executiveTaskExecutionDetailViews + "]";
	}

	
	

}