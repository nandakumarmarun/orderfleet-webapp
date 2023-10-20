package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.PunchOut;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * DTO for managing PunchOut.
 * 
 * @author Athul
 * @since March 27,2018
 */
public class PunchOutDTO {

	private String pid;
	private String clientTransactionKey;
	private String punchOutRemarks;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String mnc;
	private String mcc;
	private String cellId;
	private String lac;
	private LocationType locationType;
	private String location;
	private String login;
	private User userName;
	private String userPid;
	private String attendancePid;
	private LocalDateTime punchOutDate;
	private LocalDateTime createdDate;
	private String employeeName;
	private double oodoMeter;
	private String imageRefNo;

	private long companyId;

	public PunchOutDTO() {
		super();
	}

	public PunchOutDTO(PunchOut punchOut) {
		super();
		this.pid = punchOut.getPid();
		this.clientTransactionKey = punchOut.getClientTransactionKey();
		this.punchOutRemarks = punchOut.getRemarks();
		this.latitude = punchOut.getLatitude();
		this.longitude = punchOut.getLongitude();
		this.mnc = punchOut.getMnc();
		this.mcc = punchOut.getMcc();
		this.cellId = punchOut.getCellId();
		this.location = punchOut.getLocation();
		this.lac = punchOut.getLac();
		this.locationType = punchOut.getLocationType();
		this.login = punchOut.getUser().getLogin();
		this.userName = punchOut.getUser();
		this.userPid = punchOut.getUser().getPid();
		this.attendancePid = punchOut.getAttendance().getPid();
		this.punchOutDate = punchOut.getPunchOutDate();
		this.createdDate = punchOut.getCreatedDate();
		this.oodoMeter = punchOut.getOodoMeter();
		this.imageRefNo = punchOut.getImageRefNo();
		this.companyId = punchOut.getCompany().getId();

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

	public String getPunchOutRemarks() {
		return punchOutRemarks;
	}

	public void setPunchOutRemarks(String punchOutRemarks) {
		this.punchOutRemarks = punchOutRemarks;
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public User getUserName() {
		return userName;
	}

	public void setUserName(User userName) {
		this.userName = userName;
	}

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getAttendancePid() {
		return attendancePid;
	}

	public void setAttendancePid(String attendancePid) {
		this.attendancePid = attendancePid;
	}

	public LocalDateTime getPunchOutDate() {
		return punchOutDate;
	}

	public void setPunchOutDate(LocalDateTime punchOutDate) {
		this.punchOutDate = punchOutDate;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	
	

	public double getOodoMeter() {
		return oodoMeter;
	}

	public void setOodoMeter(double oodoMeter) {
		this.oodoMeter = oodoMeter;
	}

	public String getImageRefNo() {
		return imageRefNo;
	}

	public void setImageRefNo(String imageRefNo) {
		this.imageRefNo = imageRefNo;
	}


	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		return "PunchOutDTO{" +
				"pid='" + pid + '\'' +
				", clientTransactionKey='" + clientTransactionKey + '\'' +
				", punchOutRemarks='" + punchOutRemarks + '\'' +
				", latitude=" + latitude +
				", longitude=" + longitude +
				", mnc='" + mnc + '\'' +
				", mcc='" + mcc + '\'' +
				", cellId='" + cellId + '\'' +
				", lac='" + lac + '\'' +
				", locationType=" + locationType +
				", location='" + location + '\'' +
				", login='" + login + '\'' +
				", userName=" + userName +
				", userPid='" + userPid + '\'' +
				", attendancePid='" + attendancePid + '\'' +
				", punchOutDate=" + punchOutDate +
				", createdDate=" + createdDate +
				", employeeName='" + employeeName + '\'' +
				", oodoMeter=" + oodoMeter +
				", imageRefNo='" + imageRefNo + '\'' +
				", companyId=" + companyId +
				'}';
	}
}
