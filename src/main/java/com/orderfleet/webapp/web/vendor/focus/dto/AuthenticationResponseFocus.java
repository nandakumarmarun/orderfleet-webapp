package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "GetloginResult" })
public class AuthenticationResponseFocus {

	@JsonProperty("GetloginResult")
	private GetLoginResultFocus getloginResult;

	@JsonProperty("GetloginResult")
	public GetLoginResultFocus getGetloginResult() {
		return getloginResult;
	}

	@JsonProperty("GetloginResult")
	public void setGetloginResult(GetLoginResultFocus getloginResult) {
		this.getloginResult = getloginResult;
	}

}
