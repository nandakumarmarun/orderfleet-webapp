package com.orderfleet.webapp.web.vendor.model;

import java.util.List;

public class PickingHeader {
	
	private String transactionNo;
	
	private String referenceNo;
	
	private String orderDate;
	
	private String location;
	
	private String pickUser;
	
	private String accountProfilePid;
	
	private String accountProfileName;
	
	private String branchId;
	
	private String remarks;
	
	private List<PickingDetail> pickDetails;

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPickUser() {
		return pickUser;
	}

	public void setPickUser(String pickUser) {
		this.pickUser = pickUser;
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

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<PickingDetail> getPickDetails() {
		return pickDetails;
	}

	public void setPickDetails(List<PickingDetail> pickDetails) {
		this.pickDetails = pickDetails;
	}
	
}
