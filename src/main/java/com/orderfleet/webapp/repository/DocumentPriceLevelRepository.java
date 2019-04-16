package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPriceLevel;
import com.orderfleet.webapp.domain.PriceLevel;

/**
 * repository for DocumentPriceLevel
 * 
 * @author Muhammed Riyas T
 * @since August 30, 2016
 */
public interface DocumentPriceLevelRepository extends JpaRepository<DocumentPriceLevel, Long> {

	@Query("select documentPriceLevel.priceLevel from DocumentPriceLevel documentPriceLevel where documentPriceLevel.document.pid = ?1 ")
	List<PriceLevel> findPriceLevelByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	@Query("select documentPriceLevel from DocumentPriceLevel documentPriceLevel where documentPriceLevel.priceLevel.company.id = ?#{principal.companyId}")
	List<DocumentPriceLevel> findAllByCompanyId();
	
	List<DocumentPriceLevel> findByDocumentIn(List<Document> documents);
	
	@Query("select documentPriceLevel from DocumentPriceLevel documentPriceLevel where documentPriceLevel.priceLevel.company.id = ?#{principal.companyId} and documentPriceLevel.document in ?1 and (documentPriceLevel.lastModifiedDate > ?2) or (documentPriceLevel.priceLevel.lastModifiedDate > ?2) or (documentPriceLevel.document.lastModifiedDate > ?2)")
	List<DocumentPriceLevel> findByDocumentInAndLastModifiedDate(List<Document> documents, LocalDateTime lastModifiedDate);
	
	void deleteByCompanyId(Long companyId);
}
