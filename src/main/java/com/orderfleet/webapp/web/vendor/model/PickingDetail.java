package com.orderfleet.webapp.web.vendor.model;

public class PickingDetail {
	
	private Long id;
	
	private String productName;
	
	private String stockId;
	
	private double orderQty;
	
	private double pickedQty;
	
	private double currentQty;
	
	private String pick;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public double getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(double orderQty) {
		this.orderQty = orderQty;
	}

	public double getPickedQty() {
		return pickedQty;
	}

	public void setPickedQty(double pickedQty) {
		this.pickedQty = pickedQty;
	}

	public double getCurrentQty() {
		return currentQty;
	}

	public void setCurrentQty(double currentQty) {
		this.currentQty = currentQty;
	}

	public String getPick() {
		return pick;
	}

	public void setPick(String pick) {
		this.pick = pick;
	}

	
	
	
	
}
