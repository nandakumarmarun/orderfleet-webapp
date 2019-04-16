package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;

/**
 * Service Interface for managing UserProductCategory.
 * 
 * @author Sarath
 * @since July 8 2016
 */
public interface UserProductCategoryService {

	/**
	 * Save a UserProductCategory.
	 * 
	 * @param userPid
	 * @param assignedProductCategory
	 */
	void save(String userPid, String assignedProductCategory);

	List<ProductCategoryDTO> findProductCategoriesByUserIsCurrentUser();

	List<ProductCategoryDTO> findProductCategoriesByUserPid(String userPid);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all productCategoryDTO from ProductCategory by status and user.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	List<ProductCategoryDTO> findProductCategoriesByUserIsCurrentUserAndProductCategoryActivated(boolean active);
	
	List<ProductCategoryDTO> findByUserIsCurrentUserAndProductCategoryActivatedAndLastModifiedDate(boolean active,LocalDateTime lastModifiedDate);

	void copyProductCategories(String fromUserPid, List<String> toUserPids);
}
