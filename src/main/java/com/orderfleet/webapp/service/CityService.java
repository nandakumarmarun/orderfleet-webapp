package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.CityFocusDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictFocusDTO;

public interface CityService {
	String PID_PREFIX = "CF-";
	void save(List<CityFocusDTO> cityFocusDTODTO);
}
