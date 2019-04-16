package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.DayPlanPages;
import com.orderfleet.webapp.web.rest.dto.DayPlanExecutionConfigDTO;

/**
 * Service Interface for managing DayPlanExecutionConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
public interface DayPlanExecutionConfigService {

	/**
	 * Save a dayPlanExecutionConfigs.
	 * 
	 * @param dayPlanExecutionConfigDTOs
	 *            the entity to save
	 */
	void save(List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigDTOs);

	void saveAssignedUsers(Long id, String assignedUsers);

	/**
	 * Get all the dayPlanExecutionConfigs.
	 * 
	 * @return the list of entities
	 */
	List<DayPlanExecutionConfigDTO> findAllByCompany();

	List<DayPlanExecutionConfigDTO> findAllByCompanyIdAndEnabledTrueCurrentUser();

	List<DayPlanExecutionConfigDTO> findAllByCompanyIdAndEnabledTrue();

	/**
	 * Get the dayPlanExecutionConfigDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DayPlanExecutionConfigDTO> findByName(DayPlanPages name);

	DayPlanExecutionConfigDTO findById(Long id);

}
