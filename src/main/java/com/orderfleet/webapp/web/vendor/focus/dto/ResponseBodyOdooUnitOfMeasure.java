package com.orderfleet.webapp.web.vendor.focus.dto;

public class ResponseBodyOdooUnitOfMeasure {

	private String jsonrpc;

	private long id;

	private ResultOdooUnitOfMeasure result;

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

	public ResultOdooUnitOfMeasure getResult() {
		return result;
	}

	public void setResult(ResultOdooUnitOfMeasure result) {
		this.result = result;
	}

}
