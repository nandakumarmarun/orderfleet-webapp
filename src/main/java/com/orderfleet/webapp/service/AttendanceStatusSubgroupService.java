package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;

/**
 *Service  for AttendanceStatusSubgroup
 *
 * @author fahad
 * @since Jul 25, 2017
 */
public interface AttendanceStatusSubgroupService {

	String PID_PREFIX = "ASSP-";

	/**
	 * Save a attendanceStatusSubgroup.
	 * 
	 * @param attendanceStatusSubgroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	AttendanceStatusSubgroupDTO save(AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO);

	/**
	 * Update a attendanceStatusSubgroup.
	 * 
	 * @param attendanceStatusSubgroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	AttendanceStatusSubgroupDTO update(AttendanceStatusSubgroupDTO attendanceStatusSubgroupDTO);

	/**
	 * Get all the attendanceStatusSubgroups.
	 * 
	 * @return the list of entities
	 */
	List<AttendanceStatusSubgroupDTO> findAllByCompany();

	/**
	 * Get the "id" attendanceStatusSubgroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	AttendanceStatusSubgroupDTO findOne(Long id);

	/**
	 * Get the attendanceStatusSubgroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<AttendanceStatusSubgroupDTO> findByName(String name);
	
	/**
	 * Delete the "id" attendanceStatusSubgroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long pid);
}
