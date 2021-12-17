package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdooUser {

	private long status;

	private String message;

	private List<OdooUser> response;

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<OdooUser> getResponse() {
		return response;
	}

	public void setResponse(List<OdooUser> response) {
		this.response = response;
	}

}
