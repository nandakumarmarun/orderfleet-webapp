package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class OdooInvoiceLine {

	private long uom_id;

	private long product_id;

	private double quantity;

	private double price_unit;

	private double discount;

	private double is_foc;

	public long getUom_id() {
		return uom_id;
	}

	public void setUom_id(long uom_id) {
		this.uom_id = uom_id;
	}

	public long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPrice_unit() {
		return price_unit;
	}

	public void setPrice_unit(double price_unit) {
		this.price_unit = price_unit;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getIs_foc() {
		return is_foc;
	}

	public void setIs_foc(double is_foc) {
		this.is_foc = is_foc;
	}

	@Override
	public String toString() {
		return "OdooInvoiceLine [uom_id=" + uom_id + ", product_id=" + product_id + ", quantity=" + quantity
				+ ", price_unit=" + price_unit + ", discount=" + discount + ", is_foc=" + is_foc + "]";
	}

}
