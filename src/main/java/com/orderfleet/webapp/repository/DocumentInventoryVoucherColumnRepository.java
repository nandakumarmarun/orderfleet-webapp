package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.InventoryVoucherColumn;

/**
 * repository for DocumentInventoryVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 */
public interface DocumentInventoryVoucherColumnRepository extends JpaRepository<DocumentInventoryVoucherColumn, Long> {

	@Query("select documentInventoryVoucherColumn.inventoryVoucherColumn from DocumentInventoryVoucherColumn documentInventoryVoucherColumn where documentInventoryVoucherColumn.document.pid = ?1 ")
	List<InventoryVoucherColumn> findInventoryVoucherColumnsByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	@Query("select documentInventoryVoucherColumn from DocumentInventoryVoucherColumn documentInventoryVoucherColumn where documentInventoryVoucherColumn.company.id = ?#{principal.companyId}")
	List<DocumentInventoryVoucherColumn> findAllByCompanyId();

	List<DocumentInventoryVoucherColumn> findByDocumentPid(String documentPid);

	List<DocumentInventoryVoucherColumn> findAllByCompanyPid(String companyPid);

	@Query("select documentInventoryVoucherColumn from DocumentInventoryVoucherColumn documentInventoryVoucherColumn where documentInventoryVoucherColumn.company.id = ?#{principal.companyId} and documentInventoryVoucherColumn.lastModifiedDate > ?1")
	List<DocumentInventoryVoucherColumn> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	@Query("select documentInventoryVoucherColumn from DocumentInventoryVoucherColumn documentInventoryVoucherColumn where documentInventoryVoucherColumn.company.id = ?#{principal.companyId} and documentInventoryVoucherColumn.document.pid = ?1")
	List<DocumentInventoryVoucherColumn> findAllByCompanyIdAndDocumentPid(String documentPid);
}
