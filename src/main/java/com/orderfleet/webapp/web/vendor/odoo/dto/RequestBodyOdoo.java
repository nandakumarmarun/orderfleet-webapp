package com.orderfleet.webapp.web.vendor.odoo.dto;

public class RequestBodyOdoo {

	private String jsonrpc;

	private ParamsOdoo params;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public ParamsOdoo getParams() {
		return params;
	}

	public void setParams(ParamsOdoo params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "RequestBodyOdoo [jsonrpc=" + jsonrpc + ", params=" + params + "]";
	}

}
