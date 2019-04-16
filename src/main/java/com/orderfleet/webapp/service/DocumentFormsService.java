package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;

/**
 * Spring Data JPA repository for the DocumentForms entity.
 * 
 * @author Sarath
 * @since July 22, 2016
 */
public interface DocumentFormsService {

	void save(List<DocumentFormDTO> documentForms);

	List<FormDTO> findFormsByDocumentPid(String documentPid);

	List<DocumentForms> findAllByCompany();

	Map<FormDTO, List<FormElementDTO>> findFormFormElementByDocumentPid(String documentPid);

	List<DocumentForms> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);

	List<DocumentFormDTO> findByDocumentPid(String documentPid);
}
