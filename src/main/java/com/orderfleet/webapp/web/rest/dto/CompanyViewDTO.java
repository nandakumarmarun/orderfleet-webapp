package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDate;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.enums.CompanyType;
import com.orderfleet.webapp.domain.enums.Industry;

public class CompanyViewDTO {

	private String pid;
	private String legalName;
	private String alias;
	private CompanyType companyType;
	private Industry industry;
	private String address1;
	private String address2;
	private String countryName;
	private String countryCode;
	private String stateName;
	private String stateCode;
	private String districtName;
	private String districtCode;
	private String gstNo;
	private String email;
	private String pincode;
	private String location;
	private byte[] logo;
	private String logoContentType;
	private String website;
	private boolean activated;
	private LocalDate periodStartDate;
	private LocalDate periodEndDate;
	private String apiUrl;
	private boolean onPremise;
	private String smsApiKey;

	public CompanyViewDTO() {
		super();
	}

	public CompanyViewDTO(Company company) {
		super();
		this.pid = company.getPid();
		this.legalName = company.getLegalName();
		this.alias = company.getAlias();
		this.companyType = company.getCompanyType();
		this.industry = company.getIndustry();
		this.address1 = company.getAddress1();
		this.address2 = company.getAddress2();
		this.countryName = company.getCountry().getName();
		this.countryCode = company.getCountry().getCode();
		this.stateName = company.getState().getName();
		this.stateCode = company.getState().getCode();
		this.districtName = company.getDistrict().getName();
		this.districtCode = company.getDistrict().getCode();
		this.email = company.getEmail();
		this.pincode = company.getPincode();
		this.location = company.getLocation();
		this.logo = company.getLogo();
		this.logoContentType = company.getLogoContentType();
		this.website = company.getWebsite();
		this.activated = company.getIsActivated();
		this.periodStartDate = company.getPeriodStartDate();
		this.periodEndDate = company.getPeriodEndDate();
		this.apiUrl = company.getApiUrl();
		this.onPremise = company.isOnPremise();
		this.gstNo = company.getGstNo();
		this.smsApiKey = company.getSmsApiKey();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public CompanyType getCompanyType() {
		return companyType;
	}

	public void setCompanyType(CompanyType companyType) {
		this.companyType = companyType;
	}

	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
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

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getLogoContentType() {
		return logoContentType;
	}

	public void setLogoContentType(String logoContentType) {
		this.logoContentType = logoContentType;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public LocalDate getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(LocalDate periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public LocalDate getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(LocalDate periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public boolean isOnPremise() {
		return onPremise;
	}

	public void setOnPremise(boolean onPremise) {
		this.onPremise = onPremise;
	}

	public String getSmsApiKey() {
		return smsApiKey;
	}

	public void setSmsApiKey(String smsApiKey) {
		this.smsApiKey = smsApiKey;
	}

	@Override
	public String toString() {
		return "CompanyViewDTO [legalName=" + legalName + ", apiUrl=" + apiUrl + ", onPremise=" + onPremise + "]";
	}

}
