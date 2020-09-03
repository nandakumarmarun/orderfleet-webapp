package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class NetSalesAndCollectionDTO {

	public double netSaleAmount;
	public double netCollectionAmount;
	public double netCollectionAmountCash;
	public double netCollectionAmountCheque;
	public double netCollectionAmountRtgs;

	public double getNetSaleAmount() {
		return netSaleAmount;
	}

	public void setNetSaleAmount(double netSaleAmount) {
		this.netSaleAmount = netSaleAmount;
	}

	public double getNetCollectionAmount() {
		return netCollectionAmount;
	}

	public void setNetCollectionAmount(double netCollectionAmount) {
		this.netCollectionAmount = netCollectionAmount;
	}

	public double getNetCollectionAmountCash() {
		return netCollectionAmountCash;
	}

	public void setNetCollectionAmountCash(double netCollectionAmountCash) {
		this.netCollectionAmountCash = netCollectionAmountCash;
	}

	public double getNetCollectionAmountCheque() {
		return netCollectionAmountCheque;
	}

	public void setNetCollectionAmountCheque(double netCollectionAmountCheque) {
		this.netCollectionAmountCheque = netCollectionAmountCheque;
	}

	public double getNetCollectionAmountRtgs() {
		return netCollectionAmountRtgs;
	}

	public void setNetCollectionAmountRtgs(double netCollectionAmountRtgs) {
		this.netCollectionAmountRtgs = netCollectionAmountRtgs;
	}

}
