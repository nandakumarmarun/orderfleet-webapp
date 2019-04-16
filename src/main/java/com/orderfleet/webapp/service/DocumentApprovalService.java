package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.DocumentApproval;
import com.orderfleet.webapp.web.rest.dto.DocumentApprovalDTO;

/**
 * Service Interface for managing DocumentApproval.
 * 
 * @author Muhammed Riyas T
 * @since November 23, 2016
 */
public interface DocumentApprovalService {

	String PID_PREFIX = "DOCA-";

	void updateDocumentApproval(DocumentApproval documentApproval);

	/**
	 * Get the documentApproval by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	DocumentApproval findOneByPid(String pid);

	List<DocumentApprovalDTO> findDocumentApprovalsByCurrentUser();

}
