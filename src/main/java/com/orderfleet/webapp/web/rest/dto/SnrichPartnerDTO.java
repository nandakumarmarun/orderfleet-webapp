package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.SnrichPartner;

public class SnrichPartnerDTO {

	private String pid;
	private String name;
	private String address1;
	private String address2;
	private String country;
	private String countryCode;
	private String state;
	private String stateCode;
	private String district;
	private String districtCode;
	private String email;
	private String phone;
	private String location;
	private LocalDateTime createdDate;
	private Boolean activated;
	
	
	public SnrichPartnerDTO() {
		super();
	}
	
	public SnrichPartnerDTO(SnrichPartner snrichPartner) {
		super();
		this.pid = snrichPartner.getPid();
		this.name = snrichPartner.getName();
		this.address1 = snrichPartner.getAddress1();
		this.address2 = snrichPartner.getAddress2();
		this.country = snrichPartner.getCountry().getName();
		this.state = snrichPartner.getState().getName();
		this.district = snrichPartner.getDistrict().getName();
		this.countryCode = snrichPartner.getCountry().getCode();
		this.stateCode = snrichPartner.getState().getCode();
		this.districtCode = snrichPartner.getDistrict().getCode();
		this.email = snrichPartner.getEmail();
		this.phone = snrichPartner.getPhone();
		this.location = snrichPartner.getLocation();
		this.createdDate = snrichPartner.getCreatedDate();
		this.activated = snrichPartner.getActivated();
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
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Boolean getActivated() {
		return activated;
	}
	public void setActivated(Boolean activated) {
		this.activated = activated;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
