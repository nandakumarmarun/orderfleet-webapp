package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.AccountingVoucherColumn;

/**
 * repository for DocumentAccountingVoucherColumn
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 */
public interface DocumentAccountingVoucherColumnRepository
		extends JpaRepository<DocumentAccountingVoucherColumn, Long> {

	@Query("select documentAccountingVoucherColumn.accountingVoucherColumn from DocumentAccountingVoucherColumn documentAccountingVoucherColumn where documentAccountingVoucherColumn.document.pid = ?1 ")
	List<AccountingVoucherColumn> findAccountingVoucherColumnsByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	@Query("select documentAccountingVoucherColumn from DocumentAccountingVoucherColumn documentAccountingVoucherColumn where documentAccountingVoucherColumn.company.id = ?#{principal.companyId}")
	List<DocumentAccountingVoucherColumn> findAllByCompanyId();

	List<DocumentAccountingVoucherColumn> findByDocumentPid(String documentPid);
	
	@Query("select documentAccountingVoucherColumn from DocumentAccountingVoucherColumn documentAccountingVoucherColumn where documentAccountingVoucherColumn.company.id = ?#{principal.companyId} and documentAccountingVoucherColumn.lastModifiedDate > ?1")
	List<DocumentAccountingVoucherColumn> findByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	@Query("select documentAccountingVoucherColumn from DocumentAccountingVoucherColumn documentAccountingVoucherColumn where documentAccountingVoucherColumn.company.id = ?#{principal.companyId} and documentAccountingVoucherColumn.document.pid = ?1")
	List<DocumentAccountingVoucherColumn> findByCompanyIdAndDocumentPid(String documentPid);
}
