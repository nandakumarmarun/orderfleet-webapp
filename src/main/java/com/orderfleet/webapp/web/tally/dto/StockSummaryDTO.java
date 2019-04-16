package com.orderfleet.webapp.web.tally.dto;

public class StockSummaryDTO {

	private String batchName;
	private String stockLocationName;
	private double quantity;
	
	
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getStockLocationName() {
		return stockLocationName;
	}
	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	
}
