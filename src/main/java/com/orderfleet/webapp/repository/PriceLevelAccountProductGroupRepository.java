package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;

public interface PriceLevelAccountProductGroupRepository extends JpaRepository<PriceLevelAccountProductGroup, Long> {
	@Query("select priceLevelAccountProductGroup from PriceLevelAccountProductGroup priceLevelAccountProductGroup where priceLevelAccountProductGroup.company.id = ?#{principal.companyId} Order By priceLevelAccountProductGroup.accountProfile.name asc")
	List<PriceLevelAccountProductGroup> findAllByCompanyId();

	Optional<PriceLevelAccountProductGroup> findOneByPid(String pid);

	@Query("select priceLevelAccountProductGroup.priceLevel from PriceLevelAccountProductGroup priceLevelAccountProductGroup where priceLevelAccountProductGroup.company.id = ?#{principal.companyId} and priceLevelAccountProductGroup.accountProfile.pid = ?1")
	List<PriceLevel> findAllByAccountProfile(String accountPid);

	@Query("select priceLevelAccountProductGroup from PriceLevelAccountProductGroup priceLevelAccountProductGroup where priceLevelAccountProductGroup.company.id = ?1")
	List<PriceLevelAccountProductGroup> findAllByCompanyId(Long companyId);

	@Query("select priceLevelAccountProductGroup.priceLevel from PriceLevelAccountProductGroup priceLevelAccountProductGroup where priceLevelAccountProductGroup.company.id = ?1 and priceLevelAccountProductGroup.accountProfile.pid = ?2 and priceLevelAccountProductGroup.productGroup.pid = ?3 and priceLevelAccountProductGroup.priceLevel.pid = ?4")
	Optional<PriceLevelAccountProductGroup> findOneByAccountProfilePidAndProductGroupPidAndPriceLevelPid(Long companyId,
			String accountPid, String productGroupPid, String PriceLevelPid);

	@Query("select priceLevelAccountProductGroup.priceLevel from PriceLevelAccountProductGroup priceLevelAccountProductGroup where priceLevelAccountProductGroup.company.id = ?1 and priceLevelAccountProductGroup.accountProfile.pid = ?2 and priceLevelAccountProductGroup.productGroup.pid = ?3")
	Optional<PriceLevel> findOneByAccountProfilePidAndProductGroupPid(Long companyId,
			String accountPid, String productGroupPid);
	@Query("select priceLevelAccountProductGroup from PriceLevelAccountProductGroup priceLevelAccountProductGroup where priceLevelAccountProductGroup.company.id = ?1 and priceLevelAccountProductGroup.accountProfile.pid IN ?2 and priceLevelAccountProductGroup.productGroup.pid IN ?3")
	List<PriceLevelAccountProductGroup> findByAccountPidsAndProductGroupPisds(Long companyId,List<String> accountPids,List<String> productGroupPids);
}
