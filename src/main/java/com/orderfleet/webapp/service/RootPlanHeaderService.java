package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.web.rest.api.dto.RootPlanHeaderUserTaskListDTO;
import com.orderfleet.webapp.web.rest.dto.RootPlanHeaderDTO;

public interface RootPlanHeaderService {

	String PID_PREFIX = "RPHR-";

	/**
	 * Save a rootPlanHeader.
	 * 
	 * @param rootPlanHeaderDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	RootPlanHeaderDTO save(RootPlanHeaderDTO rootPlanHeaderDTO);

	/**
	 * Update a rootPlanHeader.
	 * 
	 * @param rootPlanHeaderDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	RootPlanHeaderDTO update(RootPlanHeaderDTO rootPlanHeaderDTO);

	/**
	 * Get all the rootPlanHeaders.
	 * 
	 * @return the list of entities
	 */
	List<RootPlanHeaderDTO> findAllByCompany();

	/**
	 * Get all the rootPlanHeaders.
	 * 
	 * @return the list of entities
	 */
	List<RootPlanHeaderDTO> findAllByUser();

	/**
	 * Get the rootPlanHeaderDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<RootPlanHeaderDTO> findOneByPid(String pid);

	/**
	 * Get the rootPlanHeaderDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<RootPlanHeaderDTO> findByName(String name);

	/**
	 * Delete the "id" rootPlanHeader.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<RootPlanHeaderUserTaskListDTO> findAllDetailsByUserLogin();

	RootPlanDetail findTaskListFromDetailByUserLoginAndStatusApproved();

	/**
	 * Get all the rootPlanHeaders.
	 * 
	 * @return the list of entities
	 */
	List<RootPlanHeaderDTO> findAllByUserPid(String pid);

	List<RootPlanHeaderDTO> findAllByDateGreaterFromDateAndLessThanToDate(LocalDate fromDate, LocalDate toDate);

	List<RootPlanHeaderDTO> findAllByUserPidAndDateGreaterFromDateAndLessThanToDate(String userPid, LocalDate fromDate,
			LocalDate toDate);

	List<RootPlanHeaderDTO> findAllByActivated(boolean active);

	List<RootPlanHeaderDTO> findAllByUserPidAndActivated(String userPid, boolean active);

	List<RootPlanHeaderDTO> findAllByUserIdsInAndActivated(List<Long> userIds,boolean activated);

	List<RootPlanHeaderDTO> findAllByUserIdsInAndCompany(List<Long> userIds);

}
