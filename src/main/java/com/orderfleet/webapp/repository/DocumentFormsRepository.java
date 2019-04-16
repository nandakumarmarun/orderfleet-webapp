package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.domain.Form;

/**
 * repository for DocumentForms
 * 
 * @author Sarath
 * @since July 22, 2016
 */
public interface DocumentFormsRepository extends JpaRepository<DocumentForms, Long> {

	@Query("select documentForms.form from DocumentForms documentForms where documentForms.document.pid = ?1 ")
	List<Form> findFormsByDocumentPid(String documentPid);

	@Query("select documentForms from DocumentForms documentForms where documentForms.document.pid = ?1 ")
	List<DocumentForms> findByDocumentPid(String documentPid);

	@Query("select documentForm from DocumentForms documentForm where documentForm.form.company.id = ?#{principal.companyId}")
	List<DocumentForms> findAllByCompanyId();

	void deleteByDocumentPid(String documentPid);

	List<DocumentForms> findAllByDocumentCompanyPid(String companyPid);
	
	@Query("select documentForm from DocumentForms documentForm where documentForm.form.company.id = ?#{principal.companyId} and documentForm.lastModifiedDate > ?1")
	List<DocumentForms> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);
}
