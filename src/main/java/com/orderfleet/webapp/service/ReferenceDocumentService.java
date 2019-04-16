package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Service Interface for managing ReferenceDocumentService.
 *
 * @author Sarath
 * @since Oct 19, 2016
 */
public interface ReferenceDocumentService {

	void saveReferenceDocument(String documentPid, String assignedReferenceDocuments);

	List<DocumentDTO> findReferenceDocumentsByDocumentPid(String documentPid);

}
