package com.orderfleet.webapp.web.rest.api.dto;

public class StockLocationWiseStockDTO {
	
	private String pid;
	
	private String name;
	
	private double stock;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public double getStock() {
		return stock;
	}

	public void setStock(double stock) {
		this.stock = stock;
	}
	
}
