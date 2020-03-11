package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.web.rest.dto.SubFormElementDTO;

public interface SubFormElementService {

	void saveSubFormElements(Document document,Form form ,FormElement formElement ,
									FormElementValue formelementValue, List<FormElement> subFormElements);
	List<String> assignedSubFormElements(Long companyId, String documentPid,String formPid,String formElement,long formElementValueId);
	
	List<SubFormElementDTO> findAllSubFormElementByCompany();
	
	void deleteAllAssociatedFormElements(Document document,Form form ,FormElement formElement ,FormElementValue formelementValue);
}
