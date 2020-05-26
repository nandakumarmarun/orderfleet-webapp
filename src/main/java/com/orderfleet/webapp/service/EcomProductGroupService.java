package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;

/**
 * Service Interface for managing ProductGroup.
 * 
 * @author Anish
 * @since May 17, 2020
 */
public interface EcomProductGroupService {

	String PID_PREFIX = "PGP-";

	/**
	 * Save a productGroup.
	 * 
	 * @param productGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	EcomProductGroupDTO save(EcomProductGroupDTO ecomProductGroupDTO);

	/**
	 * Update a productGroup.
	 * 
	 * @param productGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	EcomProductGroupDTO update(EcomProductGroupDTO ecomProductGroupDTO);

	/**
	 * Get all the productGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<EcomProductGroup> findAll(Pageable pageable);

	/**
	 * Get all the productGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<EcomProductGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" productGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	EcomProductGroupDTO findOne(Long id);

	/**
	 * Get the productGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<EcomProductGroupDTO> findOneByPid(String pid);

	/**
	 * Get the productGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<EcomProductGroupDTO> findByName(String name);

	/**
	 * Delete the "id" productGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Get all the productGroup of a company.
	 * 
	 * @return the list of entities
	 */
	List<EcomProductGroupDTO> findAllByCompany();

	void saveTaxRate(SetTaxRate setTaxRate);
	
	void saveUnitQuantity(SetTaxRate setUnitQty);

	/**
	 * Update the productGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	EcomProductGroupDTO updateProductGroupStatus(String pid, boolean active);

	List<EcomProductGroupDTO> findAllByCompanyOrderByName();

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
	Page<EcomProductGroupDTO> findAllByCompanyAndActivatedProductGroupOrderByName(Pageable pageable, boolean active);

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
	List<EcomProductGroupDTO> findAllByCompanyAndDeactivatedProductGroup(boolean deactive);

	Optional<EcomProductGroupDTO> findByCompanyIdAndName(Long companyId, String name);

	EcomProductGroupDTO saveProductGroup(Long companyId, EcomProductGroupDTO productGroupDTO);
	
	EcomProductGroupDTO updateProductGroupThirdpartyUpdate(String pid, boolean Thirdparty);
	
	void saveTaxMaster(List<String> taxmasterPids,List<String> productGroupPids);
	
	List<EcomProductGroupDTO> findAllProductGroupByCompanyOrderByName();
	
	List<EcomProductGroupDTO> findAllProductGroupByCompanyPid(String companyPid);
	
	List<EcomProductGroupDTO> findAllProductGroupByCompanyPidAndGroupPid(String companyPid,List<String>groupPids);
}
