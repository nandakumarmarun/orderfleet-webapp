package com.orderfleet.webapp.web.vendor.garuda.dto;

public class SalesOrderDetailGarudaDTO {
	private String productCode;
	
	private double quantity;
	
	private double rate;
	
	private double mrp;
	
	private double discPer;
	
	private double discPrice;
	
	private double taxPer;
	
	private double freeQuantity;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public double getDiscPer() {
		return discPer;
	}

	public void setDiscPer(double discPer) {
		this.discPer = discPer;
	}

	public double getDiscPrice() {
		return discPrice;
	}

	public void setDiscPrice(double discPrice) {
		this.discPrice = discPrice;
	}

	public double getTaxPer() {
		return taxPer;
	}

	public void setTaxPer(double taxPer) {
		this.taxPer = taxPer;
	}

	public double getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(double freeQuantity) {
		this.freeQuantity = freeQuantity;
	}
}
