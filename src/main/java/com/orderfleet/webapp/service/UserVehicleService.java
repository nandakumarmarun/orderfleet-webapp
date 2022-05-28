package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;

public interface UserVehicleService {
	
	String PID_PREFIX = "UVS-";
	
	void save(String userPid, String assignedvehicle);
	
	VehicleDTO findVehicleByUserPid(String userPid);

}
