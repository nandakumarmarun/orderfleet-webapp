package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementOrderDTO;

/**
 * Service Interface for managing Form.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
public interface FormService {

	String PID_PREFIX = "FORM-";

	/**
	 * Save a form.
	 * 
	 * @param formDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	FormDTO save(FormDTO formDTO);

	void saveAssignedQuestions(FormFormElementOrderDTO formFormElementDTO);

	/**
	 * Update a form.
	 * 
	 * @param formDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	FormDTO update(FormDTO formDTO);

	/**
	 * Get all the forms.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<Form> findAll(Pageable pageable);

	/**
	 * Get all the forms of a company.
	 * 
	 * @return the list of entities
	 */
	List<FormDTO> findAllByCompany();

	/**
	 * Get all the forms of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<FormDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" form.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	FormDTO findOne(Long id);

	/**
	 * Get the form by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FormDTO> findOneByPid(String pid);

	/**
	 * Get the form by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<FormDTO> findByName(String name);

	/**
	 * Delete the "id" form.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Update the Form status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	FormDTO updateFormStatus(String pid, boolean active);

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
	Page<FormDTO> findAllByCompanyAndActivatedFormOrderByName(Pageable pageable, boolean active);

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
	List<FormDTO> findAllByCompanyAndDeactivatedForm(boolean deactive);

	List<FormDTO> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
}
