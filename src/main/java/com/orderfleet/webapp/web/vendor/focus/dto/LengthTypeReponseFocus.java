package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "GetLengthTypeResult" })
public class LengthTypeReponseFocus {

	@JsonProperty("GetLengthTypeResult")
	private LengthTypeDataFocus LengthTypeDataFocus;

	@JsonProperty("GetLengthTypeResult")
	public LengthTypeDataFocus getLengthTypeDataFocus() {
		return LengthTypeDataFocus;
	}

	@JsonProperty("GetLengthTypeResult")
	public void setLengthTypeDataFocus(LengthTypeDataFocus lengthTypeDataFocus) {
		LengthTypeDataFocus = lengthTypeDataFocus;
	}
}
