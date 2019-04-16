package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.ReferenceDocument;

/**
 * Spring Data JPA repository for the ReferenceDocument entity.
 *
 * @author Sarath
 * @since Oct 19, 2016
 */
public interface ReferenceDocumentRepository extends JpaRepository<ReferenceDocument, Long> {

	@Query("select referenceDocument.referenceDocument from ReferenceDocument referenceDocument where referenceDocument.document.pid = ?1 ")
	List<Document> findReferenceDocumentsByDocumentPid(String documentPid);
	
	@Query("select referenceDocument.referenceDocument.id from ReferenceDocument referenceDocument where referenceDocument.document.pid = ?1 ")
	Set<Long> findReferenceDocumentIdByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	List<ReferenceDocument> findByDocumentIn(List<Document> documents);

	List<ReferenceDocument> findAllByCompanyPid(String companyPid);
	
	List<ReferenceDocument> findAllByCompanyId(Long companyId);
}
