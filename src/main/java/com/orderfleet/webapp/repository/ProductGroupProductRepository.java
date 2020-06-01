package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;

/**
 * Spring Data JPA repository for the ProductGroupProduct entity.
 *
 * @author Sarath
 * @since Aug 9, 2016
 */
public interface ProductGroupProductRepository extends JpaRepository<ProductGroupProduct, Long> {

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid = ?1 ")
	List<ProductProfile> findProductByProductGroupPid(String productGroupPid);

	@Query("select productGroupProduct.productGroup from ProductGroupProduct productGroupProduct where productGroupProduct.product.pid = ?1 and productGroupProduct.productGroup.activated = true")
	List<ProductGroup> findProductGroupByProductPid(String productPid);

	@Query("select productGroupProduct.productGroup from ProductGroupProduct productGroupProduct where productGroupProduct.product.pid IN ?1 and productGroupProduct.productGroup.activated = true")
	List<ProductGroup> findProductGroupByProductPids(List<String> productPids);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid in ?1 ")
	List<ProductProfile> findProductByProductGroupPidIn(List<String> productGroupPids);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid in ?1 and productGroupProduct.product.productCategory.pid in ?2")
	List<ProductProfile> findProductByProductGroupPidInAndProductCategoryPidIn(List<String> productGroupPids,
			List<String> productCategoryPids);

