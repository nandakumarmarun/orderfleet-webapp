package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.StockLocation;

public interface DocumentStockLocationSourceRepository extends JpaRepository<DocumentStockLocationSource, Long> {

	@Query("select documentStockLocationSource.stockLocation from DocumentStockLocationSource documentStockLocationSource where documentStockLocationSource.document.pid = ?1 ")
	List<StockLocation> findStockLocationByDocumentPid(String documentPid);
	
	@Query("select documentStockLocationSource.stockLocation from DocumentStockLocationSource documentStockLocationSource where documentStockLocationSource.document.pid = ?1 and documentStockLocationSource.stockLocation in ?2")
	List<StockLocation> findStockLocationByDocumentPidAndStockLocationIn(String documentPid, List<StockLocation> stockLocations);

	List<DocumentStockLocationSource> findByDocumentIn(List<Document> documents);

	List<DocumentStockLocationSource> findByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	DocumentStockLocationSource findByDocumentPidAndIsDefaultTrue(String documentPid);

	@Query("select documentStockLocationSource from DocumentStockLocationSource documentStockLocationSource where documentStockLocationSource.company.id = ?#{principal.companyId}")
	List<DocumentStockLocationSource> findAllByCompanyId();
	
	List<DocumentStockLocationSource> findAllByCompanyPid(String companyPid);

	@Query("select documentStockLocationSource from DocumentStockLocationSource documentStockLocationSource where documentStockLocationSource.company.id = ?#{principal.companyId} and documentStockLocationSource.lastModifiedDate>?1")
	List<DocumentStockLocationSource> findAllByCompanyIdAndlastModifiedDate(LocalDateTime lastModifiedDate);

	@Query("select docStockLoc.stockLocation.id from DocumentStockLocationSource docStockLoc where docStockLoc.company.id = ?#{principal.companyId}")
	Set<Long> findStockLocationIdsByCompanyId();
	
	List<String> findPidByCompanyId(Long id);
}
