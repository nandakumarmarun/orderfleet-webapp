package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.CompetitorProfile;
import com.orderfleet.webapp.domain.PriceTrendProductCompetitor;

/**
 * repository for PriceTrendProductCompetitor
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
public interface PriceTrendProductCompetitorRepository extends JpaRepository<PriceTrendProductCompetitor, Long> {

	@Query("select priceTrendProductCompetitor.competitor from PriceTrendProductCompetitor priceTrendProductCompetitor where priceTrendProductCompetitor.priceTrendProduct.pid = ?1 ")
	List<CompetitorProfile> findCompetitorsByPriceTrendProductPid(String priceTrendProductPid);

	void deleteByPriceTrendProductPid(String priceTrendProductPid);

	@Query("select priceTrendProductCompetitor from PriceTrendProductCompetitor priceTrendProductCompetitor where priceTrendProductCompetitor.priceTrendProduct.company.id = ?#{principal.companyId}")
	List<PriceTrendProductCompetitor> findAllByCompanyId();

	@Query("select priceTrendProductCompetitor from PriceTrendProductCompetitor priceTrendProductCompetitor where priceTrendProductCompetitor.priceTrendProduct.company.id = ?#{principal.companyId} and (priceTrendProductCompetitor.lastModifiedDate > ?1) or (priceTrendProductCompetitor.priceTrendProduct.lastModifiedDate > ?1)")
	List<PriceTrendProductCompetitor> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate);

}
