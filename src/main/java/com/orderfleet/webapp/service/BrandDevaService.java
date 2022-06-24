package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.dto.BrandDevaDTO;

public interface BrandDevaService {

	String PID_PREFIX = "BRD-";
	
	void save(List<BrandDevaDTO> brandDevaDTO);
}
