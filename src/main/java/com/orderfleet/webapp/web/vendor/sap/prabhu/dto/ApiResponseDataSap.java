package com.orderfleet.webapp.web.vendor.sap.prabhu.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "id" })
public class ApiResponseDataSap {

	@JsonProperty("status")
	private String status;
	@JsonProperty("id")
	private List<String> id = null;

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("id")
	public List<String> getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(List<String> id) {
		this.id = id;
	}

}