package com.orderfleet.webapp.web.vendor.focus.dto;

public class ResponseBodyOneOdooInvoice {
	private String jsonrpc;

	private long id;
	
	private ResultOdooInvoice result;
	// private ResultOneOdooInvoice result;

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

	public ResultOdooInvoice getResult() {
		return result;
	}

	public void setResult(ResultOdooInvoice result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseBodyOneOdooInvoice [jsonrpc=" + jsonrpc + ", id=" + id + ", result=" + result + "]";
	}

	
}
