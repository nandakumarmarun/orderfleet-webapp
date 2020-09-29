package com.orderfleet.webapp.web.vendor.odoo.dto;

public class OdooStockLocation {

	private long salesman_id;

	private long id;

	private String name;

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
