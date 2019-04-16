package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.FeedbackGroupFormElement;
import com.orderfleet.webapp.domain.enums.FeedbackElementType;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;

/**
 * Service Interface for managing FeedbackGroupFormElement.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
public interface FeedbackGroupFormElementService {

	/**
	 * Save a feedbackGroup FormElements.
	 * 
	 */
	void save(String feedbackGroupPid, String formElements, FeedbackElementType feedbackElementType);

	void save(String feedbackGroupPid, String users);
	
	void saveStatusField(String feedbackGroupPid, String statusFieldPid);

	/**
	 * Get all the feedbackGroup FormElements.
	 * 
	 * @return the list of entities
	 */
	List<FormElementDTO> findFormElementsByGroupPidAnd(String feedbackGroupPid,
			FeedbackElementType feedbackElementType);

	FeedbackGroupFormElement findByFormElementPid(String formElementPid);

}
