package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.KnowledgeBaseFilesDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseFileDTO;

/**
 * Service Interface for managing UserknowledgeBaseFiles.
 * 
 * @author Muhammed Riyas T
 * @since October 05, 2016
 */
public interface UserKnowledgebaseFileService {

	List<KnowledgebaseFileDTO> findKnowledgebaseFilesByUserIsCurrentUser();

	List<KnowledgebaseFileDTO> findKnowledgebaseFilesByUserPid(String userPid);

	List<UserDTO> findUsersByKnowledgebaseFilePid(String knowledgeBaseFilePid);

	List<KnowledgeBaseFilesDTO> findAllByUserIsCurrentUser();

}
