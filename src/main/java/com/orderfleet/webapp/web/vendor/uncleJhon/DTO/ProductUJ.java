package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductUJ {

	@JsonProperty("Group")
	public String getGroup() {
		return Group;
	}

	public void setGroup(String group) {
		Group = group;
	}

	String Group;

	@JsonProperty("alphaCode")
	public String getAlphaCode() {
		return this.alphaCode;
	}

	public void setAlphaCode(String alphaCode) {
		this.alphaCode = alphaCode;
	}

	String alphaCode;

	@JsonProperty("code")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	String code;

	@JsonProperty("hsnCode")
	public String getHsnCode() {
		return this.hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	String hsnCode;

	@JsonProperty("mrp")
	public int getMrp() {
		return this.mrp;
	}

	public void setMrp(int mrp) {
		this.mrp = mrp;
	}

	int mrp;

	@JsonProperty("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String name;

	@JsonProperty("sku")
	public String getSku() {
		return this.sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	String sku;

	@JsonProperty("stockQty")
	public int getStockQty() {
		return this.stockQty;
	}

	public void setStockQty(int stockQty) {
		this.stockQty = stockQty;
	}

	int stockQty;

	@JsonProperty("taxRate")
	public int getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}

	int taxRate;

	@JsonProperty("unitMlLtr")
	public int getUnitMlLtr() {
		return this.unitMlLtr;
	}

	public void setUnitMlLtr(int unitMlLtr) {
		this.unitMlLtr = unitMlLtr;
	}

	int unitMlLtr;

	@JsonProperty("unitQty")
	public int getUnitQty() {
		return this.unitQty;
	}

	public void setUnitQty(int unitQty) {
		this.unitQty = unitQty;
	}

	int unitQty;

	@JsonProperty("wholesalePrice")
	public double getWholesalePrice() {
		return this.wholesalePrice;
	}

	public void setWholesalePrice(double wholesalePrice) {
		this.wholesalePrice = wholesalePrice;
	}

	double wholesalePrice;

}
