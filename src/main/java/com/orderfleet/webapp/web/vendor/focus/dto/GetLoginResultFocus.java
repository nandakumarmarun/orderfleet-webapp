package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Auth_Token", "iStatus", "obj", "sMessage" })
public class GetLoginResultFocus {

	@JsonProperty("Auth_Token")
	private String authToken;

	@JsonProperty("iStatus")
	private long isStatus;

	@JsonProperty("obj")
	private String obj;

	@JsonProperty("sMessage")
	private String sMessage;

	@JsonProperty("Auth_Token")
	public String getAuthToken() {
		return authToken;
	}

	@JsonProperty("Auth_Token")
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	@JsonProperty("iStatus")
	public long getIsStatus() {
		return isStatus;
	}

	@JsonProperty("iStatus")
	public void setIsStatus(long isStatus) {
		this.isStatus = isStatus;
	}

	@JsonProperty("obj")
	public String getObj() {
		return obj;
	}

	@JsonProperty("obj")
	public void setObj(String obj) {
		this.obj = obj;
	}

	@JsonProperty("sMessage")
	public String getsMessage() {
		return sMessage;
	}

	@JsonProperty("sMessage")
	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}

}
