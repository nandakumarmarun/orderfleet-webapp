package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResponseBodyOdooPriceLevel {

	private String jsonrpc;

	private long id;

	private ResultOdooPriceLevel result;

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

	public ResultOdooPriceLevel getResult() {
		return result;
	}

	public void setResult(ResultOdooPriceLevel result) {
		this.result = result;
	}

}
