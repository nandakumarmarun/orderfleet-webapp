package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOdoo {

	private String jsonrpc;

	private long id;

	private ResultOdoo result;

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

	public ResultOdoo getResult() {
		return result;
	}

	public void setResult(ResultOdoo result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseBodyOdooInvoice [jsonrpc=" + jsonrpc + ", id=" + id + ", result=" + result + "]";
	}

}
