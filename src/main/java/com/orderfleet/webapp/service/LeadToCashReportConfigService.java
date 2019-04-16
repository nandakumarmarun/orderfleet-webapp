package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.LeadToCashReportConfigDTO;

public interface LeadToCashReportConfigService {

	String PID_PREFIX = "LCRC-";
	
	List<LeadToCashReportConfigDTO> findAllByCompany();
	
	List<String> findStagePidsByCompany();
	
	LeadToCashReportConfigDTO saveLeadToCashReportConfig (LeadToCashReportConfigDTO leadToCashReportConfigDTO);
	
	LeadToCashReportConfigDTO updateLeadToCashReportConfig (LeadToCashReportConfigDTO leadToCashReportConfigDTO);
	
	Optional<LeadToCashReportConfigDTO> findLeadToCashReportConfigByPid (String pid);
	
	void delete(String pid);
}
