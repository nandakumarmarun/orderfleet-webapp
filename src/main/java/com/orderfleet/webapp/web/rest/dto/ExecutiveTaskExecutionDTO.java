package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * A DTO for the ExecutiveTaskExecution entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public class ExecutiveTaskExecutionDTO {

	private String pid;

	private String clientTransactionKey;

	private LocalDateTime createdDate;

	private LocalDateTime date;

	private LocalDateTime sendDate;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String timeSpend;

	private String remarks;

	private String userPid;

	private String userName;

	private String activityPid;

	private String activityName;

	private String accountTypePid;

	private String accountTypeName;

	private String accountProfilePid;

	private String accountProfileName;

	private String accountLocation;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal towerLatitude;

	private BigDecimal towerLongitude;

	private String mnc;

	private String mcc;

	private String cellId;

	private String lac;

	private LocationType locationType;

	private boolean isGpsOff;

	private boolean isMobileDataOff;

	private String startLocation;

	private BigDecimal startLatitude;

	private BigDecimal startLongitude;

	private String startMnc;

	private String startMcc;

	private String startCellId;

	private String startLac;

	private LocationType startLocationType;

	private boolean startIsGpsOff;

	private boolean startIsMobileDataOff;

	private String location;

	private String towerLocation;

	private ActivityStatus activityStatus;

	private String executiveTaskPlanPid;

	private String rejectReasonRemark;

	private String employeeName;

	private boolean interimSave;

	private LocalDateTime punchInDate;

	private boolean mockLocationStatus;

	private boolean withCustomer;
	
	private String description;

	public ExecutiveTaskExecutionDTO() {
		super();
	}

	public ExecutiveTaskExecutionDTO(ExecutiveTaskExecution executiveTaskExecution) {
		this(executiveTaskExecution.getPid(), executiveTaskExecution.getClientTransactionKey(),
				executiveTaskExecution.getLatitude(), executiveTaskExecution.getLongitude(),
				executiveTaskExecution.getTowerLatitude(), executiveTaskExecution.getTowerLongitude(),
				executiveTaskExecution.getCreatedDate(), executiveTaskExecution.getDate(),
				executiveTaskExecution.getSendDate(), executiveTaskExecution.getRemarks(),
				executiveTaskExecution.getUser().getPid(), executiveTaskExecution.getUser().getFirstName(),
				executiveTaskExecution.getActivity().getPid(), executiveTaskExecution.getActivity().getName(),
				executiveTaskExecution.getAccountType().getPid(), executiveTaskExecution.getAccountType().getName(),
				executiveTaskExecution.getAccountProfile().getPid(),
				executiveTaskExecution.getAccountProfile().getName(),
				executiveTaskExecution.getAccountProfile().getLocation(), executiveTaskExecution.getLocation(),
				executiveTaskExecution.getTowerLocation(), executiveTaskExecution.getLocationType(),
				executiveTaskExecution.getIsGpsOff(), executiveTaskExecution.getIsMobileDataOff(),
				executiveTaskExecution.getActivityStatus(),
				executiveTaskExecution.getExecutiveTaskPlan() != null
						? executiveTaskExecution.getExecutiveTaskPlan().getPid()
						: null,
				executiveTaskExecution.getStartIsGpsOff(), executiveTaskExecution.getIsMobileDataOff(),
				executiveTaskExecution.getStartLocationType(), executiveTaskExecution.getStartLocation(),
				executiveTaskExecution.getStartLatitude(), executiveTaskExecution.getStartLongitude(),
				executiveTaskExecution.getStartTime(), executiveTaskExecution.getEndTime(),
				executiveTaskExecution.getRejectReasonRemark(), executiveTaskExecution.getPunchInDate(),
				executiveTaskExecution.getMockLocationStatus(), executiveTaskExecution.getWithCustomer(),
                executiveTaskExecution.getAccountProfile().getDescription());
	}

	public ExecutiveTaskExecutionDTO(String pid, String clientTransactionKey, BigDecimal latitude, BigDecimal longitude,
			BigDecimal towerLatitude, BigDecimal towerLongitude, LocalDateTime createdDate, LocalDateTime date,
			LocalDateTime sendDate, String remarks, String userPid, String userName, String activityPid,
			String activityName, String accountTypePid, String accountTypeName, String accountProfilePid,
			String accountProfileName, String accountLocation, String location, String towerLocation,
			LocationType locationType, boolean isGpsOff, boolean isMobileDataOff, ActivityStatus activityStatus,
			String executiveTaskPlanPid, boolean startIsGpsOff, boolean startIsMobileDataOff,
			LocationType startLocationType, String startLocation, BigDecimal startLatitude, BigDecimal startLongitude,
			LocalDateTime startTime, LocalDateTime endTime, String rejectReasonRemark, LocalDateTime punchInDate,
			boolean mockLocationStatus, boolean withCustomer,String description) {
		super();
		this.pid = pid;
		this.clientTransactionKey = clientTransactionKey;
		this.latitude = latitude;
		this.longitude = longitude;
		this.towerLatitude = towerLatitude;
		this.towerLongitude = towerLongitude;
		this.createdDate = createdDate;
		this.date = date;
		this.sendDate = sendDate;
		this.remarks = remarks;
		this.userPid = userPid;
		this.userName = userName;
		this.activityPid = activityPid;
		this.activityName = activityName;
		this.accountTypePid = accountTypePid;
		this.accountTypeName = accountTypeName;
		this.accountProfilePid = accountProfilePid;
		this.accountProfileName = accountProfileName;
		this.accountLocation = accountLocation;
		this.location = location;
		this.towerLocation = towerLocation;
		this.locationType = locationType;
		this.isGpsOff = isGpsOff;
		this.isMobileDataOff = isMobileDataOff;
		this.activityStatus = activityStatus;
		this.executiveTaskPlanPid = executiveTaskPlanPid;
		this.startIsGpsOff = startIsGpsOff;
		this.startIsMobileDataOff = startIsMobileDataOff;
		this.startLocationType = startLocationType;
		this.startLocation = startLocation;
		this.startLatitude = startLatitude;
		this.startLongitude = startLongitude;
		this.startTime = startTime;
		this.endTime = endTime;
		this.rejectReasonRemark = rejectReasonRemark;
		this.punchInDate = punchInDate;
		this.mockLocationStatus = mockLocationStatus;
		this.withCustomer = withCustomer;
		this.setDescription(description);
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

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getAccountTypePid() {
		return accountTypePid;
	}

	public void setAccountTypePid(String accountTypePid) {
		this.accountTypePid = accountTypePid;
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public String getAccountLocation() {
		return accountLocation;
	}

	public void setAccountLocation(String accountLocation) {
		this.accountLocation = accountLocation;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getTimeSpend() {
		return timeSpend;
	}

	public void setTimeSpend(String timeSpend) {
		this.timeSpend = timeSpend;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public ActivityStatus getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(ActivityStatus activityStatus) {
		this.activityStatus = activityStatus;
	}

	public String getExecutiveTaskPlanPid() {
		return executiveTaskPlanPid;
	}

	public void setExecutiveTaskPlanPid(String executiveTaskPlanPid) {
		this.executiveTaskPlanPid = executiveTaskPlanPid;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
	}

	public String getTowerLocation() {
		return towerLocation;
	}

	public void setTowerLocation(String towerLocation) {
		this.towerLocation = towerLocation;
	}

	public BigDecimal getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(BigDecimal startLatitude) {
		this.startLatitude = startLatitude;
	}

	public BigDecimal getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(BigDecimal startLongitude) {
		this.startLongitude = startLongitude;
	}

	public String getStartMnc() {
		return startMnc;
	}

	public void setStartMnc(String startMnc) {
		this.startMnc = startMnc;
	}

	public String getStartMcc() {
		return startMcc;
	}

	public void setStartMcc(String startMcc) {
		this.startMcc = startMcc;
	}

	public String getStartCellId() {
		return startCellId;
	}

	public void setStartCellId(String startCellId) {
		this.startCellId = startCellId;
	}

	public String getStartLac() {
		return startLac;
	}

	public void setStartLac(String startLac) {
		this.startLac = startLac;
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

	public void setGpsOff(boolean isGpsOff) {
		this.isGpsOff = isGpsOff;
	}

	public void setMobileDataOff(boolean isMobileDataOff) {
		this.isMobileDataOff = isMobileDataOff;
	}

	public String getRejectReasonRemark() {
		return rejectReasonRemark;
	}

	public void setRejectReasonRemark(String rejectReasonRemark) {
		this.rejectReasonRemark = rejectReasonRemark;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public boolean getInterimSave() {
		return interimSave;
	}

	public void setInterimSave(boolean interimSave) {
		this.interimSave = interimSave;
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
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = (ExecutiveTaskExecutionDTO) o;

		if (!Objects.equals(pid, executiveTaskExecutionDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "ExecutiveTaskExecutionDTO [pid=" + pid + ", clientTransactionKey=" + clientTransactionKey
				+ ", createdDate=" + createdDate + ", date=" + date + ", sendDate=" + sendDate + ", startTime="
				+ startTime + ", endTime=" + endTime + ", timeSpend=" + timeSpend + ", remarks=" + remarks
				+ ", userPid=" + userPid + ", userName=" + userName + ", activityPid=" + activityPid + ", activityName="
				+ activityName + ", accountTypePid=" + accountTypePid + ", accountTypeName=" + accountTypeName
				+ ", accountProfilePid=" + accountProfilePid + ", accountProfileName=" + accountProfileName
				+ ", accountLocation=" + accountLocation + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", towerLatitude=" + towerLatitude + ", towerLongitude=" + towerLongitude + ", mnc=" + mnc + ", mcc="
				+ mcc + ", cellId=" + cellId + ", lac=" + lac + ", locationType=" + locationType + ", isGpsOff="
				+ isGpsOff + ", isMobileDataOff=" + isMobileDataOff + ", startLocation=" + startLocation
				+ ", startLatitude=" + startLatitude + ", startLongitude=" + startLongitude + ", startMnc=" + startMnc
				+ ", startMcc=" + startMcc + ", startCellId=" + startCellId + ", startLac=" + startLac
				+ ", startLocationType=" + startLocationType + ", startIsGpsOff=" + startIsGpsOff
				+ ", startIsMobileDataOff=" + startIsMobileDataOff + ", location=" + location + ", towerLocation="
				+ towerLocation + ", activityStatus=" + activityStatus + ", executiveTaskPlanPid="
				+ executiveTaskPlanPid + ", rejectReasonRemark=" + rejectReasonRemark + ", employeeName=" + employeeName
				+ ", interimSave=" + interimSave + ", punchInDate=" + punchInDate + ", mockLocationStatus="
				+ mockLocationStatus + ", withCustomer=" + withCustomer + "]";
	}



}