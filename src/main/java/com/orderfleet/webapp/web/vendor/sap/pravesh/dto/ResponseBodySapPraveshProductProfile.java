package com.orderfleet.webapp.web.vendor.sap.pravesh.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "productCode", "productName", "unitQuantity", "productGroup", "stockLocation", "stockInHand",
		"sku", "productPrice" })
public class ResponseBodySapPraveshProductProfile {

	@JsonProperty("id")
	private long id;
	@JsonProperty("productCode")
	private String productCode;
	@JsonProperty("productName")
	private String productName;
	@JsonProperty("unitQuantity")
	private long unitQuantity;
	@JsonProperty("productGroup")
	private String productGroup;
	@JsonProperty("stockLocation")
	private String stockLocation;
	@JsonProperty("stockInHand")
	private double stockInHand;
	@JsonProperty("sku")
	private String sku;
	@JsonProperty("productPrice")
	private double productPrice;

	@JsonProperty("id")
	public long getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(long id) {
		this.id = id;
	}

	@JsonProperty("productCode")
	public String getProductCode() {
		return productCode;
	}

	@JsonProperty("productCode")
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@JsonProperty("productName")
	public String getProductName() {
		return productName;
	}

	@JsonProperty("productName")
	public void setProductName(String productName) {
		this.productName = productName;
	}

	@JsonProperty("unitQuantity")
	public long getUnitQuantity() {
		return unitQuantity;
	}

	@JsonProperty("unitQuantity")
	public void setUnitQuantity(long unitQuantity) {
		this.unitQuantity = unitQuantity;
	}

	@JsonProperty("productGroup")
	public String getProductGroup() {
		return productGroup;
	}

	@JsonProperty("productGroup")
	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	@JsonProperty("stockLocation")
	public String getStockLocation() {
		return stockLocation;
	}

	@JsonProperty("stockLocation")
	public void setStockLocation(String stockLocation) {
		this.stockLocation = stockLocation;
	}

	@JsonProperty("stockInHand")
	public double getStockInHand() {
		return stockInHand;
	}

	@JsonProperty("stockInHand")
	public void setStockInHand(double stockInHand) {
		this.stockInHand = stockInHand;
	}

	@JsonProperty("sku")
	public String getSku() {
		return sku;
	}

	@JsonProperty("sku")
	public void setSku(String sku) {
		this.sku = sku;
	}

	@JsonProperty("productPrice")
	public double getProductPrice() {
		return productPrice;
	}

	@JsonProperty("productPrice")
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

}
