package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import java.util.ArrayList;



import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductProfileUJ {

	@JsonProperty("main")
	public ArrayList<ProductUJ> getProductUJ() {
		return this.main;
	}

	public void setMain(ArrayList<ProductUJ> main) {
		this.main = main;
	}

	ArrayList<ProductUJ> main;

	@JsonProperty("ordkey")
	public ArrayList<String> getOrdkey() {
		return this.ordkey;
	}

	public void setOrdkey(ArrayList<String> ordkey) {
		this.ordkey = ordkey;
	}

	ArrayList<String> ordkey;
}
