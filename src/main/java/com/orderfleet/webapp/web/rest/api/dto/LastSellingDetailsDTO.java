package com.orderfleet.webapp.web.rest.api.dto;

public class LastSellingDetailsDTO {

	private double sellingRate;

	private double quantity;

	private double totalValue;

	private String orderDate;

	public LastSellingDetailsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LastSellingDetailsDTO(double sellingRate, double quantity, double totalValue, String orderDate) {
		super();
		this.sellingRate = sellingRate;
		this.quantity = quantity;
		this.totalValue = totalValue;
		this.orderDate = orderDate;
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

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

}
