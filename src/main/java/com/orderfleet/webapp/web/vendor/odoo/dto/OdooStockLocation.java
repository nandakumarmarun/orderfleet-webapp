package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class OdooStockLocation {

	private List<OdooOpeningStock> product_qty;

	private long salesman_id;

	private long id;

	private String name;

	public List<OdooOpeningStock> getProduct_qty() {
		return product_qty;
	}

	public void setProduct_qty(List<OdooOpeningStock> product_qty) {
		this.product_qty = product_qty;
	}

	public long getSalesman_id() {
		return salesman_id;
	}

	public void setSalesman_id(long salesman_id) {
		this.salesman_id = salesman_id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
