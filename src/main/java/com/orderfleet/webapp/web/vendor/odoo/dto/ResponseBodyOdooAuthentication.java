package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResponseBodyOdooAuthentication {

	private String jsonrpc;

	private String id;

	private ResultOdooAuthentication result;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ResultOdooAuthentication getResult() {
		return result;
	}

	public void setResult(ResultOdooAuthentication result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseBodyOdooAuthentication [jsonrpc=" + jsonrpc + ", id=" + id + ", result=" + result + "]";
	}

}
