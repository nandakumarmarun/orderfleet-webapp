package com.orderfleet.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.GuidedSellingConfig;

/**
 * Spring Data JPA repository for the GuidedSellingConfig entity.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
public interface GuidedSellingConfigRepository extends JpaRepository<GuidedSellingConfig, Long> {

	@Query("select guidedSellingConfig from GuidedSellingConfig guidedSellingConfig where guidedSellingConfig.company.id = ?#{principal.companyId}")
	GuidedSellingConfig findByCompanyId();

}
