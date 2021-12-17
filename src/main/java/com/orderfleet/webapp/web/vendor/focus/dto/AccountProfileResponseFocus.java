package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "GetCustomerDataResult" })
public class AccountProfileResponseFocus {

	@JsonProperty("GetCustomerDataResult")
	private GetCustomerDataResult getCustomerDataResult;

	@JsonProperty("GetCustomerDataResult")
	public GetCustomerDataResult getGetCustomerDataResult() {
		return getCustomerDataResult;
	}

	@JsonProperty("GetCustomerDataResult")
	public void setGetCustomerResult(GetCustomerDataResult getCustomerDataResult) {
		this.getCustomerDataResult = getCustomerDataResult;
	}

}
