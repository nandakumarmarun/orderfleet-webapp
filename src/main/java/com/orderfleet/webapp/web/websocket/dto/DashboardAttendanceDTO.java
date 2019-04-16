package com.orderfleet.webapp.web.websocket.dto;

public class DashboardAttendanceDTO {

	private Long totalUsers;

	private Long attendedUsers;

	public DashboardAttendanceDTO(Long totalUsers, Long attendedUsers) {
		super();
		this.totalUsers = totalUsers;
		this.attendedUsers = attendedUsers;
	}

	public Long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(Long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public Long getAttendedUsers() {
		return attendedUsers;
	}

	public void setAttendedUsers(Long attendedUsers) {
		this.attendedUsers = attendedUsers;
	}

}
