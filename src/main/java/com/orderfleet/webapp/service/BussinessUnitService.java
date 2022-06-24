package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.BussinessUnitDTO;

public interface BussinessUnitService {

	String PID_PREFIX = "BU-";
	
	void save(List<BussinessUnitDTO> bussinessUnitDTOs);
}
