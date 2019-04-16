package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductProfile;

/**
 * Spring Data JPA repository for the PriceLevelList entity.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
public interface PriceLevelListRepository extends JpaRepository<PriceLevelList, Long> {

	Optional<PriceLevelList> findOneByPid(String pid);

	@Query("select priceLevelList from PriceLevelList priceLevelList where priceLevelList.company.id = ?#{principal.companyId}")
	List<PriceLevelList> findAllByCompanyId();
	
	@Query("select priceLevelList from PriceLevelList priceLevelList where priceLevelList.company.id = ?1")
	List<PriceLevelList> findAllByCompanyId(Long companyId);

	@Query("select priceLevelList from PriceLevelList priceLevelList where priceLevelList.company.id = ?#{principal.companyId}")
	Page<PriceLevelList> findAllByCompanyId(Pageable pageable);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 and pLevelList.productProfile.pid = ?2")
	List<PriceLevelList> findAllByCompanyIdPriceLevelPidAndProductProfilePid(String priceLevelPid,
			String productProfilePid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 ")
	List<PriceLevelList> findAllByCompanyIdAndPriceLevelPid(String priceLevelPid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.productProfile.pid = ?1")
	List<PriceLevelList> findAllByCompanyIdAndProductProfilePid(String productProfilePid);

	Optional<PriceLevelList> findOneByCompanyIdAndPriceLevelNameIgnoreCaseAndProductProfileNameIgnoreCase(
			Long companyId, String priceLevelName, String productProfileName);

	List<PriceLevelList> findByCompanyIdAndPriceLevelNameIgnoreCaseIn(Long id, Set<String> pllNames);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 and (pLevelList.lastModifiedDate > ?2) or (pLevelList.priceLevel.lastModifiedDate > ?2)")
	List<PriceLevelList> findAllByCompanyIdAndPriceLevelPidAndpriceLevelLastModifiedDate(String priceLevelPid,
			LocalDateTime lastModifiedDate);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 and pLevelList.lastModifiedDate > ?2")
	List<PriceLevelList> findAllByCompanyIdAndPriceLevelPidAndLastModifiedDate(String priceLevelPid,
			LocalDateTime lastModifiedDate);

	@Query("select pLevelList.productProfile from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1")
	List<ProductProfile> findAllProductProfileByCompanyIdAndPriceLevelPid(String priceLevelPid);

	Optional<PriceLevelList> findByCompanyIdAndPriceLevelPidAndProductProfilePidOrderByProductProfileNameDesc(
			Long companyId, String priceLevelPids, String productPid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.productProfile.productCategory.pid = ?1")
	List<PriceLevelList> findAllByCompanyIdAndProductCategoryPid(String productCategoryPid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.productProfile.pid = ?1 and pLevelList.productProfile.productCategory.pid = ?2")
	List<PriceLevelList> findAllByCompanyIdAndProductProfilePidAndProductCategoryPid(String productProfilePid,
			String productCategoryPid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 and pLevelList.productProfile.productCategory.pid = ?2")
	List<PriceLevelList> findAllByCompanyIdAndPriceLevelPidAndProductCategoryPid(String priceLevelPid,
			String productCategoryPid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 and pLevelList.productProfile.pid = ?2 and pLevelList.productProfile.productCategory.pid = ?3")
	List<PriceLevelList> findAllByCompanyIdAndPriceLevelPidAndProductProfilePidAndProductCategoryPid(
			String priceLevelPid, String productProfilePid, String productCategoryPid);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.productProfile.pid in ?1")
	List<PriceLevelList> findAllByCompanyIdAndProductProfilePidIn(List<String> productProfilePids);
	
	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?1 and pLevelList.productProfile.pid in ?2")
	List<PriceLevelList> findAllByCompanyAndProductProfilePidIn(Long companyId,List<String> productProfilePids);

	@Query("select pLevelList from PriceLevelList pLevelList where pLevelList.company.id = ?#{principal.companyId} and pLevelList.priceLevel.pid = ?1 and pLevelList.productProfile.pid in ?2")
	List<PriceLevelList> findAllByCompanyIdAndPriceLevelPidAndProductProfilePidIn(String priceLevelPid,
			List<String> productProfilePids);
	
	@Transactional
	@Modifying
	@Query("delete from PriceLevelList pLevelList where pLevelList.company.id = ?1")
	void deleteByCompanyId(Long companyId);
}
