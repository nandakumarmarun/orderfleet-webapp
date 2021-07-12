package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.SalesLedgerDTO;

/**
 * Service Interface for managing EmployeeProfileSalesLedgers.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */
public interface EmployeeProfileSalesLedgerService {

	/**
	 * Save a EmployeeProfileSalesLedgers.
	 * 
	 * @param employeeProfilePid
	 * @param assignedSalesLedgerss
	 */
	void save(String employeeProfilePid, String assignedSalesLedgerss);

	List<SalesLedgerDTO> findSalesLedgersByEmployeeProfileIsCurrentUser();

	List<SalesLedgerDTO> findSalesLedgersByEmployeeProfilePid(String employeeProfilePid);

	List<SalesLedgerDTO> findSalesLedgersByEmployeeProfileIsCurrentUserAndlastModifiedDate(
			LocalDateTime lastModifiedDate);

	List<SalesLedgerDTO> findSalesLedgersByUserPid(String userPid);

	List<SalesLedgerDTO> findSalesLedgersByUserPidIn(List<String> UserPids);

}
