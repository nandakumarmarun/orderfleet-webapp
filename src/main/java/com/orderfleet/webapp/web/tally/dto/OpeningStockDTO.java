package com.orderfleet.webapp.web.tally.dto;

import java.util.List;

public class OpeningStockDTO {

	private String productProfileName;
	private List<StockSummaryDTO> stockSummary;
	
	
	public String getProductProfileName() {
		return productProfileName;
	}
	public void setProductProfileName(String productProfileName) {
		this.productProfileName = productProfileName;
	}
	public List<StockSummaryDTO> getStockSummary() {
		return stockSummary;
	}
	public void setStockSummary(List<StockSummaryDTO> stockSummary) {
		this.stockSummary = stockSummary;
	}
	
	
	
}
