package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "User_Name", "Password", "Company_Code" })
public class AuthenticationRequstFocus {

	@JsonProperty("User_Name")
	private String userName;

	@JsonProperty("Password")
	private String password;

	@JsonProperty("Company_Code")
	private String companyCode;

	@JsonProperty("User_Name")
	public String getUserName() {
		return userName;
	}

	@JsonProperty("User_Name")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JsonProperty("Password")
	public String getPassword() {
		return password;
	}

	@JsonProperty("Password")
	public void setPassword(String password) {
		this.password = password;
	}

	@JsonProperty("Company_Code")
	public String getCompanyCode() {
		return companyCode;
	}

	@JsonProperty("Company_Code")
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

}
