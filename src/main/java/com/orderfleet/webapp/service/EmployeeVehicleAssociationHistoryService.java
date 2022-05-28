package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeVehicleAssosiationHistoryDTO;

public interface EmployeeVehicleAssociationHistoryService {
	
	String PID_PREFIX = "EVAH-";

	EmployeeVehicleAssosiationHistoryDTO save(EmployeeVehicleAssosiationHistoryDTO EmployeeVehicleAssosiationHistoryDTO);
	
}
