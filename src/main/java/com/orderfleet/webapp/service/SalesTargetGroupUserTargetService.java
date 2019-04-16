package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.web.rest.dto.SalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;

public interface SalesTargetGroupUserTargetService {

	String PID_PREFIX = "STGUT-";

	/**
	 * Save a salesTargetGroupUserTarget.
	 * 
	 * @param salesTargetGroupUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	SalesTargetGroupUserTargetDTO save(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO);

	/**
	 * Update a salesTargetGroupUserTarget.
	 * 
	 * @param salesTargetGroupUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	SalesTargetGroupUserTargetDTO update(SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO);

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<SalesTargetGroupUserTarget> findAll(Pageable pageable);

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	List<SalesTargetGroupUserTargetDTO> findAllByCompany();

	/**
	 * Get all the activities of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<SalesTargetGroupUserTargetDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" salesTargetGroupUserTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	SalesTargetGroupUserTargetDTO findOne(Long id);

	/**
	 * Get the salesTargetGroupUserTarget by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<SalesTargetGroupUserTargetDTO> findOneByPid(String pid);

	/**
	 * Get the "date" SalesTargetGroupUserTarget List.
	 * 
	 * @param date
	 *            the date of the entity
	 * @return the SalesTargetGroupUserTargetDTO List
	 */
	List<SalesTargetGroupUserTargetDTO> findByCurrentUserAndCurrentMonth();

	/**
	 * Delete the "id" salesTargetGroupUserTarget.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<SalesTargetGroupUserTarget> findUserAndSalesTargetGroupPidAndDateWiseDuplicate(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate);

	SalesTargetGroupUserTargetDTO saveMonthlyTarget(SalesMonthlyTargetDTO monthlyTargetDTO, LocalDate firstDateMonth,
			LocalDate lastDateMonth);

	SalesTargetGroupUserTargetDTO saveUpdateDaylyTarget(
			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs);

	List<SalesTargetGroupUserTargetDTO> findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(String userPid,
			String salesTargetGroupPid, LocalDate startDate, LocalDate endDate, TargetFrequency targetFrequency);

	List<SalesTargetGroupUserTargetDTO> findAllByCompanyIdAndTargetFrequencyAndDateBetween(
			TargetFrequency targetFrequency, LocalDate startDate, LocalDate endDate);
}
