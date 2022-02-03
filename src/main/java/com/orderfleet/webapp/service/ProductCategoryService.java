package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.SetDiscountPercentage;

/**
 * Service Interface for managing ProductCategory.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public interface ProductCategoryService {

	String PID_PREFIX = "PCAT-";

	/**
	 * Save a productCategory.
	 * 
	 * @param productCategoryDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ProductCategoryDTO save(ProductCategoryDTO productCategoryDTO);

	/**
	 * Update a productCategory.
	 * 
	 * @param productCategoryDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ProductCategoryDTO update(ProductCategoryDTO productCategoryDTO);

	/**
	 * Get all the productCategorys.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductCategory> findAll(Pageable pageable);

	/**
	 * Get all the product categories.
	 * 
	 * @return the list of entities
	 */
	List<ProductCategoryDTO> findAllByCompany();

	/**
	 * Get all the productCategories of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductCategoryDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" productCategory.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ProductCategoryDTO findOne(Long id);

	/**
	 * Get the productCategory by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ProductCategoryDTO> findOneByPid(String pid);

	/**
	 * Get the productCategoryDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ProductCategoryDTO> findByName(String name);

	/**
	 * Delete the "id" productCategory.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Update the productCategory status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activated
	 *            the activated of the entity
	 * @return the entity
	 */
	ProductCategoryDTO updateProductCategoryStatus(String pid, boolean activated);

	/**
	 * @author Fahad
	 * @since Feb 11, 2017
	 * 
	 *        find all active company
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<ProductCategoryDTO> findAllByCompanyIdAndActivatedOrderByProductCategoryName(Pageable pageable,
			boolean active);

	/**
	 * @author Fahad
	 * @since Feb 11, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<ProductCategoryDTO> findAllByCompanyAndDeactivated(boolean deactive);
	
	void saveDicountPercentage(SetDiscountPercentage setDiscountPercentage);

	Optional<ProductCategoryDTO> findByNameAndCompanyId(Long companyId, String name);

	ProductCategoryDTO saveProductCategory(Long companyId, ProductCategoryDTO productCategoryDTO);

	ProductCategoryDTO updateProductCategoryThirdpartyUpdateStatus(String pid, boolean thirdpartyUpdate);
	
	List<ProductCategoryDTO>findAllByCompanyPid(String companyPid);
}
