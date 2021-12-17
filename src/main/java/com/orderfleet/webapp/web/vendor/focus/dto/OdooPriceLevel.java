package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class OdooPriceLevel {

	private String name;

	private String date_end;

	private String date_start;

	private long version_id;

	private List<OdooPriceLevelList> product_list;

	private long pricelist_id;

	private String pricelist_name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate_end() {
		return date_end;
	}

	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}

	public String getDate_start() {
		return date_start;
	}

	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}

	public long getVersion_id() {
		return version_id;
	}

	public void setVersion_id(long version_id) {
		this.version_id = version_id;
	}

	public List<OdooPriceLevelList> getProduct_list() {
		return product_list;
	}

	public void setProduct_list(List<OdooPriceLevelList> product_list) {
		this.product_list = product_list;
	}

	public long getPricelist_id() {
		return pricelist_id;
	}

	public void setPricelist_id(long pricelist_id) {
		this.pricelist_id = pricelist_id;
	}

	public String getPricelist_name() {
		return pricelist_name;
	}

	public void setPricelist_name(String pricelist_name) {
		this.pricelist_name = pricelist_name;
	}

}
