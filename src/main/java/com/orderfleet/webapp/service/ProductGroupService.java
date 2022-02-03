package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.SetDiscountPercentage;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;

/**
 * Service Interface for managing ProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public interface ProductGroupService {

	String PID_PREFIX = "PGP-";

	/**
	 * Save a productGroup.
	 * 
	 * @param productGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ProductGroupDTO save(ProductGroupDTO productGroupDTO);

	/**
	 * Update a productGroup.
	 * 
	 * @param productGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ProductGroupDTO update(ProductGroupDTO productGroupDTO);

	/**
	 * Get all the productGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductGroup> findAll(Pageable pageable);

	/**
	 * Get all the productGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" productGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ProductGroupDTO findOne(Long id);

	/**
	 * Get the productGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ProductGroupDTO> findOneByPid(String pid);

	/**
	 * Get the productGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ProductGroupDTO> findByName(String name);

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
	List<ProductGroupDTO> findAllByCompany();

	void saveTaxRate(SetTaxRate setTaxRate);
	
	void saveUnitQuantity(SetTaxRate setUnitQty);
	
	void saveDicountPercentage(SetDiscountPercentage setDiscountPercentage);

	/**
	 * Update the productGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	ProductGroupDTO updateProductGroupStatus(String pid, boolean active);

	List<ProductGroupDTO> findAllByCompanyOrderByName();

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
	Page<ProductGroupDTO> findAllByCompanyAndActivatedProductGroupOrderByName(Pageable pageable, boolean active);

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
	List<ProductGroupDTO> findAllByCompanyAndDeactivatedProductGroup(boolean deactive);

	Optional<ProductGroupDTO> findByCompanyIdAndName(Long companyId, String name);

	ProductGroupDTO saveProductGroup(Long companyId, ProductGroupDTO productGroupDTO);
	
	ProductGroupDTO updateProductGroupThirdpartyUpdate(String pid, boolean Thirdparty);
	
	void saveTaxMaster(List<String> taxmasterPids,List<String> productGroupPids);
	
	List<ProductGroupDTO> findAllProductGroupByCompanyOrderByName();
	
	List<ProductGroupDTO> findAllProductGroupByCompanyPid(String companyPid);
	
	List<ProductGroupDTO> findAllProductGroupByCompanyPidAndGroupPid(String companyPid,List<String>groupPids);

	
}
