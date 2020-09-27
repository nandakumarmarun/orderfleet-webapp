package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureDTO;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Spring Data JPA repository for the UnitOfMeasureProduct entity.
 *
 * @author Sarath
 * @since Aug 9, 2016
 */
public interface UnitOfMeasureProductService {

	void save(String unitOfMeasurePid, String assignedProducts);

	List<ProductProfileDTO> findProductByUnitOfMeasurePid(String unitOfMeasurePid);

	List<ProductProfileDTO> findByUnitOfMeasurePids(List<String> groupPids);

	List<ProductProfileDTO> findByUnitOfMeasurePidsAndCategoryPids(List<String> groupPids, List<String> categoryPids);

	List<UnitOfMeasureProduct> findAllByCompany();

	public List<UnitOfMeasureProductDTO> findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUser();

	List<ProductProfileDTO> findAllByProductProfilePid(String productProfilePid);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by UnitOfMeasurePids, CategoryPids And
	 *        ActivatedProductProfile.
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @param groupPids
	 *            the groupPids of the entity
	 * @return the list
	 */
	List<ProductProfileDTO> findByUnitOfMeasurePidsAndCategoryPidsAndActivatedProductProfile(List<String> groupPids,
			List<String> categoryPids);

	/**
	 * @author Fahad
	 * @since Feb 17, 2017
	 *
	 *        find ProductProfile by UnitOfMeasurePids And Activated
	 * @param groupPids
	 *            the groupPids of the entity
	 * @return the list
	 */
	List<ProductProfileDTO> findByUnitOfMeasurePidsAndActivated(List<String> groupPids);

	List<UnitOfMeasureDTO> findUnitOfMeasureByProductPid(String productPid);

	/**
	 * @author Fahad
	 * @since Feb 28, 2017
	 *
	 *        find UnitOfMeasureProduct by CompanyId And Activated
	 * @param active
	 *            the active of the entity
	 * @return the list of UnitOfMeasureProduct
	 */
	List<UnitOfMeasureProduct> findAllByCompanyAndActivated(boolean active);

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find UnitOfMeasureProduct by ProductCategoryPids
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @return the list
	 */
	List<UnitOfMeasureProduct> findUnitOfMeasureProductByProductCategoryPidsAndActivated(List<String> categoryPids);

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find UnitOfMeasureProduct by UnitOfMeasurePids
	 * @param groupPids
	 *            the groupPids of the entity
	 * @return the list
	 */
	List<UnitOfMeasureProduct> findUnitOfMeasureProductByUnitOfMeasurePidsAndActivated(List<String> groupPids);

	/**
	 * @author Fahad
	 * @since Mar 3, 2017
	 *
	 *        find UnitOfMeasureProduct by UnitOfMeasurePids And CategoryPids
	 * @param groupPids
	 *            the groupPids of the entity
	 * @param categoryPids
	 *            the categoryPids of the entity
	 * @return the list
	 */
	List<UnitOfMeasureProduct> findUnitOfMeasureProductByUnitOfMeasurePidsAndCategoryPidsAndActivatedProductProfile(
			List<String> groupPids, List<String> categoryPids);

	List<UnitOfMeasureProductDTO> findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUserAndUnitOfMeasureActivated(
			boolean activted);

	List<UnitOfMeasureProductDTO> findUnitOfMeasureProductsByUserUnitOfMeasureIsCurrentUserAndUnitOfMeasureActivatedAndModifiedDate(
			boolean activted, LocalDateTime lastModifiedDate);

	List<ProductProfileDTO> findProductByUnitOfMeasurePidAndActivatedAndStockAvailabilityStatus(String unitOfMeasurePid,
			boolean activated, StockAvailabilityStatus status);

	List<ProductProfileDTO> findProductByActivatedAndStockAvailabilityStatus(boolean activated,
			StockAvailabilityStatus status);

	List<ProductProfileDTO> findEcomProductProfileByUnitOfMeasurePid(String unitOfMeasurePid);
	
	List<String> findProductByProductProfilePidAndUnitOfMeasurePidAndProductCategoryPidAndActivated(List<String> productProfilePids, List<String> unitOfMeasurePids, List<String> productCategoryPids);
	
	List<String> findProductByProductProfilePidAndProductCategoryPidAndActivated(List<String> productProfilePids, List<String> productCategoryPids);
	
	List<String> findProductByProductProfilePidAndUnitOfMeasurePidAndActivated(List<String> productProfilePids, List<String> unitOfMeasurePids);
	
}
