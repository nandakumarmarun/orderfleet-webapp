package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;


import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingGroupDTO;

public interface InventoryClosingReportSettingGroupService {
	String PID_PREFIX = "INCRSGP-";

	/**
	 * Save a inventoryClosingReportSettingGroup.
	 * 
	 * @param inventoryClosingReportSettingGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	InventoryClosingReportSettingGroupDTO save(InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO);

	/**
	 * Update a inventoryClosingReportSettingGroup.
	 * 
	 * @param inventoryClosingReportSettingGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	InventoryClosingReportSettingGroupDTO update(InventoryClosingReportSettingGroupDTO inventoryClosingReportSettingGroupDTO);

	/**
	 * Get all the inventoryClosingReportSettingGroups.
	 * 
	 * @return the list of entities
	 */
	List<InventoryClosingReportSettingGroupDTO> findAllByCompany();
	
	/**
	 * Get the "id" inventoryClosingReportSettingGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	InventoryClosingReportSettingGroupDTO findOne(Long id);

	/**
	 * Get the inventoryClosingReportSettingGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<InventoryClosingReportSettingGroupDTO> findOneByPid(String pid);

	/**
	 * Get the inventoryClosingReportSettingGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<InventoryClosingReportSettingGroupDTO> findByName(String name);

	/**
	 * Delete the "id" inventoryClosingReportSettingGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * 
	 * Update the InventoryClosingReportSettingGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	InventoryClosingReportSettingGroupDTO updateInventoryClosingReportSettingGroupStatus(String pid, boolean activate);


	/**
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<InventoryClosingReportSettingGroupDTO> findAllByCompanyAndDeactivatedInventoryClosingReportSettingGroup(boolean deactive);
}
