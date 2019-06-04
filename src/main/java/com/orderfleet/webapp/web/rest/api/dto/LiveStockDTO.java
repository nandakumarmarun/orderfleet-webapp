package com.orderfleet.webapp.web.rest.api.dto;


public class LiveStockDTO {

	private String productPid;
	
	private Double stock;
	

	public String getProductPid() {
		return productPid;
	}

	public void setProductPid(String productPid) {
		this.productPid = productPid;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	
}
