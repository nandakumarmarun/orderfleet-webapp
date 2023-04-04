package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountProfileResponseUj {
	@JsonProperty("data")
	public AccountProfileUJ getAccountProfileUJ() {
		return this.data;
	}

	public void setAccountProfileUJ(AccountProfileUJ data) {
		this.data = data;
	}

	AccountProfileUJ data;

}
