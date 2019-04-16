package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentStockLocationDestination;
import com.orderfleet.webapp.domain.StockLocation;

public interface DocumentStockLocationDestinationRepository
		extends JpaRepository<DocumentStockLocationDestination, Long> {

	@Query("select documentStockLocationDestination.stockLocation from DocumentStockLocationDestination documentStockLocationDestination where documentStockLocationDestination.document.pid = ?1 ")
	List<StockLocation> findStockLocationByDocumentPid(String documentPid);

	List<DocumentStockLocationDestination> findByDocumentPid(String documentPid);

	List<DocumentStockLocationDestination> findByDocumentIn(List<Document> documents);

	void deleteByDocumentPid(String documentPid);

	DocumentStockLocationDestination findByDocumentPidAndIsDefaultTrue(String documentPid);

	@Query("select documentStockLocationDestination from DocumentStockLocationDestination documentStockLocationDestination where documentStockLocationDestination.company.id = ?#{principal.companyId}")
	List<DocumentStockLocationDestination> findAllByCompanyId();
	
	List<DocumentStockLocationDestination> findAllByCompanyPid(String companyPid);
	
	@Query("select documentStockLocationDestination from DocumentStockLocationDestination documentStockLocationDestination where documentStockLocationDestination.company.id = ?#{principal.companyId} and documentStockLocationDestination.lastModifiedDate > ?1")
	List<DocumentStockLocationDestination> findAllByCompanyAndLastModifiedDate(LocalDateTime lastModifiedDate);
}
