package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PriceTrendProductGroup;

/**
 * Spring Data JPA repository for the PriceTrendProductGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface PriceTrendProductGroupRepository extends JpaRepository<PriceTrendProductGroup, Long> {

	Optional<PriceTrendProductGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<PriceTrendProductGroup> findOneByPid(String pid);

	@Query("select priceTrendProductGroup from PriceTrendProductGroup priceTrendProductGroup where priceTrendProductGroup.company.id = ?#{principal.companyId}")
	List<PriceTrendProductGroup> findAllByCompanyId();

	@Query("select priceTrendProductGroup from PriceTrendProductGroup priceTrendProductGroup where priceTrendProductGroup.company.id = ?#{principal.companyId}")
	Page<PriceTrendProductGroup> findAllByCompanyId(Pageable pageable);

	@Query("select priceTrendProductGroup from PriceTrendProductGroup priceTrendProductGroup where priceTrendProductGroup.company.id = ?#{principal.companyId} and priceTrendProductGroup.activated = ?1")
	List<PriceTrendProductGroup> findAllByCompanyIdAndPriceTrendProductGroupActivatedOrDeactivated(boolean active);

	@Query("select priceTrendProductGroup from PriceTrendProductGroup priceTrendProductGroup where priceTrendProductGroup.company.id = ?#{principal.companyId} Order By priceTrendProductGroup.name asc")
	Page<PriceTrendProductGroup> findAllByCompanyIdOrderByPriceTrendProductGroupName(Pageable pageable);

	@Query("select priceTrendProductGroup from PriceTrendProductGroup priceTrendProductGroup where priceTrendProductGroup.company.id = ?#{principal.companyId} and priceTrendProductGroup.activated = ?1 Order By priceTrendProductGroup.name asc")
	Page<PriceTrendProductGroup> findAllByCompanyIdAndActivatedOrderByPriceTrendProductGroupName(Pageable pageable,
			boolean active);

	@Query("select priceTrendProductGroup from PriceTrendProductGroup priceTrendProductGroup where priceTrendProductGroup.company.id = ?#{principal.companyId} and priceTrendProductGroup.activated = ?1 and priceTrendProductGroup.lastModifiedDate > ?2")
	List<PriceTrendProductGroup> findAllByCompanyIdAndPriceTrendProductGroupActivatedAndlastModifiedDate(boolean active,
			LocalDateTime lastModifiedDate);
	
	@Query("select ptpg from PriceTrendProductGroup ptpg where ptpg.company.id = ?#{principal.companyId} and ptpg.activated = true and ptpg.pid in ?1")
	List<PriceTrendProductGroup> findByProductGroupPidIn(List<String> productGroupPids);
	
	@Query("select ptpg from PriceTrendProductGroup ptpg where ptpg.company.id = ?#{principal.companyId} and ptpg.activated = true and ptpg.pid = ?1")
	List<PriceTrendProductGroup> findByProductGroupPid(String productGroupPid);
	
}
