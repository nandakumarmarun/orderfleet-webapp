package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.PriceTrendConfiguration;

/**
 * Spring Data JPA repository for the PriceTrendConfiguration entity.
 * 
 * @author Muhammed Riyas T
 * @since Sep 03, 2016
 */
public interface PriceTrendConfigurationRepository extends JpaRepository<PriceTrendConfiguration, Long> {

	@Query("select priceTrendConfiguration from PriceTrendConfiguration priceTrendConfiguration where priceTrendConfiguration.company.id = ?#{principal.companyId}")
	List<PriceTrendConfiguration> findAllByCompanyId();

	void deleteByCompanyId(Long companyId);
	
	@Query("select priceTrendConfiguration from PriceTrendConfiguration priceTrendConfiguration where priceTrendConfiguration.company.id = ?#{principal.companyId} and priceTrendConfiguration.lastModifiedDate > ?1")
	List<PriceTrendConfiguration> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	@Query("select ptc from PriceTrendConfiguration ptc where ptc.company.id = ?#{principal.companyId} and ptc.name in ?1")
	List<PriceTrendConfiguration> findByCompanyAndNameIn(List<String> names);


}
