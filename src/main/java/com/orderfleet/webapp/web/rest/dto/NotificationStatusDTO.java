package com.orderfleet.webapp.web.rest.dto;

/**
 * 
 * 
 * @author Sarath
 * @since Sep 8, 2016
 */
public class NotificationStatusDTO {

	private int total;
	private int success;
	private int failed;

	public NotificationStatusDTO() {
		super();
	}

	public NotificationStatusDTO(int total, int success, int failed) {
		super();
		this.total = total;
		this.success = success;
		this.failed = failed;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	@Override
	public String toString() {
		return "NotificationStatusDTO [total=" + total + ", success=" + success + ", failed=" + failed + "]";
	}

}
