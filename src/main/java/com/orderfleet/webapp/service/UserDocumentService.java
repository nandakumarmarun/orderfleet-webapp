package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserDocumentDTO;

/**
 * Service Interface for managing UserDocument.
 * 
 * @author Sarath
 * @since July 5, 2016
 */
public interface UserDocumentService {

	void save(String pid, List<UserDocumentDTO> assignedDocuments);

	List<DocumentDTO> findDocumentsByUserIsCurrentUser();

	List<DocumentDTO> findDocumentsByUserPid(String userPid);

	List<DocumentDTO> findDocumentsByUserAndDocumentType(String userPid, DocumentType documentType);
	
	List<DocumentDTO> findDocumentsByUserIdsAndDocumentType(List<Long> userIds, DocumentType documentType);

	void copyDocuments(String fromUserPid, List<String> toUserPids);

	List<UserDocumentDTO> findByUserPid(String userPid);

	List<UserDocumentDTO> findByUserIsCurrentUserAndDocumentPid(String documentPid);
}
