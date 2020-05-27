package com.orderfleet.webapp.web.vendor.odoo.dto;

public class RequestBodyOdoo {

	private String method;

	private String jsonrpc;

	private ParamsOdoo params;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

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
		return "RequestBody [method=" + method + ", jsonrpc=" + jsonrpc + ", params=" + params + "]";
	}
	
	

}
