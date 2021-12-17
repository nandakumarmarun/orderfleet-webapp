package com.orderfleet.webapp.web.vendor.focus.dto;

public class ResponseBodyOdooUser {

	private String jsonrpc;

	private long id;

	private ResultOdooUser result;

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

	public ResultOdooUser getResult() {
		return result;
	}

	public void setResult(ResultOdooUser result) {
		this.result = result;
	}

}
