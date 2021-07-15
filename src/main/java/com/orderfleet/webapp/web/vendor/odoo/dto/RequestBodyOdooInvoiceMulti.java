package com.orderfleet.webapp.web.vendor.odoo.dto;

public class RequestBodyOdooInvoiceMulti {

	private String jsonrpc;

	private ParamsOdooInvoiceMulti params;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public ParamsOdooInvoiceMulti getParams() {
		return params;
	}

	public void setParams(ParamsOdooInvoiceMulti params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "RequestBodyOdooInvoice [jsonrpc=" + jsonrpc + ", params=" + params + "]";
	}

}
