package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Location;;

/**
 * Spring Data JPA repository for the EmployeeProfileLocation entity.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
public interface EmployeeProfileLocationRepository extends JpaRepository<EmployeeProfileLocation, Long> {

	@Query("select employeeProfileLocation.location from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.login = ?#{principal.username}")
	List<Location> findLocationsByEmployeeProfileIsCurrentUser();
	
	@Query("select employeeProfileLocation.location.id from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.login = ?#{principal.username}")
	Set<Long> findLocationIdsByEmployeeProfileIsCurrentUser();

	@Query("select employeeProfileLocation.location from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.pid = ?1 ")
	List<Location> findLocationsByEmployeeProfilePid(String employeeProfilePid);

	void deleteByEmployeeProfilePid(String employeeProfilePid);

	@Query("select employeeProfileLocation.location from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.pid = ?1")
	List<Location> findLocationsByUserPid(String userPid);

	List<EmployeeProfileLocation> findByLocationCompanyPid(String companyPid);
	
	EmployeeProfileLocation findTop1ByEmployeeProfilePid(String employeeProfilePid);

	@Query("select employeeProfileLocation.location from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.login = ?#{principal.username} and employeeProfileLocation.location.lastModifiedDate > ?1")
	List<Location> findLocationsByEmployeeProfileIsCurrentUserAndlastModifiedDate(LocalDateTime lastModifiedDate);
	
	@Query("select employeeProfileLocation.employeeProfile.user.id from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.location.id in ?1")
	Set<Long> findEmployeeUserIdsByLocationIdIn(List<Long> locationIds);

	@Query("select DISTINCT employeeProfileLocation.location from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.pid in ?1")
	List<Location> findLocationsByUserPidIn(List<String> userPids);
	
	@Query("select DISTINCT employeeProfileLocation.location from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.id in ?1")
	List<Location> findLocationsByUserIdIn(List<Long> userIds);
	
	@Query("select DISTINCT employeeProfileLocation.location.id from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.id in ?1")
	Set<Long> findLocationIdsByUserIdIn(List<Long> userIds);
	
	@Query("select DISTINCT employeeProfileLocation.location.id from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.user.pid in ?1")
	Set<Long> findLocationIdsByUserPidIn(List<String> userPids);
	
	@Query("select DISTINCT employeeProfileLocation.location.id from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile.pid in ?1")
	Set<Long> findLocationIdsByEmployeePidIn(List<String> employeePids);
	
	@Query("select employeeProfileLocation.employeeProfile.user.id from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.location.pid = ?1")
	Set<Long> findUserIdByLocationPid(String locationPid);
	
	@Query("select employeeProfileLocation from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.location.pid IN ?1")
	List<EmployeeProfileLocation> findAllEmployeeByLocationPids(List<String> locationPids);
	
	//Edit Mode (8-2-19)
	@Query("select employeeProfileLocation.employeeProfile.pid from EmployeeProfileLocation employeeProfileLocation where employeeProfileLocation.employeeProfile in ?1")
	List<String> findEmployeeProfilePidByEmployeeProfileIn(List<EmployeeProfile> employees);
}
