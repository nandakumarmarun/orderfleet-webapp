package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOdooStockLocation {

	private String jsonrpc;

	private long id;

	private ResultOdooStockLocation result;

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

	public ResultOdooStockLocation getResult() {
		return result;
	}

	public void setResult(ResultOdooStockLocation result) {
		this.result = result;
	}

}
