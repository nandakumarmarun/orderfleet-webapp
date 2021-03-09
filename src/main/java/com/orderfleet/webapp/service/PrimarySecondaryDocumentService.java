package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Interface for managing PrimarySecondaryDocumentService.
 * 
 * @author Shaheer
 * @since December 31, 2016
 */
public interface PrimarySecondaryDocumentService {

	void save(String assignedDocuments, VoucherType voucherType);

	Page<PrimarySecondaryDocument> findAllByCompany(VoucherType voucherType, Pageable pageable);

	List<DocumentDTO> findAllDocumentsByCompanyId();
	
	List<DocumentDTO> findAllDocumentsByCompanyIdAndVoucherType(VoucherType voucherType);
	
	List<DocumentDTO> findAllDocumentsByCompanyIdAndVoucherTypeIn(List<VoucherType> voucherTypes);

	Page<PrimarySecondaryDocument> findAllByCompanyIdAndActivedTrue(Pageable pageable);
	
	List<VoucherType> findAllVoucherTypesByCompanyId();
	
	List<PrimarySecondaryDocument> findAllByCompanyIdAndActivedTrue();
}
