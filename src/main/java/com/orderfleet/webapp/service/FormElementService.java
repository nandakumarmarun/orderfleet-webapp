package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;

/**
 * Service Interface for managing FormElement.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
public interface FormElementService {

	String PID_PREFIX = "FRME-";

	/**
	 * Save a formElement.
	 * 
	 * @param formElementDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	FormElementDTO save(FormElementDTO formElementDTO);

	void saveDefaultValue(String pid, String defaultValue);

	/**
	 * Update a formElement.
	 * 
	 * @param formElementDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	FormElementDTO update(FormElementDTO formElementDTO);

	/**
	 * Get all the formElements.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<FormElement> findAll(Pageable pageable);

	/**
	 * Get all the formElements of a company.
	 * 
	 * @return the list of entities
	 */
	List<FormElementDTO> findAllByCompany();

	/**
	 * Get all the formElements of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<FormElementDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" formElement.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	FormElementDTO findOne(Long id);

	/**
	 * Get the formElement by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FormElementDTO> findOneByPid(String pid);

	/**
	 * Get the formElement by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<FormElementDTO> findByName(String name);

	/**
	 * Delete the "id" formElement.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	// /**
	// * Get the "formId" formElement.
	// *
	// * @param formId
	// * the formId of the entity
	// * @return the entity
	// */
	// FormElementDTO findByFormId(Long formId);

	List<FormElementDTO> findUsersFormElement();

	/**
	 * Update the FormElement status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	FormElementDTO updateFormElementStatus(String pid, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	Page<FormElementDTO> findAllByCompanyAndActivatedFormElement(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<FormElementDTO> findAllByCompanyAndDeactivatedFormElement(boolean deactive);
	
	List<FormElementDTO> findAllByCompanyIdAndFormElementPidIn(List<String> formElementPids);
}
