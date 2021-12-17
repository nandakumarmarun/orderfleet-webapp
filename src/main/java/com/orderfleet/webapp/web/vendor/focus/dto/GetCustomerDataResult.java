package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Account", "iCount", "iStatus", "sMessage" })
public class GetCustomerDataResult {

	@JsonProperty("Account")
	private List<AccountProfileFocus> accountProfiles;

	@JsonProperty("iCount")
	private long iCount;

	@JsonProperty("iStatus")
	private long isStatus;

	@JsonProperty("sMessage")
	private String sMessage;

	@JsonProperty("Account")
	public List<AccountProfileFocus> getAccountProfiles() {
		return accountProfiles;
	}

	@JsonProperty("Account")
	public void setAccountProfiles(List<AccountProfileFocus> accountProfiles) {
		this.accountProfiles = accountProfiles;
	}

	@JsonProperty("iCount")
	public long getiCount() {
		return iCount;
	}

	@JsonProperty("iCount")
	public void setiCount(long iCount) {
		this.iCount = iCount;
	}

	@JsonProperty("iStatus")
	public long getIsStatus() {
		return isStatus;
	}

	@JsonProperty("iStatus")
	public void setIsStatus(long isStatus) {
		this.isStatus = isStatus;
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
