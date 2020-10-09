package com.orderfleet.webapp.web.vendor.odoo.dto;

public class RequestBodyOdooInvoice {

	private String jsonrpc;

	private ParamsOdooInvoice params;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public ParamsOdooInvoice getParams() {
		return params;
	}

	public void setParams(ParamsOdooInvoice params) {
		this.params = params;
	}

}
