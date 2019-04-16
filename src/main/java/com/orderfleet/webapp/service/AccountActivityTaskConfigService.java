package com.orderfleet.webapp.service;


import com.orderfleet.webapp.web.rest.dto.AccountActivityTaskConfigDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;

public interface AccountActivityTaskConfigService {

	String PID_PREFIX = "AATC-";
	
	AccountActivityTaskConfigDTO saveActivityAccountTypeConfig(AccountActivityTaskConfigDTO activityAccountTypeConfigDTO);
	
	void createTaskAndSendNotification(LeadManagementDTO accountProfile);
	
}
