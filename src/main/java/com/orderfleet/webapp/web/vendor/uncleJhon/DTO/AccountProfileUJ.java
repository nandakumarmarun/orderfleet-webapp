package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountProfileUJ {
	@JsonProperty("main")
	public ArrayList<AccountUJ> getAccountUJ() {
		return this.main;
	}

	public void setMain(ArrayList<AccountUJ> main) {
		this.main = main;
	}

	ArrayList<AccountUJ> main;

	@JsonProperty("ordkey")
	public ArrayList<String> getOrdkey() {
		return this.ordkey;
	}

	public void setOrdkey(ArrayList<String> ordkey) {
		this.ordkey = ordkey;
	}

	ArrayList<String> ordkey;

}
