package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;

/**
 * Service Interface for managing Attendance.
 * 
 * @author Sarath
 * @since Aug 18, 2016
 */
public interface AttendanceService {

	String PID_PREFIX = "ATNTCE-";

	/**
	 * Save a attendance.
	 * 
	 * @param attendanceDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	AttendanceDTO save(AttendanceDTO attendanceDTO);

	Attendance saveAttendance(AttendanceDTO attendanceDTO);

	/**
	 * Update a attendance.
	 * 
	 * @param attendanceDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	Attendance update(Long subgroupId);

	/**
	 * Get all the attendances.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Attendance> findAll(Pageable pageable);

	/**
	 * Get all the attendances.
	 * 
	 * @return the list of entities
	 */
	List<AttendanceDTO> findAllByCompany();

	/**
	 * Get all the attendances.
	 * 
	 * @return the list of entities
	 */
	List<AttendanceDTO> findByUserIsCurrentUser();

	/**
	 * Get all the attendances of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AttendanceDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" attendance.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	AttendanceDTO findOne(Long id);

	/**
	 * Get the attendance by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<AttendanceDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" attendance.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<AttendanceDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<AttendanceDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	Long countByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<AttendanceDTO> findAllByCompanyIdUserPidInAndDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<UserDTO> findAllUniqueUsersFromAttendance(String companypid);

	List<UserDTO> getCountUniqueUsersFromAttendanceAndCreateDateBetween(String companypid, LocalDateTime startDate,
			LocalDateTime endDate);
	Optional<AttendanceDTO> findTop1(Long companyId,String userPid);
	
}