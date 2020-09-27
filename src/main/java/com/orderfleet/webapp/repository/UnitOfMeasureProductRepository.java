package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.domain.UnitOfMeasureProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;

/**
 * Spring Data JPA repository for the UnitOfMeasureProduct entity.
 *
 * @author Sarath
 * @since Aug 9, 2016
 */
public interface UnitOfMeasureProductRepository extends JpaRepository<UnitOfMeasureProduct, Long> {

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid = ?1 ")
	List<ProductProfile> findProductByUnitOfMeasurePid(String unitOfMeasurePid);

	@Query("select unitOfMeasureProduct.unitOfMeasure from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.product.pid = ?1 and unitOfMeasureProduct.unitOfMeasure.activated = true")
	List<UnitOfMeasure> findUnitOfMeasureByProductPid(String productPid);

	@Query("select unitOfMeasureProduct.unitOfMeasure from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.product.pid IN ?1 and unitOfMeasureProduct.unitOfMeasure.activated = true")
	List<UnitOfMeasure> findUnitOfMeasureByProductPids(List<String> productPids);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid in ?1 ")
	List<ProductProfile> findProductByUnitOfMeasurePidIn(List<String> unitOfMeasurePids);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid in ?1 and unitOfMeasureProduct.product.productCategory.pid in ?2")
	List<ProductProfile> findProductByUnitOfMeasurePidInAndProductCategoryPidIn(List<String> unitOfMeasurePids,
			List<String> productCategoryPids);

