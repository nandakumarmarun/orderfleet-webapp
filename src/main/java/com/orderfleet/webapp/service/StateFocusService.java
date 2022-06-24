package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.StateFocusDTO;

public interface StateFocusService {

	String PID_PREFIX = "SF-";
	
	void save(List<StateFocusDTO> StateFocusDTO);
}
