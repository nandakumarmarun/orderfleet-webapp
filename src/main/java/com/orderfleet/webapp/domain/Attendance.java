package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * A Attendance.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
@Entity
@Table(name = "tbl_attendance")
public class Attendance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_attendance_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_attendance_id") })
	@GeneratedValue(generator = "seq_attendance_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_attendance_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@Column(name = "client_transaction_key", updatable = false)
	private String clientTransactionKey;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@NotNull
	@Column(name = "completed", nullable = false)
	private boolean completed;

	@Column(name = "remarks", length = 500)
	private String remarks;

	@NotNull
	@Column(name = "planned_date", nullable = false)
	private LocalDateTime plannedDate;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	private User user;

	@ManyToOne
	private AttendanceStatusSubgroup attendanceStatusSubgroup;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "attendance_status", nullable = false)
	private AttendanceStatus attendanceStatus;

	@Column(name = "location")
	private String location;

	@Column(name = "latitude", precision = 10, scale = 8)
	private BigDecimal latitude;

	@Column(name = "longitude", precision = 11, scale = 8)
	private BigDecimal longitude;

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

	@Column(name = "tower_location")
	private String towerLocation;

	@Column(name = "tower_latitude", precision = 10, scale = 8)
	private BigDecimal towerLatitude;

	@Column(name = "tower_longitude", precision = 11, scale = 8)
	private BigDecimal towerLongitude;

	@Column(name = "imageRefNo")
	private String imageRefNo;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "tbl_attendance_file", joinColumns = {
			@JoinColumn(name = "attendance_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "file_id", referencedColumnName = "id") })
	private Set<File> files = new HashSet<>();

	public Attendance() {
		super();
	}

	public Attendance(Long id, String pid, LocalDateTime createdDate, boolean completed, String remarks,
			LocalDateTime plannedDate, Company company, User user, AttendanceStatus attendanceStatus, String location,
			BigDecimal latitude, BigDecimal longitude, String mnc, String mcc, String cellId, String lac,
			LocationType locationType) {
		super();
		this.id = id;
		this.pid = pid;
		this.createdDate = createdDate;
		this.completed = completed;
		this.remarks = remarks;
		this.plannedDate = plannedDate;
		this.company = company;
		this.user = user;
		this.attendanceStatus = attendanceStatus;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mnc = mnc;
		this.mcc = mcc;
		this.cellId = cellId;
		this.lac = lac;
		this.locationType = locationType;
	}

	public String getClientTransactionKey() {
		return clientTransactionKey;
	}

	public void setClientTransactionKey(String clientTransactionKey) {
		this.clientTransactionKey = clientTransactionKey;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public AttendanceStatusSubgroup getAttendanceStatusSubgroup() {
		return attendanceStatusSubgroup;
	}

	public void setAttendanceStatusSubgroup(AttendanceStatusSubgroup attendanceStatusSubgroup) {
		this.attendanceStatusSubgroup = attendanceStatusSubgroup;
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

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "Attendance [id=" + id + ", pid=" + pid + ", createdDate=" + createdDate + ", completed=" + completed
				+ ", remarks=" + remarks + ", plannedDate=" + plannedDate + "]";
	}

}
