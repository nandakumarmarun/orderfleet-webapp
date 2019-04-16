package com.orderfleet.webapp.web.vendor.integre.dto;

public class OpeningStockVendorDTO {

	private String productProfileId;
	private String stockLocationId;
	private double stock;
	
	
	public String getProductProfileId() {
		return productProfileId;
	}
	public void setProductProfileId(String productProfileId) {
		this.productProfileId = productProfileId;
	}
	public String getStockLocationId() {
		return stockLocationId;
	}
	public void setStockLocationId(String stockLocationId) {
		this.stockLocationId = stockLocationId;
	}
	public double getStock() {
		return stock;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
	
	
}
