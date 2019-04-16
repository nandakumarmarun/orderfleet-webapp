package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementTransactionDTO;

/**
 * Service Interface for managing FormFormElement.
 * 
 * @author Muhammed Riyas T
 * @since July 22, 2016
 */
public interface FormFormElementService {
	
	List<FormFormElement> findByFormPid(String pid);

	List<FormFormElementDTO> findAllByCompany();
	
	Map<FormDTO, List<FormFormElementTransactionDTO>> findFormFormElementByFormsGrouped(List<Form> formList);
	
	Map<FormDTO, List<FormFormElementTransactionDTO>> getFormFormElementBySortedFormsGrouped(List<UserForm> userForms);
	
	FormFormElement save(String formPid, String formElementPid);

	List<FormFormElementDTO> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate);

	List<FormFormElementDTO> findByFormPidIn(List<String> formPids);
	
	List<FormFormElementDTO> findByFormElementPid(String formElementPid);

	List<FormFormElementDTO> findOneByFormPid(String formPid);
}
