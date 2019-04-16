package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;

/**
 * repository for DocumentProductGroup
 * 
 * @author Sarath
 * @since Jul 22, 2016
 */
public interface DocumentProductGroupRepository extends JpaRepository<DocumentProductGroup, Long> {

	@Query("select documentProductGroup.productGroup from DocumentProductGroup documentProductGroup where documentProductGroup.document.pid = ?1 ")
	List<ProductGroup> findProductGroupsByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	List<DocumentProductGroup> findByDocumentIn(List<Document> documents);
	
	@Query("select documentProductGroup.productGroup from DocumentProductGroup documentProductGroup where documentProductGroup.document in ?1 ")
	List<ProductGroup> findProductGroupsByDocumentIn(List<Document> documents);

	List<DocumentProductGroup> findAllByCompanyPid(String companyPid);

	List<DocumentProductGroup> findByDocumentPid(String documentPid);

	@Query("select documentProductGroup from DocumentProductGroup documentProductGroup where documentProductGroup.company.id = ?#{principal.companyId} and  documentProductGroup.document in ?1 and (documentProductGroup.lastModifiedDate > ?2) or (documentProductGroup.document.lastModifiedDate > ?2)")
	List<DocumentProductGroup> findByDocumentInAndlastModifiedDate(List<Document> documents,LocalDateTime lastModifiedDate);
	
	void deleteByCompanyId(Long companyId);
}
