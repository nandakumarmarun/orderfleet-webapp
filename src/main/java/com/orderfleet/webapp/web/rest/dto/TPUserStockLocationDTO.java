package com.orderfleet.webapp.web.rest.dto;

public class TPUserStockLocationDTO {

	private String userId;

	private String stockLocationName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

}
