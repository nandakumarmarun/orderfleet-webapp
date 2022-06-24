package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "GetMasterDataResult" })
public class MasterDataReponseFocus {

	@JsonProperty("GetMasterDataResult")
	private MasterDataFocus getMasterDataFocus;

	@JsonProperty("GetMasterDataResult")
	public MasterDataFocus getGetMasterDataFocus() {
		return getMasterDataFocus;
	}

	@JsonProperty("GetMasterDataResult")
	public void setGetMasterDataFocus(MasterDataFocus getMasterDataFocus) {
		this.getMasterDataFocus = getMasterDataFocus;
	}

}
