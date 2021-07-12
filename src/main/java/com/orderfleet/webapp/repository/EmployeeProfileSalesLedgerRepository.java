package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileSalesLedger;
import com.orderfleet.webapp.domain.SalesLedger;;

/**
 * Spring Data JPA repository for the EmployeeProfileSalesLedger entity.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
public interface EmployeeProfileSalesLedgerRepository extends JpaRepository<EmployeeProfileSalesLedger, Long> {

	@Query("select employeeProfileSalesLedger.salesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.login = ?#{principal.username}")
	List<SalesLedger> findSalesLedgersByEmployeeProfileIsCurrentUser();
	
	@Query("select employeeProfileSalesLedger.salesLedger.id from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.login = ?#{principal.username}")
	Set<Long> findSalesLedgerIdsByEmployeeProfileIsCurrentUser();

	@Query("select employeeProfileSalesLedger.salesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.pid = ?1 ")
	List<SalesLedger> findSalesLedgersByEmployeeProfilePid(String employeeProfilePid);

	void deleteByEmployeeProfilePid(String employeeProfilePid);
	
	void deleteByEmployeeProfilePidIn(List<String> employeeProfilePids);

	@Query("select employeeProfileSalesLedger.salesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.pid = ?1")
	List<SalesLedger> findSalesLedgersByUserPid(String userPid);

	List<EmployeeProfileSalesLedger> findBySalesLedgerCompanyPid(String companyPid);
	
	EmployeeProfileSalesLedger findTop1ByEmployeeProfilePid(String employeeProfilePid);

	@Query("select employeeProfileSalesLedger.salesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.login = ?#{principal.username} and employeeProfileSalesLedger.salesLedger.lastModifiedDate > ?1")
	List<SalesLedger> findSalesLedgersByEmployeeProfileIsCurrentUserAndlastModifiedDate(LocalDateTime lastModifiedDate);
	
	@Query("select employeeProfileSalesLedger.employeeProfile.user.id from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.salesLedger.id in ?1")
	Set<Long> findEmployeeUserIdsBySalesLedgerIdIn(List<Long> salesLedgerIds);

	@Query("select DISTINCT employeeProfileSalesLedger.salesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.pid in ?1")
	List<SalesLedger> findSalesLedgersByUserPidIn(List<String> userPids);
	
	@Query("select DISTINCT employeeProfileSalesLedger.salesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.id in ?1")
	List<SalesLedger> findSalesLedgersByUserIdIn(List<Long> userIds);
	
	@Query("select DISTINCT employeeProfileSalesLedger.salesLedger.id from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.id in ?1")
	Set<Long> findSalesLedgerIdsByUserIdIn(List<Long> userIds);
	
	@Query("select DISTINCT employeeProfileSalesLedger.salesLedger.id from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.user.pid in ?1")
	Set<Long> findSalesLedgerIdsByUserPidIn(List<String> userPids);
	
	@Query("select DISTINCT employeeProfileSalesLedger.salesLedger.id from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile.pid in ?1")
	Set<Long> findSalesLedgerIdsByEmployeePidIn(List<String> employeePids);
	
	@Query("select employeeProfileSalesLedger.employeeProfile.user.id from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.salesLedger.pid = ?1")
	Set<Long> findUserIdBySalesLedgerPid(String salesLedgerPid);
	
	@Query("select employeeProfileSalesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.salesLedger.pid IN ?1")
	List<EmployeeProfileSalesLedger> findAllEmployeeBySalesLedgerPids(List<String> salesLedgerPids);
	
	//Edit Mode (8-2-19)
	@Query("select employeeProfileSalesLedger.employeeProfile.pid from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.employeeProfile in ?1")
	List<String> findEmployeeProfilePidByEmployeeProfileIn(List<EmployeeProfile> employees);

	@Query("select employeeProfileSalesLedger from EmployeeProfileSalesLedger employeeProfileSalesLedger where employeeProfileSalesLedger.salesLedger.pid in ?1")
	List<EmployeeProfileSalesLedger> findBySalesLedgerPidIn(List<String> salesLedgerPids);
}
