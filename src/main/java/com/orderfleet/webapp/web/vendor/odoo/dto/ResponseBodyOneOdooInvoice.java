package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOneOdooInvoice {
	private String jsonrpc;

	private long id;

	private ResultOneOdooInvoice result;

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

	public ResultOneOdooInvoice getResult() {
		return result;
	}

	public void setResult(ResultOneOdooInvoice result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseBodyOneOdooInvoice [jsonrpc=" + jsonrpc + ", id=" + id + ", result=" + result + "]";
	}
}
