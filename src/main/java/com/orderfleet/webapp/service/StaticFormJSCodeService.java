package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;

/**
 * Service Interface for managing StaticFormJSCode.
 * 
 * @author Sarath
 * @since Aug 4, 2016
 */
public interface StaticFormJSCodeService {

	/**
	 * Save a staticFormJSCode.
	 * 
	 * @param staticFormJSCodeDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	StaticFormJSCodeDTO save(StaticFormJSCodeDTO staticFormJSCodeDTO);

	void save(String companyPid, String documentPid, String jsCode);

	/**
	 * Update a staticFormJSCode.
	 * 
	 * @param staticFormJSCodeDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	StaticFormJSCodeDTO update(StaticFormJSCodeDTO staticFormJSCodeDTO);

	/**
	 * Get all the staticFormJSCodes.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<StaticFormJSCode> findAll(Pageable pageable);

	/**
	 * Get all the staticFormJSCodes of a company.
	 * 
	 * @return the list of entities
	 */
	List<StaticFormJSCodeDTO> findAllByCompany();

	/**
	 * Get all the staticFormJSCodes of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<StaticFormJSCodeDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" staticFormJSCode.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	StaticFormJSCodeDTO findOne(Long id);

	/**
	 * Get the staticFormJSCode by "id".
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	Optional<StaticFormJSCodeDTO> findOneById(Long id);

	/**
	 * Delete the "id" staticFormJSCode.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);

	StaticFormJSCodeDTO findByName(String formulaName);
}
