package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Service Interface for managing EmployeeProfileLocation.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */
public interface EmployeeProfileLocationService {

	/**
	 * Save a EmployeeProfileLocation.
	 * 
	 * @param employeeProfilePid
	 * @param assignedLocations
	 */
	void save(String employeeProfilePid, String assignedLocations);

	List<LocationDTO> findLocationsByEmployeeProfileIsCurrentUser();

	List<LocationDTO> findLocationsByEmployeeProfilePid(String employeeProfilePid);

	List<LocationDTO> findLocationsByEmployeeProfileIsCurrentUserAndlastModifiedDate(LocalDateTime lastModifiedDate);

	List<LocationDTO> findLocationsByUserPid(String userPid);
	
	List<LocationDTO> findLocationsByUserPidIn(List<String>UserPids);

}
