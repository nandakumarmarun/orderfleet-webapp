package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.VehicleDTO;

public interface VehicleService {

	String PID_PREFIX = "VHCL-";
	
	VehicleDTO save(VehicleDTO vehicleDTO);

	VehicleDTO update(VehicleDTO vehicleDTO);

	List<VehicleDTO> findAllByCompany();

	Optional<VehicleDTO> findOneByPid(String pid);
	
	void delete(String pid);
}
