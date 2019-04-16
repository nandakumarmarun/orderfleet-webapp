package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;

import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.web.rest.dto.AttendanceSubgroupApprovalRequestDTO;

public interface AttendanceSubgroupApprovalRequestService {
	
	List<AttendanceSubgroupApprovalRequestDTO>findAllByCompany();
	
	List<AttendanceSubgroupApprovalRequestDTO> findAllByUsers(String userPid);
	
	AttendanceSubgroupApprovalRequest approveAttendanceRequest(Long id,String message);
	
	void rejectAttendanceRequest(Long id,String message);
	
	void sendNotification(AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest);
	
	List<AttendanceSubgroupApprovalRequestDTO>findAllByRequestedDateBetween(LocalDate fromDate,LocalDate toDate);
}
