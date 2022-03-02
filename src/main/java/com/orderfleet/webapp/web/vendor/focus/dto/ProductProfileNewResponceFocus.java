package com.orderfleet.webapp.web.vendor.focus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "GETProductsWithPriceResult" })
public class ProductProfileNewResponceFocus {
	
	@JsonProperty("GETProductsWithPriceResult")
	private GETProductsWithPriceResult getProductsWithPriceResult;

	@JsonProperty("GETProductsWithPriceResult")
	public GETProductsWithPriceResult getGetProductsWithPriceResult() {
		return getProductsWithPriceResult;
	}

	@JsonProperty("GETProductsWithPriceResult")
	public void setGetProductsWithPriceResult(GETProductsWithPriceResult getProductsWithPriceResult) {
		this.getProductsWithPriceResult = getProductsWithPriceResult;
	}
	
	
}
