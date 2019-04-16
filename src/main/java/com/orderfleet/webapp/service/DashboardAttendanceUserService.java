package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

public interface DashboardAttendanceUserService {
	List<UserDTO> findUserByDashboardAttendanceId(Long id);
	
	void saveAssignedUsers(Long pid,String assignedUsers);
}
