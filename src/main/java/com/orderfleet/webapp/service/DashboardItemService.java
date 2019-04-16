package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.DashboardItemDTO;

/**
 * Service Interface for managing DashboardItem.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
public interface DashboardItemService {

	String PID_PREFIX = "DBI-";

	/**
	 * Save a dashboardItem.
	 * 
	 * @param dashboardItemDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	DashboardItemDTO save(DashboardItemDTO dashboardItemDTO);

	/**
	 * Update a dashboardItem.
	 * 
	 * @param dashboardItemDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	DashboardItemDTO update(DashboardItemDTO dashboardItemDTO);

	/**
	 * Get all the dashboardItems.
	 * 
	 * @return the list of entities
	 */
	List<DashboardItemDTO> findAllByCompany();
	
	List<DashboardItemDTO> findAllByCompanyId(Long companyId);

	/**
	 * Get the "id" dashboardItem.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	DashboardItemDTO findOne(Long id);

	/**
	 * Get the dashboardItem by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<DashboardItemDTO> findOneByPid(String pid);

	/**
	 * Get the dashboardItemDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<DashboardItemDTO> findByName(String name);

	/**
	 * Delete the "id" dashboardItem.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
}
