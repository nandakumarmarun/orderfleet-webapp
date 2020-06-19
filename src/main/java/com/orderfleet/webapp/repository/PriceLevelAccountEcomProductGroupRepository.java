package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountEcomProductGroup;

public interface PriceLevelAccountEcomProductGroupRepository extends JpaRepository<PriceLevelAccountEcomProductGroup, Long> {
	@Query("select priceLevelAccountEcomProductGroup from PriceLevelAccountEcomProductGroup priceLevelAccountEcomProductGroup where priceLevelAccountEcomProductGroup.company.id = ?#{principal.companyId} Order By priceLevelAccountEcomProductGroup.accountProfile.name asc")
	List<PriceLevelAccountEcomProductGroup> findAllByCompanyId();

	Optional<PriceLevelAccountEcomProductGroup> findOneByPid(String pid);

	@Query("select priceLevelAccountEcomProductGroup.priceLevel from PriceLevelAccountEcomProductGroup priceLevelAccountEcomProductGroup where priceLevelAccountEcomProductGroup.company.id = ?#{principal.companyId} and priceLevelAccountEcomProductGroup.accountProfile.pid = ?1")
	List<PriceLevel> findAllByAccountProfile(String accountPid);

	@Query("select priceLevelAccountEcomProductGroup from PriceLevelAccountEcomProductGroup priceLevelAccountEcomProductGroup where priceLevelAccountEcomProductGroup.company.id = ?1")
	List<PriceLevelAccountEcomProductGroup> findAllByCompanyId(Long companyId);

	@Query("select priceLevelAccountEcomProductGroup.priceLevel from PriceLevelAccountEcomProductGroup priceLevelAccountEcomProductGroup where priceLevelAccountEcomProductGroup.company.id = ?1 and priceLevelAccountEcomProductGroup.accountProfile.pid = ?2 and priceLevelAccountEcomProductGroup.productGroup.pid = ?3 and priceLevelAccountEcomProductGroup.priceLevel.pid = ?4")
	Optional<PriceLevelAccountEcomProductGroup> findOneByAccountProfilePidAndProductGroupPidAndPriceLevelPid(Long companyId,
			String accountPid, String productGroupPid, String PriceLevelPid);

	@Query("select priceLevelAccountEcomProductGroup.priceLevel from PriceLevelAccountEcomProductGroup priceLevelAccountEcomProductGroup where priceLevelAccountEcomProductGroup.company.id = ?1 and priceLevelAccountEcomProductGroup.accountProfile.pid = ?2 and priceLevelAccountEcomProductGroup.productGroup.pid = ?3")
	Optional<PriceLevel> findOneByAccountProfilePidAndProductGroupPid(Long companyId,
			String accountPid, String productGroupPid);
	@Query("select priceLevelAccountEcomProductGroup from PriceLevelAccountEcomProductGroup priceLevelAccountEcomProductGroup where priceLevelAccountEcomProductGroup.company.id = ?1 and priceLevelAccountEcomProductGroup.accountProfile.pid IN ?2 and priceLevelAccountEcomProductGroup.productGroup.pid IN ?3")
	List<PriceLevelAccountEcomProductGroup> findByAccountPidsAndProductGroupPisds(Long companyId,List<String> accountPids,List<String> productGroupPids);
}
