package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.AccountNameTextSettingsDTO;
import com.orderfleet.webapp.web.rest.dto.ProductNameTextSettingsDTO;

/**
 * Service Interface for managing ProductNameTextSettings.
 * 
 * @author Muhammed Riyas T
 * @since Dec 29, 2016
 */
public interface AccountNameTextSettingsService {

	String PID_PREFIX = "ANTS-";

	/**
	 * Save a productNameTextSettings.
	 * 
	 * @param productNameTextSettingsDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(List<AccountNameTextSettingsDTO> accountNameTextSettingsDTOs);

	/**
	 * Get all the productNameTextSettingss of a company.
	 * 
	 * @return the list of entities
	 */
	List<AccountNameTextSettingsDTO> findAllByCompany();

	/**
	 * Get the productNameTextSettings by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<AccountNameTextSettingsDTO> findOneByPid(String pid);

	/**
	 * Get the productNameTextSettingsDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<AccountNameTextSettingsDTO> findByName(String name);

	void saveDefault(List<AccountNameTextSettingsDTO> textSettingsDTOs);

}
