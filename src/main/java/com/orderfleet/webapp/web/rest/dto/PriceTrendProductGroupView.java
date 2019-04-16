package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class PriceTrendProductGroupView {

	private String pid;
	private String name;
	private List<PriceTrendProductView> productCategories;
	private List<FullMonth> months;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PriceTrendProductView> getProductCategories() {
		return productCategories;
	}

	public void setProductCategories(List<PriceTrendProductView> productCategories) {
		this.productCategories = productCategories;
	}

	public List<FullMonth> getMonths() {
		return months;
	}

	public void setMonths(List<FullMonth> months) {
		this.months = months;
	}

}
