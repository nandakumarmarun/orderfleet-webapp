package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "data" })
public class ProductProfileResponceUJ {
	
	@JsonProperty("data")
	public ProductProfileUJ getProductProfileUJ() {
		return this.data;
	}

	public void setProductProfileUJ(ProductProfileUJ data) {
		this.data = data;
	}

	@JsonProperty("data")
	ProductProfileUJ data;

}
