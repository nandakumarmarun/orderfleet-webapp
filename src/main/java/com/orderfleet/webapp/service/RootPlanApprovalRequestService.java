package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.RootPlanApprovalRequestDTO;

public interface RootPlanApprovalRequestService {

	List<RootPlanApprovalRequestDTO>findAllByCompany();
	
	List<RootPlanApprovalRequestDTO> findAllByUsers(String userPid);
	
	boolean approveRootPlanRequest(Long id,String message);
	
	void rejectRootPlanRequest(Long id,String message);
	
	List<RootPlanApprovalRequestDTO>findAllByRequestedDateBetween(LocalDate fromDate,LocalDate toDate);
}
