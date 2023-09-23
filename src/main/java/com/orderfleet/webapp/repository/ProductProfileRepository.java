package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.ProductCategory;

import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.enums.StockAvailabilityStatus;

/**
 * Spring Data JPA repository for the ProductProfile entity.
 * 
 * @author Muhammed Riyas T
 * @since May 18, 2016
 */
public interface ProductProfileRepository extends JpaRepository<ProductProfile, Long> {

	Optional<ProductProfile> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<ProductProfile> findOneByPid(String pid);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} order by productProfile.name asc")
	List<ProductProfile> findAllByCompanyId();

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?1  order by productProfile.name asc")
	List<ProductProfile> findByCompanyId(Long companyId);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.pid = ?1")
	Optional<ProductProfile> findOneByCompanyIdandPid(String pid);

	@Query("select productProfile.pid,productProfile.name,productProfile.price from ProductProfile productProfile where productProfile.pid in ?1 and productProfile.company.id = ?#{principal.companyId} order by productProfile.name asc")
	List<Object[]> findAllNameAndPriceByCompanyIdAndPidIn(List<String> productPids);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.activated=true order by productProfile.name asc")
	List<ProductProfile> findAllByCompanyIdActivatedTrue();

	List<ProductProfile> findByCompanyIdAndActivatedTrue(Long companyId);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId}")
	Page<ProductProfile> findAllByCompanyId(Pageable pageable);

	List<ProductProfile> findByProductCategoryPid(String categoryPid);

	List<ProductProfile> findByProductCategoryPidIn(List<String> productCategoyPids);

	Page<ProductProfile> findByProductCategoryIn(List<ProductCategory> productCategories, Pageable pageable);

	@Query("select productProfile.name from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId}")
	List<String> findProductNamesByCompanyId();

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.productCategory.pid=?1")
	Page<ProductProfile> findAllByCompanyIdAndCategoryPid(Pageable pageable, String CategoryPid);

	@Query("select productProfile.files from ProductProfile productProfile where productProfile.pid = ?1")
	Set<File> findProductProfileImages(String productPid);

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.taxRate = ?1 where productProfile in ?2")
	void updateTaxRate(double taxRate, List<ProductProfile> productProfiles);

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.unitQty = ?1 where productProfile in ?2")
	void updateUnitQuantity(double unitQty, List<ProductProfile> productProfiles);

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.discountPercentage = ?1 where productProfile in ?2")
	void updateDiscoundPercentage(double unitQty, List<ProductProfile> productProfiles);

	List<ProductProfile> findAllByCompanyPid(String companyPid);

	@Query(value = "select * from tbl_product_profile where company_id=?1", nativeQuery = true)
	List<ProductProfile> findAllByCompanyID(long companyId);

	List<ProductProfile> findByProductCategoryIn(List<ProductCategory> productCategories);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.productCategory in (select userProductCategory.productCategory from UserProductCategory userProductCategory where userProductCategory.user.login = ?#{principal.username})")
	List<ProductProfile> findByCurrentUsersProductCategoriesIn();

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.size = ?1 where productProfile.pid in ?2")
	void updateSize(double size, List<String> productProfiles);

	Page<ProductProfile> findByProductCategoryInAndActivatedTrue(List<ProductCategory> productCategories,
			Pageable pageable);

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.description = '0' where productProfile.company.id = ?#{principal.companyId}")
	void updateSDiscription();

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.activated = ?1 Order By productProfile.name asc")
	List<ProductProfile> findAllByCompanyIdAndActivatedOrDeactivatedProductProfileOrderByName(boolean active);

	@Query(value = "select * from tbl_product_profile  where company_Id = ?#{principal.companyId} and activated = ?1 Order By name asc Limit 1000", nativeQuery = true)
	List<ProductProfile> findAllByCompanyIdAndActivatedOrDeactivatedProductProfileOrderByNameCountByLimit(
			boolean active);
	
	
	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.productCategory.pid in ?1 and  productProfile.activated = ?2 Order by productProfile.name asc")
	List<ProductProfile> findByProductCategoryPidInAndActivated(List<String> productCategoyPids, boolean active);

	List<ProductProfile> findByCompanyIdAndNameIn(Long companyId, Set<String> names);

	List<ProductProfile> findByCompanyIdAndNameIgnoreCaseIn(Long id, Set<String> ppNames);

	List<ProductProfile> findByCompanyIdAndAliasIgnoreCaseIn(Long id, Set<String> ppAlias);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.productCategory in ?1 and productProfile.activated = ?2 and productProfile.lastModifiedDate > ?3")
	Page<ProductProfile> findByProductCategoryInAndActivatedTrueAndLastModifiedDate(
			List<ProductCategory> productCategories, boolean activated, LocalDateTime lastModifiedDate,
			Pageable pageable);

	// mainly used for noor asia company
	Optional<ProductProfile> findByCompanyIdAndAliasIgnoreCase(Long id, String alias);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and productProfile.activated = ?1 and productProfile.stockAvailabilityStatus=?2 Order By productProfile.name asc")
	List<ProductProfile> findAllByCompanyIdAndActivatedAndStockAvailabilityStatusProductProfileOrderByName(
			boolean active, StockAvailabilityStatus status);

	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?1 order by productProfile.name asc")
	List<ProductProfile> findAllByCompanyId(Long companyId);

	@Query("select productProfile.name from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and activated=true and productProfile.productCategory.pid=?1 and productProfile.createdDate <= ?2 Order By productProfile.name asc")
	List<String> findProductNamesByCompanyIdAndActivatedTrueAndCategoryPidAndCreatedLessThan(String categoryPid,
			LocalDateTime toDate);

	@Query("select productProfile.name from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and activated=true and productProfile.createdDate <= ?1 Order By productProfile.name asc")
	List<String> findProductNamesByCompanyIdAndActivatedTrueAndCreatedLessThan(LocalDateTime toDate);

	List<ProductProfile> findByPidInAndActivated(List<String> profilePids, boolean active);

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.activated = false where productProfile.company.id = ?1")
	void deactivateAllProductProfile(Long companyId);

	@Modifying
	@Query("update ProductProfile productProfile set productProfile.activated = false where productProfile.company.id = ?#{principal.companyId} AND productProfile.id IN ?1")
	void deactivateProductProfileUsingInId(Set<Long> id);
	@Modifying
	@Query("update ProductProfile productProfile set productProfile.activated = false ,productProfile.lastModifiedDate = ?2 where productProfile.company.id = ?#{principal.companyId} AND productProfile.id IN ?1")
	void deactivateProductProfileUsingInIdAndLastModifiedDate(Set<Long> id,LocalDateTime localDateTime);

	@Query("select productProfile.productDescription from ProductProfile productProfile where productProfile.company.id = ?#{principal.companyId} and activated=true and productProfile.createdDate <= ?1 Order By productProfile.productDescription asc")
	List<String> findProductDescriptionByCompanyIdAndActivatedTrueAndCreatedLessThan(LocalDateTime atTime);

	@Query("Select productProfile from ProductProfile  productProfile where productProfile.company.id = ?#{principal.companyId} and lower(productProfile.name) like lower(concat('%', :name,'%'))")
	public List<ProductProfile> searchByName(@Param("name") String name);
	@Query("select productProfile from ProductProfile productProfile where productProfile.company.id = ?1 and productProfile.activated = 'true' order by productProfile.name asc")
	List<ProductProfile> findAllByCompanyIdAndActivated(Long id);
}
