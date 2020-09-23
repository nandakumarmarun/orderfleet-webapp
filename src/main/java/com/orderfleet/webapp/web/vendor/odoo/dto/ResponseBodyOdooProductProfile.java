package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOdooProductProfile {

	private String jsonrpc;

	private long id;

	private ResultOdooProductProfile result;

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

	public ResultOdooProductProfile getResult() {
		return result;
	}

	public void setResult(ResultOdooProductProfile result) {
		this.result = result;
	}

}
