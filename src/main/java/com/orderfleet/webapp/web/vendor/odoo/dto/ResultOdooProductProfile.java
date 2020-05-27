package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooProductProfile {

	private List<String> taxRate;

	private String productCode;

	private String hsnCode;

	private long size;

	private double price;

	private List<UnitQtyOdooProductProfile> UnitQty;

	private String name;

	private String id;

	private List<String> sku;

	public List<String> getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(List<String> taxRate) {
		this.taxRate = taxRate;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public List<UnitQtyOdooProductProfile> getUnitQty() {
		return UnitQty;
	}

	public void setUnitQty(List<UnitQtyOdooProductProfile> unitQty) {
		UnitQty = unitQty;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSku() {
		return sku;
	}

	public void setSku(List<String> sku) {
		this.sku = sku;
	}

}
