package com.orderfleet.webapp.web.vendor.odoo.dto;

public class OdooProductProfile {

	private long category;

	private String group;

	private String name;

	private String default_code;

	private String standard_price;

	private long id;

	private long uom;

	private Object tax_ids;

	public long getCategory() {
		return category;
	}

	public void setCategory(long category) {
		this.category = category;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefault_code() {
		return default_code;
	}

	public void setDefault_code(String default_code) {
		this.default_code = default_code;
	}

	public String getStandard_price() {
		return standard_price;
	}

	public void setStandard_price(String standard_price) {
		this.standard_price = standard_price;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUom() {
		return uom;
	}

	public void setUom(long uom) {
		this.uom = uom;
	}

	public Object getTax_ids() {
		return tax_ids;
	}

	public void setTax_ids(Object tax_ids) {
		this.tax_ids = tax_ids;
	}

}
