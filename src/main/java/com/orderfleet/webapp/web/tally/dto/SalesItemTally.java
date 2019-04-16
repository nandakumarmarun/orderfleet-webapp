package com.orderfleet.webapp.web.tally.dto;

public class SalesItemTally {

	private String productProfileName;
	private double sellingRate;
	private double quantity;
	private String unit;
	private double discountPercentage;
	private double freeQuantity;
	private String stockLocationName;
	private double taxRate;
	private String remarks;
	
	
	public String getProductProfileName() {
		return productProfileName;
	}
	public void setProductProfileName(String productProfileName) {
		this.productProfileName = productProfileName;
	}
	public double getSellingRate() {
		return sellingRate;
	}
	public void setSellingRate(double sellingRate) {
		this.sellingRate = sellingRate;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public double getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public double getFreeQuantity() {
		return freeQuantity;
	}
	public void setFreeQuantity(double freeQuantity) {
		this.freeQuantity = freeQuantity;
	}
	public String getStockLocationName() {
		return stockLocationName;
	}
	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}
	public double getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "SalesItemTally [productProfileName=" + productProfileName + ", sellingRate=" + sellingRate
				+ ", quantity=" + quantity + ", unit=" + unit + ", discountPercentage=" + discountPercentage
				+ ", freeQuantity=" + freeQuantity + ", stockLocationName=" + stockLocationName + ", taxRate=" + taxRate
				+ ", remarks=" + remarks + "]";
	}
	
	
	
	
	
	
	
	
}
