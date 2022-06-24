package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Master_Code", "Master_Name" })
public class MasterDataResponseObject {

	@JsonProperty("Master_Code")
	private String masterCode;

	@JsonProperty("Master_Name")
	private String masterName;

	@JsonProperty("Master_Code")
	public String getMasterCode() {
		return masterCode;
	}

	@JsonProperty("Master_Code")
	public void setMasterCode(String masterCode) {
		this.masterCode = masterCode;
	}

	@JsonProperty("Master_Name")
	public String getMasterName() {
		return masterName;
	}

	@JsonProperty("Master_Name")
	public void setMaster_Name(String masterName) {
		this.masterName = masterName;
	}

}
