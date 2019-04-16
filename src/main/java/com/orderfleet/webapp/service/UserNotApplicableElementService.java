package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.UserNotApplicableElementDTO;

/**
 * Service Interface for managing UserNotApplicableElement.
 * 
 * @author Muhammed Riyas T
 * @since Jan 18, 2017
 */
public interface UserNotApplicableElementService {

	/**
	 * Save a userNotApplicableElement.
	 * 
	 * @param userNotApplicableElementDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(String documentPid, List<UserNotApplicableElementDTO> userNotApplicableElementDTOs);

	/**
	 * Get all the userNotApplicableElements.
	 * 
	 * @return the list of entities
	 */
	List<UserNotApplicableElementDTO> findAllByCompany();

	List<UserDTO> findUsersByDocumentAndFormAndFormElement(String documentPid, String formPid, String formElementPid);

	List<UserNotApplicableElementDTO> findByUserIsCurrentUser();

}
