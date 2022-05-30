package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * A ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Entity
@Table(name = "tbl_executive_task_execution")
public class ExecutiveTaskExecution implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_executive_task_execution_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_executive_task_execution_id") })
	@GeneratedValue(generator = "seq_executive_task_execution_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_executive_task_execution_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "client_transaction_key", updatable = false)
	private String clientTransactionKey;

	@ManyToOne
	@NotNull
	private User user;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@NotNull
	@Column(name = "date", nullable = false)
	private LocalDateTime date;

	@Column(name = "send_date")
	private LocalDateTime sendDate;

	// start time of order taking
	@Column(name = "start_time")
	private LocalDateTime startTime;

	// end time of order taking
	@Column(name = "end_time")
	private LocalDateTime endTime;

	@ManyToOne
	@NotNull
	private Activity activity;

	@ManyToOne
	@NotNull
	private AccountType accountType;

	@ManyToOne
	@NotNull
	private AccountProfile accountProfile;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "location")
	private String location;

	@Column(name = "tower_location")
	private String towerLocation;

	@Column(name = "latitude", precision = 10, scale = 8)
	private BigDecimal latitude;

	@Column(name = "longitude", precision = 11, scale = 8)
	private BigDecimal longitude;

	@Column(name = "tower_latitude", precision = 10, scale = 8)
	private BigDecimal towerLatitude;

	@Column(name = "tower_longitude", precision = 11, scale = 8)
	private BigDecimal towerLongitude;

	@Column(name = "mnc")
	private String mnc;

	@Column(name = "mcc")
	private String mcc;

	@Column(name = "cell_id")
	private String cellId;

	@Column(name = "lac")
	private String lac;

	@Column(name = "location_type")
	@Enumerated(EnumType.STRING)
	private LocationType locationType;

	@Column(name = "is_gps_off", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean isGpsOff;

	@Column(name = "is_mobile_data_off", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean isMobileDataOff;

	@Column(name = "start_location")
	private String startLocation;

	@Column(name = "start_latitude", precision = 10, scale = 8)
	private BigDecimal startLatitude;

	@Column(name = "start_longitude", precision = 11, scale = 8)
	private BigDecimal startLongitude;

	@Column(name = "start_mnc")
	private String startMnc;

	@Column(name = "start_mcc")
	private String startMcc;

	@Column(name = "start_cell_id")
	private String startCellId;

	@Column(name = "start_lac")
	private String startLac;

	@Column(name = "start_location_type")
	@Enumerated(EnumType.STRING)
	private LocationType startLocationType;

	@Column(name = "start_is_gps_off", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean startIsGpsOff;

	@Column(name = "start_is_mobile_data_off", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean startIsMobileDataOff;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "activity_status", nullable = false, columnDefinition = "character varying DEFAULT 'RECEIVED'")
	@Enumerated(EnumType.STRING)
	private ActivityStatus activityStatus;

	@NotNull
	@Column(name = "status", nullable = false)
	private Boolean status = false;

	@ManyToOne
	private ExecutiveTaskPlan executiveTaskPlan;

	@Column(name = "reject_reason_remark")
	private String rejectReasonRemark;

	@Column(name = "location_variance")
	private String locationVariance;

	@Column(name = "punch_in_date")
	private LocalDateTime punchInDate;

	@Column(name = "mock_location_status", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean mockLocationStatus;

	@Column(name = "with_customer", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean withCustomer;
	
	@Column(name = "vehicle_Number")
	private String vehicleNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}
	
	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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

	public ActivityStatus getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(ActivityStatus activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public ExecutiveTaskPlan getExecutiveTaskPlan() {
		return executiveTaskPlan;
	}

	public void setExecutiveTaskPlan(ExecutiveTaskPlan executiveTaskPlan) {
		this.executiveTaskPlan = executiveTaskPlan;
	}

	public String getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(String startLocation) {
		this.startLocation = startLocation;
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

	public String getRejectReasonRemark() {
		return rejectReasonRemark;
	}

	public void setRejectReasonRemark(String rejectReasonRemark) {
		this.rejectReasonRemark = rejectReasonRemark;
	}

	public String getLocationVariance() {
		return locationVariance;
	}

	public void setLocationVariance(String locationVariance) {
		this.locationVariance = locationVariance;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExecutiveTaskExecution executiveTaskExecution = (ExecutiveTaskExecution) o;
		if (executiveTaskExecution.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, executiveTaskExecution.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "ExecutiveTaskExecution [id=" + id + ", pid=" + pid + ", towerLocation=" + towerLocation + ", latitude="
				+ latitude + ", longitude=" + longitude + ", towerLatitude=" + towerLatitude + ", towerLongitude="
				+ towerLongitude + ", mnc=" + mnc + ", mcc=" + mcc + ", cellId=" + cellId + ", locationType="
				+ locationType + ", executiveTaskPlan=" + executiveTaskPlan + "]";
	}

//	@Override
//	public String toString() {
//		return "ExecutiveTaskExecution{" + "id=" + id + ", pid='" + pid + "'" + '}';
//	}

}