package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentEcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;


/**
 * Service Interface for managing DocumentProductGroupService.
 * 
 * @author Anish
 * @since July 10 2020
 *
 */
public interface DocumentEcomProductGroupService {

	void save(List<DocumentEcomProductGroupDTO> documentProductGroupDTOs);

	List<EcomProductGroupDTO> findProductGroupsByDocumentPid(String documentPid);

	List<DocumentEcomProductGroupDTO> findByUserDocumentIsCurrentUser();

	List<DocumentEcomProductGroupDTO> findByDocumentPid(String documentPid);

	List<DocumentEcomProductGroupDTO> findByUserDocumentIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	
	
}
