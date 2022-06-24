package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Length_Code", "Length_Name","Length_Mtr" })
public class LengthTypeResponseObject {
 
	@JsonProperty("Length_Code")
	private String masterCode;

	@JsonProperty("Length_Name")
	private String masterName;
	
	@JsonProperty("Length_Mtr")
	private String MeterConversion;

	@JsonProperty("Master_Code")
	public String getMasterCode() {
		return masterCode;
	}

	@JsonProperty("Master_Code")
	public void setMasterCode(String masterCode) {
		this.masterCode = masterCode;
	}

	@JsonProperty("Length_Name")
	public String getMasterName() {
		return masterName;
	}

	@JsonProperty("Length_Name")
	public void setMaster_Name(String masterName) {
		this.masterName = masterName;
	}

	@JsonProperty("Length_Mtr")
	public String getMeterConversion() {
		return MeterConversion;
	}

	@JsonProperty("Length_Mtr")
	public void setMeterConversion(String meterConversion) {
		MeterConversion = meterConversion;
	}
	
}
