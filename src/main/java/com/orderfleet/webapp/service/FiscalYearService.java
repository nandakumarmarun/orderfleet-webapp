package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orderfleet.webapp.web.rest.dto.FiscalYearDTO;

public interface FiscalYearService {

	String PID_PREFIX = "FY-";
	
	void save(List<FiscalYearDTO> FiscalYearDTO);
}
