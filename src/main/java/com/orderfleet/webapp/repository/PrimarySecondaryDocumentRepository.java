package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.enums.VoucherType;

/**
 * Spring Data JPA repository for the PrimarySecondaryDocument entity.
 * 
 * @author Shaheer
 * @since December 31, 2016
 */
public interface PrimarySecondaryDocumentRepository extends JpaRepository<PrimarySecondaryDocument, Long> {

	@Query("select psd from PrimarySecondaryDocument psd where psd.voucherType = ?1 and psd.company.id = ?#{principal.companyId}")
	Page<PrimarySecondaryDocument> findByVoucherTypeAndCompanyId(VoucherType voucherType, Pageable pageable);

	@Query("select psd from PrimarySecondaryDocument psd where psd.voucherType = ?1 and psd.company.id = ?#{principal.companyId}")
	List<PrimarySecondaryDocument> findByVoucherTypeAndCompanyId(VoucherType voucherType);
	
	@Query("select psd from PrimarySecondaryDocument psd where psd.company.id = ?#{principal.companyId} and psd.activated=true")
	Page<PrimarySecondaryDocument> findByCompanyIdAndActivedTrue(Pageable pageable);

	@Query("select psd from PrimarySecondaryDocument psd where psd.company.id = ?#{principal.companyId} and psd.activated=true and psd.voucherType = ?1")
	List<PrimarySecondaryDocument> findByCompanyIdAndActivedTrueAndVoucherType(VoucherType voucherType);

	@Query("select psd.document from PrimarySecondaryDocument psd where psd.company.id = ?#{principal.companyId}")
	List<Document> findAllDocumentsByCompanyId();

	@Query("select psd.document from PrimarySecondaryDocument psd where psd.voucherType = ?1 and psd.company.id = ?#{principal.companyId}")
	List<Document> findDocumentsByVoucherTypeAndCompanyId(VoucherType voucherType);

	@Query("select psd.document from PrimarySecondaryDocument psd where psd.company.id = ?#{principal.companyId} and psd.document.pid = ?1 ")
	Document findByDocumentPid(String documentId);
	
	@Query("select distinct  psd.voucherType from PrimarySecondaryDocument psd where psd.company.id = ?#{principal.companyId} order by psd.voucherType asc")
	List<VoucherType> findAllVoucherTypesByCompanyId();

	void deleteByVoucherTypeAndCompanyPid(VoucherType voucherType, String companyPid);
	
	void deleteByCompanyPid(String companyPid);
	
	@Query("select psd from PrimarySecondaryDocument psd where psd.company.id = ?#{principal.companyId} and psd.activated=true")
	List<PrimarySecondaryDocument> findByCompanyIdAndActivedTrue();
	
	@Query("select psd from PrimarySecondaryDocument psd where psd.voucherType = ?1 and psd.company.id = ?2")
	List<PrimarySecondaryDocument> findByVoucherTypeAndCompany(VoucherType voucherType, Long companyId);
	
	@Query("select psd from PrimarySecondaryDocument psd where psd.voucherType in ?1 and psd.company.id = ?2")
	List<PrimarySecondaryDocument> findByVoucherTypesAndCompany(List<VoucherType> voucherTypes, Long companyId);

}