	void deleteByProductGroupPid(String productGroupPid);

	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId}")
	List<ProductGroupProduct> findAllByCompanyId();

	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId} and productGroupProduct.productGroup.activated=true and productGroupProduct.product.activated=true")
	List<ProductGroupProduct> findByProductGroupProductActivatedAndCompanyId();

	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.pid = ?1")
	List<ProductGroupProduct> findAllByCompanyPid(String companyPid);

	List<ProductGroupProduct> findByProductGroupIn(List<ProductGroup> productGroups);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid in ?1 ")
	List<ProductProfile> findProductsByProductGroupPids(List<String> productGroupPids);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId}")
	List<ProductProfile> findAssignedProductProfiles();

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid in ?1 and productGroupProduct.product.activated = ?2 Order By productGroupProduct.product.name asc")
	List<ProductProfile> findProductByProductGroupPidInAndActivated(List<String> productGroupPids, boolean active);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid in ?1 and productGroupProduct.product.productCategory.pid in ?2 and productGroupProduct.product.activated = ?3 Order By productGroupProduct.product.name asc")
	List<ProductProfile> findProductByProductGroupPidInAndProductCategoryPidInAndActivated(
			List<String> productGroupPids, List<String> productCategoryPids, boolean active);

	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId} and productGroupProduct.productGroup.activated = ?1")
	List<ProductGroupProduct> findAllByCompanyIdAndActivated(boolean active);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId} and productGroupProduct.product.pid=?1 and productGroupProduct.product.activated = true")
	List<ProductProfile> findAllProductProfileByProductProfilePid(String productProfilePid);

	/**
	 * @author fahad
	 * @since Mar 3 2017
	 *
	 * @param productCategoyPids
	 * @param active
	 * @return list of ProductGroupProduct
	 */
	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId} and productGroupProduct.product.productCategory.pid in ?1 and  productGroupProduct.product.activated = ?2 Order by productGroupProduct.product.name asc")
	List<ProductGroupProduct> findProductGroupProductByProductCategoryPidInAndActivated(List<String> productCategoyPids,
			boolean active);

	/**
	 * @author fahad
	 * @since Mar 3 2017
	 *
	 * @param productGroupPids
	 * @param active
	 * @return list of ProductGroupProduct
	 */
	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId} and productGroupProduct.productGroup.pid in ?1 and productGroupProduct.product.activated = ?2 Order By productGroupProduct.product.name asc")
	List<ProductGroupProduct> findProductGroupProductByProductGroupPidInAndActivated(List<String> productGroupPids,
			boolean active);

	/**
	 * @author fahad
	 * @since Mar 3 2017
	 *
	 * @param productGroupPids
	 * @param productCategoryPids
	 * @return list of ProductGroupProduct
	 */
	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?#{principal.companyId} and productGroupProduct.productGroup.pid in ?1 and productGroupProduct.product.productCategory.pid in ?2")
	List<ProductGroupProduct> findProductGroupProductByProductGroupPidInAndProductCategoryPidIn(
			List<String> productGroupPids, List<String> productCategoryPids);

	Optional<ProductGroupProduct> findByCompanyIdAndProductGroupNameIgnoreCaseAndProductNameIgnoreCase(Long companyId,
			String pgName, String pName);

	@Query("select productGroupProduct from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid in ?1 and productGroupProduct.productGroup.activated = ?2 and productGroupProduct.product.activated = ?2 and (productGroupProduct.lastModifiedDate > ?3) or (productGroupProduct.productGroup.lastModifiedDate > ?3)")
	List<ProductGroupProduct> findByProductGroupPidInAndActivatedAndLastModifiedDateAndProductGroupLastModified(
			List<String> productGroupPids, boolean activated, LocalDateTime lastModifiedDate);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid = ?1 and productGroupProduct.productGroup.activated = ?2 and productGroupProduct.product.activated = ?2 and productGroupProduct.activated = ?2 ")
	List<ProductProfile> findProductByProductGroupPidAndActivated(String productGroupPid, boolean activated);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid = ?1 and productGroupProduct.productGroup.activated = ?2 and productGroupProduct.product.activated = ?2 and productGroupProduct.product.stockAvailabilityStatus=?3")
	List<ProductProfile> findProductByProductGroupPidAndActivatedAndStockAvailabilityStatus(String productGroupPid,
			boolean activated, StockAvailabilityStatus status);

	@Query("select productGroupProduct.product from ProductGroupProduct productGroupProduct where  productGroupProduct.productGroup.activated = ?1 and productGroupProduct.product.activated = ?1 and productGroupProduct.product.stockAvailabilityStatus=?2")
	List<ProductProfile> findProductByActivatedAndStockAvailabilityStatus(boolean activated,
			StockAvailabilityStatus status);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE ProductGroupProduct pgp SET pgp.activated = FALSE WHERE pgp.company.id = ?1")
	int updateProductGroupProductInactivate(Long companyId);

	@Query("select productGroupProduct.product.id from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup in ?1 ")
	Set<Long> findProductIdByProductGroupIn(Set<ProductGroup> productGroups);

	@Query("select productGroupProduct.product.id from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid = ?1 ")
	Set<Long> findProductIdByProductGroupPid(String productGroupPid);

	@Query("select productGroupProduct.product.pid from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid = ?1 ")
	Set<String> findProductPidByProductGroupPid(String productGroupPid);

	@Query("select productGroupProduct.product.name from ProductGroupProduct productGroupProduct where productGroupProduct.productGroup.pid = ?1 and productGroupProduct.product.activated=true and productGroupProduct.product.createdDate <= ?2")
	List<String> findProductNameByProductGroupPidAndActivatedTrueAndCreatedLessThan(String productGroupPid,
			LocalDateTime toDate);

	@Query("select productGroupProduct.product.pid from ProductGroupProduct productGroupProduct where productGroupProduct.product.pid in ?1 and productGroupProduct.productGroup.pid in ?2 and productGroupProduct.product.productCategory.pid in ?3 and productGroupProduct.product.activated = ?4 Order By productGroupProduct.product.name asc")
	List<String> findProductByProductPidInAndProductGroupPidInAndProductProductCategoryPidInAndActivated(
			List<String> productProfilePids, List<String> productGroupPids, List<String> productCategoryPids,
			boolean active);

	@Query("select productGroupProduct.product.pid from ProductGroupProduct productGroupProduct where productGroupProduct.product.pid in ?1 and productGroupProduct.product.productCategory.pid in ?2 and productGroupProduct.product.activated = ?3 Order By productGroupProduct.product.name asc")
	List<String> findProductByProductPidInAndProductProductCategoryPidInAndActivated(List<String> productProfilePids,
			List<String> productCategoryPids, boolean active);

	@Query("select productGroupProduct.product.pid from ProductGroupProduct productGroupProduct where productGroupProduct.product.pid in ?1 and productGroupProduct.productGroup.pid in ?2 and productGroupProduct.product.activated = ?3 Order By productGroupProduct.product.name asc")
	List<String> findProductByProductPidInAndProductGroupPidInAndActivated(List<String> productProfilePids,
			List<String> productGroupPids, boolean active);

	void deleteByCompanyId(Long companyId);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_product_group_product where company_id= ?1 ", nativeQuery = true)
	void deleteByCompany(Long companyId);

	@Transactional
	@Modifying
	@Query(value = "delete from tbl_product_group_product where company_id= ?1 and product_group_id in ?2 ", nativeQuery = true)
	void deleteByCompanyAndProductGroup(Long companyId, List<Long> productGroupIds);

	@Transactional
	@Modifying
	@Query("delete from ProductGroupProduct productGroupProduct where productGroupProduct.company.id = ?1 and productGroupProduct.id in ?2")
	void deleteByIdIn(Long companyId, List<Long> pgIds);
}
