package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentProductCategory;
import com.orderfleet.webapp.domain.ProductCategory;

/**
 * Spring Data JPA repository for the DocumentProductCategoryHistory entity.
 * 
 * @author Sarath
 * @since July 08 2016
 */

public interface DocumentProductCategoryRepository extends JpaRepository<DocumentProductCategory, Long> {

	@Query("select documentProductCategory.productCategory from DocumentProductCategory documentProductCategory where documentProductCategory.document.pid = ?1 ")
	List<ProductCategory> findProductCategoriesByDocumentPid(String documentPid);

	void deleteByDocumentPid(String documentPid);

	List<DocumentProductCategory> findByDocumentIn(List<Document> documents);

	List<DocumentProductCategory> findAllByCompanyPid(String companyPid);

	@Query("select documentProductCategory from DocumentProductCategory documentProductCategory where documentProductCategory.company.id = ?#{principal.companyId} and  documentProductCategory.document in ?1 and (documentProductCategory.lastModifiedDate > ?2) or (documentProductCategory.document.lastModifiedDate > ?2)")
	List<DocumentProductCategory> findByDocumentInAndLastModifiedandLastModifiedDate(List<Document> documents,LocalDateTime lastModifiedDate);
	
	void deleteByCompanyId(Long companyId);
}
