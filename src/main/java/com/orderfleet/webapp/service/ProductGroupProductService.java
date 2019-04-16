package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Spring Data JPA repository for the ProductGroupProduct entity.
 *
 * @author Sarath
 * @since Aug 9, 2016
 */
public interface ProductGroupProductService {

	void save(String productGroupPid, String assignedProducts);

	List<ProductProfileDTO> findProductByProductGroupPid(String productGroupPid);

	List<ProductProfileDTO> findByProductGroupPids(List<String> groupPids);

	List<ProductProfileDTO> findByProductGroupPidsAndCategoryPids(List<String> groupPids, List<String> categoryPids);

	List<ProductGroupProduct> findAllByCompany();

	public List<ProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUser();

	List<ProductProfileDTO> findAllByProductProfilePid(String productProfilePid);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by ProductGroupPids, CategoryPids And
	 *        ActivatedProductProfile.
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @param groupPids
	 *            the groupPids of the entity
	 * @return the list
	 */
	List<ProductProfileDTO> findByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(List<String> groupPids,
			List<String> categoryPids);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by ProductGroupPids And Activated
	 * @param groupPids
	 *            the groupPids of the entity
	 * @return the list
	 */
	List<ProductProfileDTO> findByProductGroupPidsAndActivated(List<String> groupPids);

	List<ProductGroupDTO> findProductGroupByProductPid(String productPid);

	/**
	 * @author Fahad
	 * @since Feb 28, 2017
	 *
	 *        find ProductGroupProduct by CompanyId And Activated
	 * @param active
	 *            the active of the entity
	 * @return the list of ProductGroupProduct
	 */
	List<ProductGroupProduct> findAllByCompanyAndActivated(boolean active);

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find ProductGroupProduct by ProductCategoryPids
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @return the list
	 */
	List<ProductGroupProduct> findProductGroupProductByProductCategoryPidsAndActivated(List<String> categoryPids);

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find ProductGroupProduct by ProductGroupPids
	 * @param groupPids
	 *            the groupPids of the entity
	 * @return the list
	 */
	List<ProductGroupProduct> findProductGroupProductByProductGroupPidsAndActivated(List<String> groupPids);

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find ProductGroupProduct by ProductGroupPids And CategoryPids
	 * @param groupPids
	 *            the groupPids of the entity
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @return the list
	 */
	List<ProductGroupProduct> findProductGroupProductByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids);

	List<ProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(
			boolean activted);

	List<ProductGroupProductDTO> findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivatedAndModifiedDate(
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
