package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.BestPerformanceConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.DocumentType;

/**
 * Spring Data JPA repository for the BestPerformanceConfiguration entity.
 *
 * @author Sarath
 * @since Mar 27, 2018
 *
 */
public interface BestPerformanceConfigurationRepository extends JpaRepository<BestPerformanceConfiguration, Long> {

	@Query("select bpc from BestPerformanceConfiguration bpc where bpc.company.id = ?#{principal.companyId} and bpc.documentType = ?1")
	List<BestPerformanceConfiguration> findByCompanyIdAndDocumentType(DocumentType documentType);

	@Query("select bpc from BestPerformanceConfiguration bpc where bpc.company.id = ?#{principal.companyId}")
	List<BestPerformanceConfiguration> findByCompanyId();

	@Query("select bpc.document from BestPerformanceConfiguration bpc where bpc.company.id = ?#{principal.companyId} and bpc.bestPerformanceType = ?1")
	List<Document> findAllDocumentsByBestPerformanceType(BestPerformanceType bestPerformanceType);

	@Query("select bpc from BestPerformanceConfiguration bpc where bpc.company.id = ?#{principal.companyId} and bpc.bestPerformanceType = ?1")
	List<BestPerformanceConfiguration> findAllByBestPerformanceType(BestPerformanceType bestPerformanceType);
}
