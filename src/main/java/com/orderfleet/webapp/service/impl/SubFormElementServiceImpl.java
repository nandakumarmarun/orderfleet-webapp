package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.domain.SubFormElement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SubFormElementRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SubFormElementService;
import com.orderfleet.webapp.web.rest.dto.SubFormElementDTO;

@Service
public class SubFormElementServiceImpl implements SubFormElementService {

	@Inject
	SubFormElementRepository subFormElementRepository;
	
	@Inject
	CompanyRepository companyRepository;
	
	@Override
	public void saveSubFormElements(Document document, Form form, FormElement formElement,
			FormElementValue formElementValue, List<FormElement> subFormElements) {
		List<SubFormElement> subFormElementList = new ArrayList<>();
		Optional<Company> company = companyRepository.findById(SecurityUtils.getCurrentUsersCompanyId());
		for(FormElement formElemt : subFormElements) {
			SubFormElement subFormElement = new SubFormElement();
			
			subFormElement.setCompany(company.get());
			subFormElement.setDocument(document);
			subFormElement.setForm(form);
			subFormElement.setFormElement(formElement);
			subFormElement.setFormElementValue(formElementValue);
			subFormElement.setSubFormElement(formElemt);
			subFormElementList.add(subFormElement);
		}
		
		subFormElementRepository.save(subFormElementList);
	}

	@Override
	public List<String> assignedSubFormElements(Long companyId ,String documentPid, String formPid, String formElement,
			long formElementValueId) {
		return subFormElementRepository.findAllAssignedAccounts(companyId, documentPid, formPid, formElement, formElementValueId);
	}

	@Override
	public List<SubFormElementDTO> findAllSubFormElementByCompany() {
		List<SubFormElement> subFormElement = subFormElementRepository.findAllByCompanyId();
		List<SubFormElementDTO> subFormElementDto = subFormElement.stream()
												.map(sub -> new SubFormElementDTO(sub)).collect(Collectors.toList());
		return subFormElementDto;
	}

	@Transactional
	@Override
	public void deleteAllAssociatedFormElements(Document document, Form form, FormElement formElement,
			FormElementValue formelementValue) {
		subFormElementRepository.deleteAllAssociatedFormElements(
				document.getId(), form.getId(), formElement.getId(), formelementValue.getId());
		
	}
	
}
