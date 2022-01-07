package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "GetMasterDataResult" })
public class ProductProfileResponseFocus {

	@JsonProperty("GetMasterDataResult")
	private GetMasterDataResult getMasterDataResult;

	@JsonProperty("GetMasterDataResult")
	public GetMasterDataResult getGetMasterDataResult() {
		return getMasterDataResult;
	}

	@JsonProperty("GetMasterDataResult")
	public void setGetMasterDataResult(GetMasterDataResult getMasterDataResult) {
		this.getMasterDataResult = getMasterDataResult;
	}

}
