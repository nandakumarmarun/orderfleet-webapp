package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsColumnsDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsRowColourDTO;

/**
 * Service Interface for managing DashboardActivity.
 *
 * @author Sarath
 * @since Aug 29, 2017
 *
 */
public interface DynamicDocumentSettingsHeaderService {

	String PID_PREFIX = "DYDOCSTNGHD-";

	/**
	 * Save a dynamicDocumentSettingsHeader.
	 * 
	 * @param documentSettingsHeaderDTO
	 * @return
	 */
	DynamicDocumentSettingsHeaderDTO save(DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO);

	/**
	 * Get all the dynamicDocumentSettingsHeader.
	 * 
	 * @return the list of entities
	 */
	List<DynamicDocumentSettingsHeaderDTO> findAllByCompanyId();

	/**
	 * Get one the dynamicDocumentSettingsHeader.
	 * 
	 * @return the list of entities
	 */
	Optional<DynamicDocumentSettingsHeaderDTO> findOneByPid(String pid);

	DynamicDocumentSettingsHeaderDTO update(DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO);

	void delete(String pid);

	void updateDynamicDocumentSettingsColumns(
			List<DynamicDocumentSettingsColumnsDTO> dynamicDocumentSettingsColumnsDTOs);

	void saveDynamicDocumentSettingsRowColour(
			List<DynamicDocumentSettingsRowColourDTO> dynamicDocumentSettingsRowColourDTOs);

	Optional<DynamicDocumentSettingsHeaderDTO> findOneByName(String name);

	DynamicDocumentSettingsHeaderDTO updateDynamicDocumentSettingsHeader(
			DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO);
}
