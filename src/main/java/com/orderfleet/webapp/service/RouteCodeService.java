package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.RouteCodeDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;

public interface RouteCodeService {

	String PID_PREFIX = "RC-";
	
	void save(List<RouteCodeDTO> routeCodeDTOs);
	
}
