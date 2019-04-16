package com.orderfleet.webapp.web.rest.dto;

/**
 * a billinguser dto.
 *
 * @author Sarath
 * @since Mar 15, 2018
 *
 */
public class BillingUserDTO {

	private String login;
	private Long executionCount;
	private Long attendanceCount;
	private String employeeName;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Long getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Long executionCount) {
		this.executionCount = executionCount;
	}

	public Long getAttendanceCount() {
		return attendanceCount;
	}

	public void setAttendanceCount(Long attendanceCount) {
		this.attendanceCount = attendanceCount;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

}
