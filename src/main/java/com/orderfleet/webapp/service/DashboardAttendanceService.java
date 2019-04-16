package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.DashboardAttendanceDTO;

public interface DashboardAttendanceService {

	String PID_PREFIX = "DBA-";
	
	
	/**
	 * Save a dashboardAttendance.
	 * 
	 * @param dashboardAttendanceDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DashboardAttendanceDTO save(DashboardAttendanceDTO dashboardAttendanceDTO);

	/**
	 * Update a dashboardAttendance.
	 * 
	 * @param dashboardAttendanceDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DashboardAttendanceDTO update(DashboardAttendanceDTO dashboardAttendanceDTO);

	/**
	 * Get all the dashboardAttendances.
	 * 
	 * @return the list of entities
	 */
	List<DashboardAttendanceDTO> findAllByCompany();
	
	/**
	 * Get the "id" dashboardAttendance.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DashboardAttendanceDTO findOne(Long id);

	/**
	 * Get the dashboardAttendanceDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DashboardAttendanceDTO> findByName(String name);

	/**
	 * Delete the "id" dashboardAttendance.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);
	
}
