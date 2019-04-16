package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;

/**
 * Spring Data JPA repository for the DocumentProductCategory entity.
 * 
 * @author Sarath
 * @since July 08 2016
 */
public interface DocumentProductCategoryService {

	void save(String documentPid, String assignedProductCategories);

	List<ProductCategoryDTO> findProductCategoriesByDocumentPid(String documentPid);

	List<DocumentProductCategoryDTO> findByUserDocumentIsCurrentUser();

	List<DocumentProductCategoryDTO> findByUserDocumentIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);

}
