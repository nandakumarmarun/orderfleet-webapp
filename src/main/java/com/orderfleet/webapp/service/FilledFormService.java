package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;

/**
 * Service Interface for managing FilledForm.
 * 
 * @author Muhammed Riyas T
 * @since August 06, 2016
 */
public interface FilledFormService {

	String PID_PREFIX = "FLFRM-";

	/**
	 * Get the "id" filledForm.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	FilledFormDTO findOne(Long id);

	/**
	 * Get the filledForm by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<FilledFormDTO> findOneByPid(String pid);

	List<FilledFormDTO> findByDynamicDocumentHeaderDocumentPid(String documentHeaderpid);

}
