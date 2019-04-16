package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class SetTaxRate {

	private List<String> productGroups;

	private double taxRate;

	public List<String> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(List<String> productGroups) {
		this.productGroups = productGroups;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

}
