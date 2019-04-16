package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.FormElementMasterTypeDTO;

/**
 * Service Interface for managing FormElementMasterType.
 *
 * @author Sarath
 * @since Nov 2, 2016
 */
public interface FormElementMasterTypeService {

	String PID_PREFIX = "FRELMSTR-";

	/**
	 * Save a formElementMasterType.
	 * 
	 * @param formElementMasterTypeDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	FormElementMasterTypeDTO save(FormElementMasterTypeDTO formElementMasterTypeDTO);

	/**
	 * Update a formElementMasterType.
	 * 
	 * @param formElementMasterTypeDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	FormElementMasterTypeDTO update(FormElementMasterTypeDTO formElementMasterTypeDTO);

	/**
	 * Get all the formElementMasterTypes.
	 * 
	 * @return the list of entities
	 */
	List<FormElementMasterTypeDTO> findAllByCompany();

	/**
	 * Get all the formElementMasterTypes of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<FormElementMasterTypeDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" formElementMasterType.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	FormElementMasterTypeDTO findOne(Long id);

	/**
	 * Get the formElementMasterType by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FormElementMasterTypeDTO> findOneByPid(String pid);

	/**
	 * Get the formElementMasterTypeDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<FormElementMasterTypeDTO> findByName(String name);

	/**
	 * Delete the "id" formElementMasterType.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	/**
	 * Update  the FormElementMasterType status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	FormElementMasterTypeDTO updateFormElementMasterTypeStatus(String pid ,boolean active);
	
	/**
	 *  @author Fahad
	 * @since Feb 14, 2017
	 * 
	 * find all active company 
	 * 
	 *@param active
	 *            the active of the entity
	 *            
	 *@param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
    Page<FormElementMasterTypeDTO> findAllByCompanyAndActivatedFormElementMasterTypeOrderByName(Pageable pageable,boolean active);
	
	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<FormElementMasterTypeDTO> findAllByCompanyAndDeactivatedFormElementMasterType(boolean deactive);
}
