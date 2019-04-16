package com.orderfleet.webapp.service;

import com.orderfleet.webapp.web.rest.dto.TaskReferenceDocumentDTO;

/**
 * Service Interface for managing TaskReferenceDocument.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
public interface TaskReferenceDocumentService {

	String PID_PREFIX = "TRDO-";

	/**
	 * Get the "id" taskReferenceDocument.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	TaskReferenceDocumentDTO findOne(Long id);

}
