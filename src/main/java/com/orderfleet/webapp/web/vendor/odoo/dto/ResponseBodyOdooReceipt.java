package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOdooReceipt {

	private String jsonrpc;

	private long id;

	private ResultOdooReceipt result;

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

	public ResultOdooReceipt getResult() {
		return result;
	}

	public void setResult(ResultOdooReceipt result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseBodyOdooInvoice [jsonrpc=" + jsonrpc + ", id=" + id + ", result=" + result + "]";
	}

}
