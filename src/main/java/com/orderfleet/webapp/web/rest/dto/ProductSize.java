package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

public class ProductSize {

	private double size;

	private List<String> products;

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public List<String> getProducts() {
		return products;
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}

}
