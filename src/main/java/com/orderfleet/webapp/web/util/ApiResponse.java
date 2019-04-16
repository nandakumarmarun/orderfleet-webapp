package com.orderfleet.webapp.web.util;

/**
 * @author Shaheer
 * @since September 01, 2018
 *
 */
public final class ApiResponse<T> {

	private String status;

	private String description;

	private T responseBody;

	public ApiResponse() {
	}

	public ApiResponse(String status, String description) {
		this.status = status;
		this.description = description;
	}

	public ApiResponse(String status, T responseBody) {
		this.status = status;
		this.responseBody = responseBody;
	}

	public ApiResponse(String status, String description, T responseBody) {
		this.status = status;
		this.description = description;
		this.responseBody = responseBody;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public T getBaseResponse() {
		return responseBody;
	}

	public void setBaseResponse(T baseResponse) {
		this.responseBody = baseResponse;
	}

	@Override
	public String toString() {
		return "ApiResponse [status=" + status + ", description=" + description + ", responseBody=" + responseBody + "]";
	}
	
}
