package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ResultOdooAccountProfile {

	private long status;

	private String message;

	private List<GetLoginResultFocus> response;

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

	public List<GetLoginResultFocus> getResponse() {
		return response;
	}

	public void setResponse(List<GetLoginResultFocus> response) {
		this.response = response;
	}

}
