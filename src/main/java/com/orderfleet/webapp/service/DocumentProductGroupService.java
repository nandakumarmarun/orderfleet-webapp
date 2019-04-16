package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.DocumentProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Service Interface for managing DocumentProductGroupService.
 * 
 * @author Sarath
 * @since July 07 2016
 *
 */
public interface DocumentProductGroupService {

	void save(List<DocumentProductGroupDTO> documentProductGroupDTOs);

	List<ProductGroupDTO> findProductGroupsByDocumentPid(String documentPid);

	List<DocumentProductGroupDTO> findByUserDocumentIsCurrentUser();

	List<DocumentProductGroupDTO> findByDocumentPid(String documentPid);

	List<DocumentProductGroupDTO> findByUserDocumentIsCurrentUserAndLastModifiedDate(LocalDateTime lastModifiedDate);
	
	
	
}
