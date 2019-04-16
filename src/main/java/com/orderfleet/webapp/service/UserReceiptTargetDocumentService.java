package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Interface for managing UserReceiptTargetDocument.
 * 
 * @author Sarath
 * @since Sep 3, 2016
 */
public interface UserReceiptTargetDocumentService {
	
	String PID_PREFIX = "URTDOC-";

	List<DocumentDTO> findReceiptTargetDocumentsByReceiptTargetPid(String receiptTargetPid);

	void save(String userReceiptTargetId, String assignedDocuments);
}
