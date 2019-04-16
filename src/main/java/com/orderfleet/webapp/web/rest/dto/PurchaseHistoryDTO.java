package com.orderfleet.webapp.web.rest.dto;

public class PurchaseHistoryDTO {

	private String productPid;

	private String productName;

	private double quantity;

	public PurchaseHistoryDTO() {
	}

	public PurchaseHistoryDTO(String productPid, String productName, double quantity) {
		this.productPid = productPid;
		this.productName = productName;
		this.quantity = quantity;
	}

	public String getProductPid() {
		return productPid;
	}

	public void setProductPid(String productPid) {
		this.productPid = productPid;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

}
