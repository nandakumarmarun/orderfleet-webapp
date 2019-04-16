package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingSalesTargetBlockDTO;

/**
 * Service Interface for managing .
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
public interface SalesTargetReportSettingService {

	String PID_PREFIX = "STRSTING-";

	/**
	 * Save a salesTargetReportSetting.
	 * 
	 * @param salesTargetReportSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	SalesTargetReportSettingDTO save(SalesTargetReportSettingDTO salesTargetReportSettingDTO);

	/**
	 * Update a salesTargetReportSetting.
	 * 
	 * @param salesTargetReportSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	SalesTargetReportSettingDTO update(SalesTargetReportSettingDTO salesTargetReportSettingDTO);

	/**
	 * Get all the salesTargetReportSettings.
	 * 
	 * @return the list of entities
	 */
	List<SalesTargetReportSettingDTO> findAllByCompany();

	/**
	 * Get the salesTargetReportSetting by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<SalesTargetReportSettingDTO> findOneByPid(String pid);

	/**
	 * Get the salesTargetReportSettingDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<SalesTargetReportSettingDTO> findByName(String name);

	/**
	 * Delete the "id" salesTargetReportSetting.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	void saveAssignedTargetBlocks(String pid, List<SalesTargetReportSettingSalesTargetBlockDTO> assignedTargetBlocks);

	List<SalesTargetReportSettingDTO> findAllByCompanyIdAndTargetSettingType(BestPerformanceType targetSettingType);

}
