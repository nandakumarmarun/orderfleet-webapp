package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.FormElementMasterDTO;

/**
 * Service Interface for managing FormElementMaster.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public interface FormElementMasterService {

	String PID_PREFIX = "FREM-";

	/**
	 * Get all the formElementMasters.
	 * 
	 * @return the list of entities
	 */
	List<FormElementMasterDTO> findAllByCompany();

	/**
	 * Get the formElementMaster by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FormElementMasterDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" formElementMaster.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Get the formElementMaster by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FormElementMasterDTO> findOneByName(String name);

	FormElementMasterDTO save(FormElementMasterDTO formElementMasterDTO);

}
