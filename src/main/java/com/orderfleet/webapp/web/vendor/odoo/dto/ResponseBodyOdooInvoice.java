package com.orderfleet.webapp.web.vendor.odoo.dto;

public class ResponseBodyOdooInvoice {

	private String jsonrpc;

	private long id;

	private ResultOdooInvoice result;

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
		return "ResponseBodyOdooInvoice [jsonrpc=" + jsonrpc + ", id=" + id + ", result=" + result + "]";
	}
	
	

}
