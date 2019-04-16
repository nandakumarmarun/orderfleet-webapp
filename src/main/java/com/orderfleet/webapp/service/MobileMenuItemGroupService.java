package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemGroupDTO;

/**
 * Service Interface for managing MobileMenuItemGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
public interface MobileMenuItemGroupService {

	String PID_PREFIX = "MMIG-";

	/**
	 * Save a mobileMenuItemGroup.
	 * 
	 * @param mobileMenuItemGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	MobileMenuItemGroupDTO save(MobileMenuItemGroupDTO mobileMenuItemGroupDTO);

	void save(String groupPid, List<MobileMenuItemDTO> menuItems);

	/**
	 * Update a mobileMenuItemGroup.
	 * 
	 * @param mobileMenuItemGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	MobileMenuItemGroupDTO update(MobileMenuItemGroupDTO mobileMenuItemGroupDTO);

	/**
	 * Get all the mobileMenuItemGroups.
	 * 
	 * @return the list of entities
	 */
	List<MobileMenuItemGroupDTO> findAll();

	/**
	 * Get the mobileMenuItemGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<MobileMenuItemGroupDTO> findOneByPid(String pid);

	/**
	 * Get the mobileMenuItemGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<MobileMenuItemGroupDTO> findByName(String name);

	/**
	 * Delete the "id" mobileMenuItemGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
}
