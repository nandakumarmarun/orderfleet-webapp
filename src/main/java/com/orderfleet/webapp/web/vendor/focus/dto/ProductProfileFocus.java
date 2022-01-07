package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Master_Code", "Master_Name" })
public class ProductProfileFocus {
	@JsonProperty("Master_Code")
	private String master_Code;
	@JsonProperty("Master_Name")
	private String master_Name;

	@JsonProperty("Master_Code")
	public String getMaster_Code() {
		return master_Code;
	}

	@JsonProperty("Master_Code")
	public void setMaster_Code(String master_Code) {
		this.master_Code = master_Code;
	}

	@JsonProperty("Master_Name")
	public String getMaster_Name() {
		return master_Name;
	}

	@JsonProperty("Master_Name")
	public void setMaster_Name(String master_Name) {
		this.master_Name = master_Name;
	}

}
