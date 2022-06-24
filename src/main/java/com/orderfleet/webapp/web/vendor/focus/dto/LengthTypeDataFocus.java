package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "LengthType", "iCount", "iStatus", "sMessage" })
public class LengthTypeDataFocus {

	@JsonProperty("LengthType")
	private List<LengthTypeResponseObject> masterDatas;
	
	@JsonProperty("iCount")
	private long iCount;

	@JsonProperty("iStatus")
	private long isStatus;

	@JsonProperty("sMessage")
	private String sMessage;

	public List<LengthTypeResponseObject > getMasterDatas() {
		return masterDatas;
	}

	public void setMasterDatas(List<LengthTypeResponseObject > masterDatas) {
		this.masterDatas = masterDatas;
	}

	public long getiCount() {
		return iCount;
	}

	public void setiCount(long iCount) {
		this.iCount = iCount;
	}

	public long getIsStatus() {
		return isStatus;
	}

	public void setIsStatus(long isStatus) {
		this.isStatus = isStatus;
	}

	public String getsMessage() {
		return sMessage;
	}

	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}

}