	void deleteByUnitOfMeasurePid(String unitOfMeasurePid);

	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId}")
	List<UnitOfMeasureProduct> findAllByCompanyId();

	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId} and unitOfMeasureProduct.unitOfMeasure.activated=true and unitOfMeasureProduct.product.activated=true")
	List<UnitOfMeasureProduct> findByUnitOfMeasureProductActivatedAndCompanyId();

	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.pid = ?1")
	List<UnitOfMeasureProduct> findAllByCompanyPid(String companyPid);

	List<UnitOfMeasureProduct> findByUnitOfMeasureIn(List<UnitOfMeasure> unitOfMeasures);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid in ?1 ")
	List<ProductProfile> findProductsByUnitOfMeasurePids(List<String> unitOfMeasurePids);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId}")
	List<ProductProfile> findAssignedProductProfiles();

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid in ?1 and unitOfMeasureProduct.product.activated = ?2 Order By unitOfMeasureProduct.product.name asc")
	List<ProductProfile> findProductByUnitOfMeasurePidInAndActivated(List<String> unitOfMeasurePids, boolean active);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid in ?1 and unitOfMeasureProduct.product.productCategory.pid in ?2 and unitOfMeasureProduct.product.activated = ?3 Order By unitOfMeasureProduct.product.name asc")
	List<ProductProfile> findProductByUnitOfMeasurePidInAndProductCategoryPidInAndActivated(
			List<String> unitOfMeasurePids, List<String> productCategoryPids, boolean active);

	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId} and unitOfMeasureProduct.unitOfMeasure.activated = ?1")
	List<UnitOfMeasureProduct> findAllByCompanyIdAndActivated(boolean active);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId} and unitOfMeasureProduct.product.pid=?1 and unitOfMeasureProduct.product.activated = true")
	List<ProductProfile> findAllProductProfileByProductProfilePid(String productProfilePid);

	/**
	 * @author fahad
	 * @since Mar 3 2017
	 *
	 * @param productCategoyPids
	 * @param active
	 * @return list of UnitOfMeasureProduct
	 */
	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId} and unitOfMeasureProduct.product.productCategory.pid in ?1 and  unitOfMeasureProduct.product.activated = ?2 Order by unitOfMeasureProduct.product.name asc")
	List<UnitOfMeasureProduct> findUnitOfMeasureProductByProductCategoryPidInAndActivated(List<String> productCategoyPids,
			boolean active);

	/**
	 * @author fahad
	 * @since Mar 3 2017
	 *
	 * @param unitOfMeasurePids
	 * @param active
	 * @return list of UnitOfMeasureProduct
	 */
	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId} and unitOfMeasureProduct.unitOfMeasure.pid in ?1 and unitOfMeasureProduct.product.activated = ?2 Order By unitOfMeasureProduct.product.name asc")
	List<UnitOfMeasureProduct> findUnitOfMeasureProductByUnitOfMeasurePidInAndActivated(List<String> unitOfMeasurePids,
			boolean active);

	/**
	 * @author fahad
	 * @since Mar 3 2017
	 *
	 * @param unitOfMeasurePids
	 * @param productCategoryPids
	 * @return list of UnitOfMeasureProduct
	 */
	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?#{principal.companyId} and unitOfMeasureProduct.unitOfMeasure.pid in ?1 and unitOfMeasureProduct.product.productCategory.pid in ?2")
	List<UnitOfMeasureProduct> findUnitOfMeasureProductByUnitOfMeasurePidInAndProductCategoryPidIn(
			List<String> unitOfMeasurePids, List<String> productCategoryPids);

	Optional<UnitOfMeasureProduct> findByCompanyIdAndUnitOfMeasureNameIgnoreCaseAndProductNameIgnoreCase(Long companyId,
			String pgName, String pName);

	@Query("select unitOfMeasureProduct from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid in ?1 and unitOfMeasureProduct.unitOfMeasure.activated = ?2 and unitOfMeasureProduct.product.activated = ?2 and (unitOfMeasureProduct.lastModifiedDate > ?3) or (unitOfMeasureProduct.unitOfMeasure.lastModifiedDate > ?3)")
	List<UnitOfMeasureProduct> findByUnitOfMeasurePidInAndActivatedAndLastModifiedDateAndUnitOfMeasureLastModified(
			List<String> unitOfMeasurePids, boolean activated, LocalDateTime lastModifiedDate);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid = ?1 and unitOfMeasureProduct.unitOfMeasure.activated = ?2 and unitOfMeasureProduct.product.activated = ?2 and unitOfMeasureProduct.activated = ?2 ")
	List<ProductProfile> findProductByUnitOfMeasurePidAndActivated(String unitOfMeasurePid, boolean activated);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid = ?1 and unitOfMeasureProduct.unitOfMeasure.activated = ?2 and unitOfMeasureProduct.product.activated = ?2 and unitOfMeasureProduct.product.stockAvailabilityStatus=?3")
	List<ProductProfile> findProductByUnitOfMeasurePidAndActivatedAndStockAvailabilityStatus(String unitOfMeasurePid,
			boolean activated, StockAvailabilityStatus status);

	@Query("select unitOfMeasureProduct.product from UnitOfMeasureProduct unitOfMeasureProduct where  unitOfMeasureProduct.unitOfMeasure.activated = ?1 and unitOfMeasureProduct.product.activated = ?1 and unitOfMeasureProduct.product.stockAvailabilityStatus=?2")
	List<ProductProfile> findProductByActivatedAndStockAvailabilityStatus(boolean activated,
			StockAvailabilityStatus status);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE UnitOfMeasureProduct pgp SET pgp.activated = FALSE WHERE pgp.company.id = ?1")
	int updateUnitOfMeasureProductInactivate(Long companyId);

	@Query("select unitOfMeasureProduct.product.id from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure in ?1 ")
	Set<Long> findProductIdByUnitOfMeasureIn(Set<UnitOfMeasure> unitOfMeasures);

	@Query("select unitOfMeasureProduct.product.id from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid = ?1 ")
	Set<Long> findProductIdByUnitOfMeasurePid(String unitOfMeasurePid);

	@Query("select unitOfMeasureProduct.product.pid from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid = ?1 ")
	Set<String> findProductPidByUnitOfMeasurePid(String unitOfMeasurePid);

	@Query("select unitOfMeasureProduct.product.name from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.unitOfMeasure.pid = ?1 and unitOfMeasureProduct.product.activated=true and unitOfMeasureProduct.product.createdDate <= ?2")
	List<String> findProductNameByUnitOfMeasurePidAndActivatedTrueAndCreatedLessThan(String unitOfMeasurePid,
			LocalDateTime toDate);

	@Query("select unitOfMeasureProduct.product.pid from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.product.pid in ?1 and unitOfMeasureProduct.unitOfMeasure.pid in ?2 and unitOfMeasureProduct.product.productCategory.pid in ?3 and unitOfMeasureProduct.product.activated = ?4 Order By unitOfMeasureProduct.product.name asc")
	List<String> findProductByProductPidInAndUnitOfMeasurePidInAndProductProductCategoryPidInAndActivated(
			List<String> productProfilePids, List<String> unitOfMeasurePids, List<String> productCategoryPids,
			boolean active);

	@Query("select unitOfMeasureProduct.product.pid from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.product.pid in ?1 and unitOfMeasureProduct.product.productCategory.pid in ?2 and unitOfMeasureProduct.product.activated = ?3 Order By unitOfMeasureProduct.product.name asc")
	List<String> findProductByProductPidInAndProductProductCategoryPidInAndActivated(List<String> productProfilePids,
			List<String> productCategoryPids, boolean active);

	@Query("select unitOfMeasureProduct.product.pid from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.product.pid in ?1 and unitOfMeasureProduct.unitOfMeasure.pid in ?2 and unitOfMeasureProduct.product.activated = ?3 Order By unitOfMeasureProduct.product.name asc")
	List<String> findProductByProductPidInAndUnitOfMeasurePidInAndActivated(List<String> productProfilePids,
			List<String> unitOfMeasurePids, boolean active);

	void deleteByCompanyId(Long companyId);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_unit_of_measure_product where company_id= ?1 ", nativeQuery = true)
	void deleteByCompany(Long companyId);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_unit_of_measure_product where company_id= ?1 and unit_of_measure_id in ?2 ", nativeQuery = true)
	void deleteByCompanyAndUnitOfMeasure(Long companyId, List<Long> unitOfMeasureIds);

	@Transactional
	@Modifying
	@Query("delete from UnitOfMeasureProduct unitOfMeasureProduct where unitOfMeasureProduct.company.id = ?1 and unitOfMeasureProduct.id in ?2")
	void deleteByIdIn(Long companyId, List<Long> pgIds);
}
