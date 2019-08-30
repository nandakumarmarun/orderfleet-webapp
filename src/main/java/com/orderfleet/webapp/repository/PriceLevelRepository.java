package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.PriceLevel;

/**
 * Spring Data JPA repository for the PriceLevel entity.
 *
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
public interface PriceLevelRepository extends JpaRepository<PriceLevel, Long> {

	Optional<PriceLevel> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<PriceLevel> findOneByPid(String pid);

	@Query("select priceLevel from PriceLevel priceLevel where priceLevel.company.id = ?#{principal.companyId}")
	List<PriceLevel> findAllByCompanyId();

	List<PriceLevel> findByCompanyId(Long companyId);

	@Query("select priceLevel from PriceLevel priceLevel where priceLevel.company.id = ?#{principal.companyId}")
	Page<PriceLevel> findAllByCompanyId(Pageable pageable);

	@Query("select priceLevel from PriceLevel priceLevel where priceLevel.company.id = ?#{principal.companyId} Order By priceLevel.name asc")
	Page<PriceLevel> findAllByCompanyIdOrderByPriceLevelName(Pageable pageable);

	@Query("select priceLevel from PriceLevel priceLevel where priceLevel.company.id = ?#{principal.companyId} and priceLevel.activated = ?1 Order By priceLevel.name asc")
	Page<PriceLevel> findAllByCompanyIdAndActivatedPriceLevelOrderByName(Pageable pageable,boolean active);

	@Query("select priceLevel from PriceLevel priceLevel where priceLevel.company.id = ?#{principal.companyId} and priceLevel.activated = ?1")
	List<PriceLevel> findAllByCompanyIdAndDeactivatedPriceLevel(boolean deactive);

	List<PriceLevel> findByCompanyIdAndNameIgnoreCaseIn(Long id, Set<String> names);

	PriceLevel findFirstByCompanyId(Long id);

	@Query("select pricelevel from PriceLevel pricelevel where pricelevel.company.id = ?1 and UPPER(pricelevel.name) in ?2")
	List<PriceLevel> findByCompanyIdAndNameIgnoreCaseIn(Long id, List<String> plNames);
	
	@Transactional
	Long deleteByCompanyId(Long companyId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update PriceLevel priceLevel set priceLevel.activated = ?1 where priceLevel.id = ?2 and priceLevel.company.id = ?3")
	void updateActivatedByIdAndCompanyId(boolean activated, Long id, Long companyId);

	@Query("select priceLevel from PriceLevel priceLevel where priceLevel.company.id = ?#{principal.companyId} and priceLevel.id in ?1")
	List<PriceLevel> findAllByCompanyIdAndIdsIn(Set<Long> priceLeveIds);
	
}
