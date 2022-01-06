package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Account_Type", "Address", "Closing_Balance", "Code", "Credit_Days", "Credit_Limit", "GSTIN",
		"Name", "Route_Code", "Route_Name", "SalesExecutive", "city", "phone" })
public class AccountProfileFocus {

	@JsonProperty("Account_Type")
	private String accountType;

	@JsonProperty("Address")
	private String address;

	@JsonProperty("Closing_Balance")
	private String closing_Balance;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Credit_Days")
	private String creditDays;

	@JsonProperty("Credit_Limit")
	private String creditLimit;

	@JsonProperty("GSTIN")
	private String gstin;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Route_Code")
	private String routeCode;

	@JsonProperty("Route_Name")
	private String routeName;

	@JsonProperty("SalesExecutive")
	private String salesExecutive;

	@JsonProperty("city")
	private String city;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("Account_Type")
	public String getAccountType() {
		return accountType;
	}

	@JsonProperty("Account_Type")
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	@JsonProperty("Address")
	public String getAddress() {
		return address;
	}

	@JsonProperty("Address")
	public void setAddress(String address) {
		this.address = address;
	}

	@JsonProperty("Closing_Balance")
	public String getClosing_Balance() {
		return closing_Balance;
	}

	@JsonProperty("Closing_Balance")
	public void setClosing_Balance(String closing_Balance) {
		this.closing_Balance = closing_Balance;
	}

	@JsonProperty("Code")
	public String getCode() {
		return code;
	}

	@JsonProperty("Code")
	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty("Credit_Days")
	public String getCreditDays() {
		return creditDays;
	}

	@JsonProperty("Credit_Days")
	public void setCreditDays(String creditDays) {
		this.creditDays = creditDays;
	}

	@JsonProperty("Credit_Limit")
	public String getCreditLimit() {
		return creditLimit;
	}

	@JsonProperty("Credit_Limit")
	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	@JsonProperty("GSTIN")
	public String getGstin() {
		return gstin;
	}

	@JsonProperty("GSTIN")
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("Route_Code")
	public String getRouteCode() {
		return routeCode;
	}

	@JsonProperty("Route_Code")
	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	@JsonProperty("Route_Name")
	public String getRouteName() {
		return routeName;
	}

	@JsonProperty("Route_Name")
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	@JsonProperty("SalesExecutive")
	public String getSalesExecutive() {
		return salesExecutive;
	}

	@JsonProperty("SalesExecutive")
	public void setSalesExecutive(String salesExecutive) {
		this.salesExecutive = salesExecutive;
	}

	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@JsonProperty("city")
	public void setCity(String city) {
		this.city = city;
	}

	@JsonProperty("phone")
	public String getPhone() {
		return phone;
	}

	@JsonProperty("phone")
	public void setPhone(String phone) {
		this.phone = phone;
	}

}
