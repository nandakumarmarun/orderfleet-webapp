package com.orderfleet.webapp.web.vendor.odoo.dto;

public class RequestBodyOneOdooInvoice {
	private String jsonrpc;

	private ParamsOneOdooInvoice params;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public ParamsOneOdooInvoice getParams() {
		return params;
	}

	public void setParams(ParamsOneOdooInvoice params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "RequestBodyOneOdooInvoice [jsonrpc=" + jsonrpc + ", params=" + params + "]";
	}
}
