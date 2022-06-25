package com.orderfleet.webapp.web.rest.dto;

public class StockDetailsTotalDTO {

	private double documentTotal;
	private double receivedvalue;
	private double totalCash;
	private double totalCheque;
	public StockDetailsTotalDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public StockDetailsTotalDTO(double documentTotal, double receivedvalue, double totalCash, double totalCheque) {
		super();
		this.documentTotal = documentTotal;
		this.receivedvalue = receivedvalue;
		this.totalCash = totalCash;
		this.totalCheque = totalCheque;
	}
	public double getDocumentTotal() {
		return documentTotal;
	}
	public void setDocumentTotal(double documentTotal) {
		this.documentTotal = documentTotal;
	}
	public double getReceivedvalue() {
		return receivedvalue;
	}
	public void setReceivedvalue(double receivedvalue) {
		this.receivedvalue = receivedvalue;
	}
	public double getTotalCash() {
		return totalCash;
	}
	public void setTotalCash(double totalCash) {
		this.totalCash = totalCash;
	}
	public double getTotalCheque() {
		return totalCheque;
	}
	public void setTotalCheque(double totalCheque) {
		this.totalCheque = totalCheque;
	}
	@Override
	public String toString() {
		return "StockDetailsTotalDTO [documentTotal=" + documentTotal + ", receivedvalue=" + receivedvalue
				+ ", totalCash=" + totalCash + ", totalCheque=" + totalCheque + "]";
	}
	
	
}
