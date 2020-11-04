package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOdooTaxList {
	private String jsonrpc;

	private long id;

	private ResultOdooTaxList result;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ResultOdooTaxList getResult() {
		return result;
	}

	public void setResult(ResultOdooTaxList result) {
		this.result = result;
	}
}
