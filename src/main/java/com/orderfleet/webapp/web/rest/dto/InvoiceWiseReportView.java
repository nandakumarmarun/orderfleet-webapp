package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

public class InvoiceWiseReportView {

	private String pid;

	private LocalDateTime createdDate;

	private LocalDateTime plannedDate;

	private LocalDateTime sendDate;

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

	private List<InvoiceWiseReportDetailView> invoiceWiseReportDetailViews;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal towerLatitude;

	private BigDecimal towerLongitude;

	private String employeeName;

	private String towerLocation;

	private double totalRecieptAmount;

	private double totalSalesOrderAmount;

	private LocalDateTime punchInDate;

	private boolean mockLocationStatus;

	private boolean withCustomer;
	
	private String description;
	
	private String vehicleRegistrationNumber;
	 
	private String vehiclename;
	
	private String invoiceNo;

	public InvoiceWiseReportView() {
		super();
	}

	public InvoiceWiseReportView(ExecutiveTaskExecution executiveTaskExecution) {
		super();
		this.pid = executiveTaskExecution.getPid();
		this.createdDate = executiveTaskExecution.getCreatedDate();
		this.plannedDate = executiveTaskExecution.getDate();
		this.sendDate = executiveTaskExecution.getSendDate() != null ? executiveTaskExecution.getSendDate()
				: executiveTaskExecution.getDate();
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
		this.punchInDate = executiveTaskExecution.getPunchInDate();
		this.mockLocationStatus = executiveTaskExecution.getMockLocationStatus();
		this.withCustomer = executiveTaskExecution.getWithCustomer();
		this.setDescription(executiveTaskExecution.getAccountProfile().getDescription());
	}

	public InvoiceWiseReportView(String pid, LocalDateTime createdDate, LocalDateTime plannedDate,
			LocalDateTime sendDate, LocalDateTime startTime, LocalDateTime endTime, String timeSpend, String remarks,
			String userName, String activityName, String accountTypeName, String accountProfileName,
			String accountProfileLocation, BigDecimal accountProfileLatitude, BigDecimal accountProfileLongitude,
			LocationType locationType, boolean isGpsOff, boolean isMobileDataOff, String location,
			LocationType startLocationType, boolean startIsGpsOff, boolean startIsMobileDataOff, String startLocation,
			ActivityStatus activityStatus, String rejectReasonRemark,
			List<InvoiceWiseReportDetailView> invoiceWiseReportDetailViews, BigDecimal latitude, BigDecimal longitude,
			BigDecimal towerLatitude, BigDecimal towerLongitude, String employeeName, String towerLocation,
			double totalRecieptAmount, double totalSalesOrderAmount, LocalDateTime punchInDate,
			boolean mockLocationStatus, boolean withCustomer, String description, String vehicleRegistrationNumber,
			String vehiclename) {
		super();
		this.pid = pid;
		this.createdDate = createdDate;
		this.plannedDate = plannedDate;
		this.sendDate = sendDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeSpend = timeSpend;
		this.remarks = remarks;
		this.userName = userName;
		this.activityName = activityName;
		this.accountTypeName = accountTypeName;
		this.accountProfileName = accountProfileName;
		this.accountProfileLocation = accountProfileLocation;
		this.accountProfileLatitude = accountProfileLatitude;
		this.accountProfileLongitude = accountProfileLongitude;
		this.locationType = locationType;
		this.isGpsOff = isGpsOff;
		this.isMobileDataOff = isMobileDataOff;
		this.location = location;
		this.startLocationType = startLocationType;
		this.startIsGpsOff = startIsGpsOff;
		this.startIsMobileDataOff = startIsMobileDataOff;
		this.startLocation = startLocation;
		this.activityStatus = activityStatus;
		this.rejectReasonRemark = rejectReasonRemark;
		this.invoiceWiseReportDetailViews = invoiceWiseReportDetailViews;
		this.latitude = latitude;
		this.longitude = longitude;
		this.towerLatitude = towerLatitude;
		this.towerLongitude = towerLongitude;
		this.employeeName = employeeName;
		this.towerLocation = towerLocation;
		this.totalRecieptAmount = totalRecieptAmount;
		this.totalSalesOrderAmount = totalSalesOrderAmount;
		this.punchInDate = punchInDate;
		this.mockLocationStatus = mockLocationStatus;
		this.withCustomer = withCustomer;
		this.description = description;
		this.vehicleRegistrationNumber = vehicleRegistrationNumber;
		this.vehiclename = vehiclename;
	}

	public String getVehicleRegistrationNumber() {
		return vehicleRegistrationNumber;
	}

	public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
		this.vehicleRegistrationNumber = vehicleRegistrationNumber;
	}

	public String getVehiclename() {
		return vehiclename;
	}

	public void setVehiclename(String vehiclename) {
		this.vehiclename = vehiclename;
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

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
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

	public List<InvoiceWiseReportDetailView> getInvoiceWiseReportDetailViews() {
		return invoiceWiseReportDetailViews;
	}

	public void setInvoiceWiseReportDetailViews(List<InvoiceWiseReportDetailView> invoiceWiseReportDetailViews) {
		this.invoiceWiseReportDetailViews = invoiceWiseReportDetailViews;
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

	public double getTotalRecieptAmount() {
		return totalRecieptAmount;
	}

	public void setTotalRecieptAmount(double totalRecieptAmount) {
		this.totalRecieptAmount = totalRecieptAmount;
	}

	public double getTotalSalesOrderAmount() {
		return totalSalesOrderAmount;
	}

	public void setTotalSalesOrderAmount(double totalSalesOrderAmount) {
		this.totalSalesOrderAmount = totalSalesOrderAmount;
	}

	public LocalDateTime getPunchInDate() {
		return punchInDate;
	}

	public void setPunchInDate(LocalDateTime punchInDate) {
		this.punchInDate = punchInDate;
	}

	public boolean getMockLocationStatus() {
		return mockLocationStatus;
	}

	public void setMockLocationStatus(boolean mockLocationStatus) {
		this.mockLocationStatus = mockLocationStatus;
	}

	public boolean getWithCustomer() {
		return withCustomer;
	}

	public void setWithCustomer(boolean withCustomer) {
		this.withCustomer = withCustomer;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
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
				+ ", invoiceWiseReportDetailViews=" + invoiceWiseReportDetailViews + "]";
	}

	

}
