package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.EcomProductGroupProduct;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Spring Data JPA repository for the ProductGroupProduct entity.
 *
 * @author Anish
 * @since June 10, 2020
 */
public interface EcomProductGroupProductService {

	void save(String productGroupPid, String assignedProducts);

	List<ProductProfileDTO> findProductByProductGroupPid(String productGroupPid);

	List<ProductProfileDTO> findByProductGroupPids(List<String> groupPids);

	List<ProductProfileDTO> findByProductGroupPidsAndCategoryPids(List<String> groupPids, List<String> categoryPids);

	List<EcomProductGroupProduct> findAllByCompany();

	public List<EcomProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUser();

	List<ProductProfileDTO> findAllByProductProfilePid(String productProfilePid);


	List<ProductProfileDTO> findByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(List<String> groupPids,
			List<String> categoryPids);

	List<ProductProfileDTO> findByProductGroupPidsAndActivated(List<String> groupPids);

	List<EcomProductGroupDTO> findProductGroupByProductPid(String productPid);


	List<EcomProductGroupProduct> findAllByCompanyAndActivated(boolean active);

	List<EcomProductGroupProduct> findProductGroupProductByProductCategoryPidsAndActivated(List<String> categoryPids);


	List<EcomProductGroupProduct> findProductGroupProductByProductGroupPidsAndActivated(List<String> groupPids);

	List<EcomProductGroupProduct> findProductGroupProductByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids);

	List<EcomProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(
			boolean activted);

	List<EcomProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivatedAndModifiedDate(
			boolean activted, LocalDateTime lastModifiedDate);

	List<ProductProfileDTO> findProductByProductGroupPidAndActivatedAndStockAvailabilityStatus(String productGroupPid,
			boolean activated, StockAvailabilityStatus status);

	List<ProductProfileDTO> findProductByActivatedAndStockAvailabilityStatus(boolean activated,
			StockAvailabilityStatus status);

	List<ProductProfileDTO> findEcomProductProfileByProductGroupPid(String productGroupPid);
	
	List<String> findProductByProductProfilePidAndProductGroupPidAndProductCategoryPidAndActivated(List<String> productProfilePids, List<String> productGroupPids, List<String> productCategoryPids);
	
	List<String> findProductByProductProfilePidAndProductCategoryPidAndActivated(List<String> productProfilePids, List<String> productCategoryPids);
	
	List<String> findProductByProductProfilePidAndProductGroupPidAndActivated(List<String> productProfilePids, List<String> productGroupPids);
	
}
