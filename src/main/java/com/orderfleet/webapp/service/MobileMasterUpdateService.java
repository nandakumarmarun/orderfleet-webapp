package com.orderfleet.webapp.service;



import com.orderfleet.webapp.domain.MobileMasterUpdate;
import com.orderfleet.webapp.web.rest.dto.MobileMasterUpdateDTO;


public interface MobileMasterUpdateService {

	String PID_PREFIX = "MMU-";
	MobileMasterUpdate convertMobileMasterUpdate(MobileMasterUpdateDTO mobileMasterUpdateDto);
	MobileMasterUpdate saveMobileMasterUpdate(MobileMasterUpdate mobileMasterUpdate );
}
