package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResponseBodyOdooAccountProfile {

	private List<ResultOdooAccountProfile> result;

	private String id;

	private String jsonrpc;

	public List<ResultOdooAccountProfile> getResult() {
		return result;
	}

	public void setResult(List<ResultOdooAccountProfile> result) {
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

}
