package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;
import com.orderfleet.webapp.web.rest.dto.ContryFocusDTO;

public interface ContryFocusService {

	String PID_PREFIX = "CF-";

	void save(List<ContryFocusDTO> contryFocusDTO);
	
}
