package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

public interface DashboardItemUserService {

	List<UserDTO> findUserByDashboardItemPid(String pid);
	
	void saveAssignedUsers(String pid,String assignedUsers);
}
