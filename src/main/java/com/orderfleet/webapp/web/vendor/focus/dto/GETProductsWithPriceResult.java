package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Count", "Product", "iStatus", "sMessage"})
public class GETProductsWithPriceResult {
	
	@JsonProperty("Count")
	private long count;
	
	@JsonProperty("Product")
	
	private List<ProductProfileNewFocus> product;
	
	@JsonProperty("iStatus")
	private long iStatus;
	
	@JsonProperty("sMessage")
	private String sMassage;

	@JsonProperty("Count")
	public long getCount() {
		return count;
	}

	@JsonProperty("Count")
	public void setCount(long count) {
		this.count = count;
	}

	@JsonProperty("Product")
	public List<ProductProfileNewFocus> getProduct() {
		return product;
	}

	
	@JsonProperty("Product")
	public void setProduct(List<ProductProfileNewFocus> product) {
		this.product = product;
	}

	@JsonProperty("iStatus")
	public long getiStatus() {
		return iStatus;
	}

	@JsonProperty("iStatus")
	public void setiStatus(long iStatus) {
		this.iStatus = iStatus;
	}

	@JsonProperty("sMessage")
	public String getsMassage() {
		return sMassage;
	}

	@JsonProperty("sMessage")
	public void setsMassage(String sMassage) {
		this.sMassage = sMassage;
	}
	
	
}
