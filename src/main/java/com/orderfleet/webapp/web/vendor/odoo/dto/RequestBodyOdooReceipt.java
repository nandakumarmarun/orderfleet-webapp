package com.orderfleet.webapp.web.vendor.odoo.dto;

public class RequestBodyOdooReceipt {

	private String jsonrpc;
	
	private ParamsReceiptOdoo params;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public ParamsReceiptOdoo getParams() {
		return params;
	}

	public void setParams(ParamsReceiptOdoo params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "RequestBodyOdooReceipt [jsonrpc=" + jsonrpc + ", params=" + params + "]";
	}

}
