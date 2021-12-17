package com.orderfleet.webapp.web.vendor.focus.dto;

public class ResponseBodyOdooOutstandingInvoice {

	private String jsonrpc;

	private long id;

	private ResultOdooOutstandingInvoice result;

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

	public ResultOdooOutstandingInvoice getResult() {
		return result;
	}

	public void setResult(ResultOdooOutstandingInvoice result) {
		this.result = result;
	}
	
}
