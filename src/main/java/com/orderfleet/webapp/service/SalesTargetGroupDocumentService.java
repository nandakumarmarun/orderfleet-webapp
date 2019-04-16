package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Interface for managing SalesTargetGroupDocument.
 * 
 * @author Sarath
 * @since Oct 14, 2016
 */
public interface SalesTargetGroupDocumentService {

	List<DocumentDTO> findSalesTargetGroupDocumentsBySalesTargetGroupPid(String salesTargetGroupPid);

	void save(String salesTargetGroupPid, String assignedDocuments);
}
