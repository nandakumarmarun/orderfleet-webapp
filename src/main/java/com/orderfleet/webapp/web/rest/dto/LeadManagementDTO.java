package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountProfile;

public class LeadManagementDTO {

	private String pid;
	private String name;
	private String alias;
	private String contactPerson;
	private String phone;
	private String city;
	private String address;
	private String locationName;
	private String locationPid;
	private String accountTypePid;
	private String accountTypeName;
	private String remarks;
	
	public LeadManagementDTO() {
		super();
	}

	public LeadManagementDTO(AccountProfile accountprofile) {
		super();
		this.pid = accountprofile.getPid();
		this.name = accountprofile.getName();
		this.alias = accountprofile.getAlias();
		this.contactPerson = accountprofile.getContactPerson();
		this.phone = accountprofile.getPhone1();
		this.address = accountprofile.getAddress();
		this.locationName = accountprofile.getLocation();
		this.accountTypePid = accountprofile.getAccountType().getPid();
		this.accountTypeName = accountprofile.getAccountType().getName();
		this.city = accountprofile.getCity();
		this.remarks = "remarks";
	}
	
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationPid() {
		return locationPid;
	}

	public void setLocationPid(String locationPid) {
		this.locationPid = locationPid;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
