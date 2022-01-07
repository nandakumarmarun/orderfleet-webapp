package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Masterdata", "iCount", "iStatus", "sMessage" })
public class GetMasterDataResult {
	
	@JsonProperty("Masterdata")
	private List<ProductProfileFocus> productProfiles;
	
	@JsonProperty("iCount")
	private long iCount;

	@JsonProperty("iStatus")
	private long isStatus;

	@JsonProperty("sMessage")
	private String sMessage;

	@JsonProperty("Masterdata")
	public List<ProductProfileFocus> getProductProfiles() {
		return productProfiles;
	}

	@JsonProperty("Masterdata")
	public void setProductProfiles(List<ProductProfileFocus> productProfiles) {
		this.productProfiles = productProfiles;
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
