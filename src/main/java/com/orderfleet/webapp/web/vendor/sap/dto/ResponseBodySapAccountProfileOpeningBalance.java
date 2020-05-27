package com.orderfleet.webapp.web.vendor.sap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Id", "Name", "OpeningBalance" })
public class ResponseBodySapAccountProfileOpeningBalance {

	@JsonProperty("Id")
	private String id;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("OpeningBalance")
	private String openingBalance;

	@JsonProperty("Id")
	public String getId() {
		return id;
	}

	@JsonProperty("Id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("Name")
	public String getName() {
		return name;
	}

	@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("OpeningBalance")
	public String getOpeningBalance() {
		return openingBalance;
	}

	@JsonProperty("OpeningBalance")
	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}

}
