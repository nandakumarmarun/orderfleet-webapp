package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class SetDiscountPercentage {
	
	private List<String> productGroups;
	
	private double discountPercentage;

	public List<String> getProductGroups() {
		return productGroups;
	}

	public void setProductGroups(List<String> productGroups) {
		this.productGroups = productGroups;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	@Override
	public String toString() {
		return "SetDiscountPercentage [productGroups=" + productGroups + ", discountPercentage=" + discountPercentage
				+ "]";
	}

	
	
}
