package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;

public interface DistrictFocusService {

	String PID_PREFIX = "DF-";
	
	void save(List<DistrictFocusDTO> districtFocusDTO);
}
