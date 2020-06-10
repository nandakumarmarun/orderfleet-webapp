package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentEcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroup;

/**
 * repository for DocumentProductGroup
 * 
 * @author Anish
 * @since June 10, 2020
 */
public interface DocumentEcomProductGroupRepository extends JpaRepository<DocumentEcomProductGroup, Long> {

	@Query("select documentProductGroup.productGroup from DocumentEcomProductGroup documentProductGroup where documentProductGroup.document.pid = ?1 ")
	List<EcomProductGroup> findProductGroupsByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	List<DocumentEcomProductGroup> findByDocumentIn(List<Document> documents);
	
	@Query("select documentProductGroup.productGroup from DocumentEcomProductGroup documentProductGroup where documentProductGroup.document in ?1 ")
	List<EcomProductGroup> findProductGroupsByDocumentIn(List<Document> documents);

	List<DocumentEcomProductGroup> findAllByCompanyPid(String companyPid);

	List<DocumentEcomProductGroup> findByDocumentPid(String documentPid);

	@Query("select documentProductGroup from DocumentEcomProductGroup documentProductGroup where documentProductGroup.company.id = ?#{principal.companyId} and  documentProductGroup.document in ?1 and (documentProductGroup.lastModifiedDate > ?2) or (documentProductGroup.document.lastModifiedDate > ?2)")
	List<DocumentEcomProductGroup> findByDocumentInAndlastModifiedDate(List<Document> documents,LocalDateTime lastModifiedDate);
	
	void deleteByCompanyId(Long companyId);
}
