package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Account_Type", "Code", "Credit_Days", "Credit_Limit", "Name", "Route_Code", "SalesExecutive" })
public class AccountProfileFocus {

	@JsonProperty("Account_Type")
	private String accountType;

	@JsonProperty("Code")
	private String code;

	@JsonProperty("Credit_Days")
	private String creditDays;

	@JsonProperty("Credit_Limit")
	private String creditLimit;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Route_Code")
	private String routeCode;

	@JsonProperty("SalesExecutive")
	private String salesExecutive;

	@JsonProperty("Account_Type")
	public String getAccountType() {
		return accountType;
	}

	@JsonProperty("Account_Type")
	public void setAccountType(String accountType) {
		this.accountType = accountType;
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

	@JsonProperty("SalesExecutive")
	public String getSalesExecutive() {
		return salesExecutive;
	}

	@JsonProperty("SalesExecutive")
	public void setSalesExecutive(String salesExecutive) {
		this.salesExecutive = salesExecutive;
	}

}
