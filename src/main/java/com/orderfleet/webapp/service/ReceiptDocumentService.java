package com.orderfleet.webapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ReceiptDocument;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Interface for managing ReceiptDocument.
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */
public interface ReceiptDocumentService {

	void save(String assignedDocuments);

	Page<ReceiptDocument> findAllByCompany(Pageable pageable);

	List<DocumentDTO> findAllDocumentsByCompanyId();
	
	Page<ReceiptDocument> findAllByCompanyIdAndActivedTrue(Pageable pageable);
	
	List<ReceiptDocument> findAllByCompanyIdAndActivedTrue();
}
