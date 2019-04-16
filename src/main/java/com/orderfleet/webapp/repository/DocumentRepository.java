package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.DocumentType;

/**
 * Spring Data JPA repository for the Document entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

	Optional<Document> findOneByPid(String pid);

	Optional<Document> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Document> findByCompanyIdAndDocumentPrefixIgnoreCase(Long id, String documentPrefix);

	@Query("select document from Document document where document.company.id = ?#{principal.companyId}")
	List<Document> findAllByCompanyId();

	@Query("select document from Document document where document.company.id = ?#{principal.companyId}")
	Page<Document> findAllByCompanyId(Pageable pageable);

	@Query("select document from Document document where document.documentType = ?1 and document.company.id = ?#{principal.companyId}")
	Page<Document> findAllByCompanyIdAndDocumentType(Pageable pageable, DocumentType documentType);

	@Query("select document from Document document where document.documentType = ?1 and document.company.id = ?#{principal.companyId}")
	List<Document> findAllDocumentsByDocumentType(DocumentType documentType);

	List<Document> findAllDocumentsByCompanyPid(String companyPid);

	@Query("select document from Document document where pid in ?1 and document.company.id = ?#{principal.companyId}")
	List<Document> findOneByPidIn(List<String> pids);
	
	@Query("select document from Document document where document.documentType = ?1 and document.company.pid = ?2")
	List<Document> findAllDocumentsByCompanyPidAndDocumentType(DocumentType documentType,String companyPid);
	
	@Query("select document from Document document where document.documentType = ?1 and document.pid in ?2")
	List<Document> findAllDocumentsByDocumentPidsAndDocumentType(DocumentType documentType,List<String> documentPids);
	
	Optional<Document> findByCompanyPidAndNameIgnoreCase(String companyPd, String name);
}
