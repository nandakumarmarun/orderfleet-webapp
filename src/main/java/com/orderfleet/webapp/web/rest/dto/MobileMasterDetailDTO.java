package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.MobileMasterDetail;
import com.orderfleet.webapp.domain.enums.MobileMasterItem;

public class MobileMasterDetailDTO {

	private MobileMasterItem mobileMasterItem;
	private String operationTime;
	private Long count;

	// For View
	private String userName;
	private String employeeName;
	private LocalDateTime createdDate;
	private String totalTime;
	private String userBuildVersion;
	private String masterItem;

	public MobileMasterDetailDTO() {
		super();

	}

	public MobileMasterDetailDTO(MobileMasterDetail mobileMasterDetail) {
		this.mobileMasterItem = mobileMasterDetail.getMobileMasterItem();
		this.operationTime = mobileMasterDetail.getOperationTime() != null ? mobileMasterDetail.getOperationTime() : "";
		this.count = mobileMasterDetail.getCount();
	}

	public MobileMasterItem getMobileMasterItem() {
		return mobileMasterItem;
	}

	public void setMobileMasterItem(MobileMasterItem mobileMasterItem) {
		this.mobileMasterItem = mobileMasterItem;
	}

	public String getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(String operationTime) {
		this.operationTime = operationTime;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getUserBuildVersion() {
		return userBuildVersion;
	}

	public void setUserBuildVersion(String userBuildVersion) {
		this.userBuildVersion = userBuildVersion;
	}

	public String getMasterItem() {
		return masterItem;
	}

	public void setMasterItem(String masterItem) {
		this.masterItem = masterItem;
	}

}
