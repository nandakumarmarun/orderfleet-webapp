package com.orderfleet.webapp.geolocation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleFormatedAddress {

	private String formatted_address;

	public GoogleFormatedAddress() {
	}

	public GoogleFormatedAddress(String formatted_address) {
		super();
		this.formatted_address = formatted_address;
	}

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

}
