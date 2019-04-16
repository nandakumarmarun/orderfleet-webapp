package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ReceiptDocument;

/**
 * Spring Data JPA repository for the ReceiptDocument entity.
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */
public interface ReceiptDocumentRepository extends JpaRepository<ReceiptDocument, Long> {

	@Query("select receiptDocument from ReceiptDocument receiptDocument where receiptDocument.company.id = ?#{principal.companyId}")
	Page<ReceiptDocument> findAllByCompanyId(Pageable pageable);
	
	@Query("select receiptDocument from ReceiptDocument receiptDocument where receiptDocument.company.id = ?#{principal.companyId} and receiptDocument.activated=true")
	Page<ReceiptDocument> findAllByCompanyIdAndActivedTrue(Pageable pageable);

	@Query("select receiptDocument from ReceiptDocument receiptDocument where receiptDocument.company.id = ?#{principal.companyId}")
	List<ReceiptDocument> findAllByCompanyId();

	@Query("select receiptDocument.document from ReceiptDocument receiptDocument where receiptDocument.company.id = ?#{principal.companyId}")
	List<Document> findAllDocumentsByCompanyId();

	@Query("select receiptDocument.document from ReceiptDocument receiptDocument where receiptDocument.company.id = ?#{principal.companyId} and receiptDocument.document.pid = ?1 ")
	Document findByDocumentPid(String documentId);
	
	void deleteByCompanyPid(String companyPid);
	
	@Query("select receiptDocument from ReceiptDocument receiptDocument where receiptDocument.company.id = ?#{principal.companyId} and receiptDocument.activated=true")
	List<ReceiptDocument> findAllByCompanyIdAndActivedTrue();
}
