package com.orderfleet.webapp.web.vendor.sap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Address", "Cellular", "Email", "str1", "str2" })
public class ResponseBodySapAccountProfile {

	@JsonProperty("Address")
	private String address;
	@JsonProperty("Cellular")
	private String cellular;
	@JsonProperty("Email")
	private String email;
	@JsonProperty("str1")
	private String str1;
	@JsonProperty("str2")
	private String str2;

	@JsonProperty("Address")
	public String getAddress() {
		return address;
	}

	@JsonProperty("Address")
	public void setAddress(String address) {
		this.address = address;
	}

	@JsonProperty("Cellular")
	public String getCellular() {
		return cellular;
	}

	@JsonProperty("Cellular")
	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	@JsonProperty("Email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("Email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("str1")
	public String getStr1() {
		return str1;
	}

	@JsonProperty("str1")
	public void setStr1(String str1) {
		this.str1 = str1;
	}

	@JsonProperty("str2")
	public String getStr2() {
		return str2;
	}

	@JsonProperty("str2")
	public void setStr2(String str2) {
		this.str2 = str2;
	}

}
