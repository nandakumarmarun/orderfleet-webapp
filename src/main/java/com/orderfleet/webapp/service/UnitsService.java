package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.UnitsDTO;
import com.orderfleet.webapp.web.rest.dto.BankDTO;

public interface UnitsService {
	String PID_PREFIX = "UNITS-";
	
	UnitsDTO save(UnitsDTO unitsDTO);
	
	UnitsDTO saveorUpdate(List<UnitsDTO> unitsDTO);
	
	UnitsDTO update(UnitsDTO unitsDTO);
	
	List<UnitsDTO> findAll();
	
	UnitsDTO findOneByPid(String pid);
	
	void delete(String pid);

	
}
