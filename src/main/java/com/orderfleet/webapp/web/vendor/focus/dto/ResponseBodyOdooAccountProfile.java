package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResponseBodyOdooAccountProfile {

	private String jsonrpc;

	private long id;

	private ResultOdooAccountProfile result;

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

	public ResultOdooAccountProfile getResult() {
		return result;
	}

	public void setResult(ResultOdooAccountProfile result) {
		this.result = result;
	}

}
