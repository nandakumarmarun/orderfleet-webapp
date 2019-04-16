package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementTransactionDTO;

/**
 * Service Implementation for managing FormFormElement.
 * 
 * @author Muhammed Riyas T
 * @since July 22, 2016
 */
@Service
@Transactional
public class FormFormElementServiceImpl implements FormFormElementService {

	private final Logger log = LoggerFactory.getLogger(FormFormElementServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Override
	public List<FormFormElement> findByFormPid(String pid) {
		log.info("find Form FormElements by form pid");
		return formFormElementRepository.findByFormPid(pid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FormFormElementDTO> findAllByCompany() {
		log.info("find Form FormElements by company id");
		List<FormFormElement> formFormElements = formFormElementRepository.findAllByCompanyId();
		return formFormElements.stream().map(FormFormElementDTO::new).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Map<FormDTO, List<FormFormElementTransactionDTO>> findFormFormElementByFormsGrouped(List<Form> formList) {
		log.info("find Form FormElements by company id and list of forms");
		List<FormFormElement> formFormElements = formFormElementRepository.findByCompanyIdAndFormsIn(formList);
		// group by form
		return formFormElements.stream().collect(Collectors.groupingBy(ffe -> new FormDTO(ffe.getForm()),
				Collectors.mapping(FormFormElementTransactionDTO::new, Collectors.toList())));
	}

	@Override
	@Transactional(readOnly = true)
	public Map<FormDTO, List<FormFormElementTransactionDTO>> getFormFormElementBySortedFormsGrouped(
			List<UserForm> sortedUserForms) {
		List<Form> formList = sortedUserForms.stream().map(UserForm::getForm).collect(Collectors.toList());
		List<FormFormElement> formFormElements = formFormElementRepository.findByCompanyIdAndFormsIn(formList);
		// group by form
		Map<FormDTO, List<FormFormElementTransactionDTO>> mapFormFormElement = formFormElements.stream()
				.collect(Collectors.groupingBy(ffe -> new FormDTO(ffe.getForm()),
						Collectors.mapping(FormFormElementTransactionDTO::new, Collectors.toList())));
		Map<FormDTO, List<FormFormElementTransactionDTO>> sortedFormFormElement = new LinkedHashMap<>();
		for (UserForm userForm : sortedUserForms) {
			// convert form to form dto
			FormDTO fromDto = new FormDTO(userForm.getForm());
			if (mapFormFormElement.containsKey(fromDto)) {
				sortedFormFormElement.put(fromDto, mapFormFormElement.get(fromDto));
			}
		}
		return sortedFormFormElement;
	}

	@Override
	public FormFormElement save(String formPid, String formElementPid) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<Form> optionalForm = formRepository.findOneByPid(formPid);
		if (optionalForm.isPresent()) {
			FormElement formElement = formElementRepository.findOneByPid(formElementPid).get();
			FormFormElement formFormElement = new FormFormElement();
			formFormElement.setForm(optionalForm.get());
			formFormElement.setFormElement(formElement);
			formFormElement.setCompany(company);
			return formFormElementRepository.save(formFormElement);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FormFormElementDTO> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		log.info("find Form FormElements by company id");
		List<FormFormElement> formFormElements = formFormElementRepository
				.findAllByCompanyIdAndLastModifiedDate(lastModifiedDate);
		return formFormElements.stream().map(FormFormElementDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<FormFormElementDTO> findByFormPidIn(List<String> formPids) {
		log.info("find Form FormElements by company id and list of form pids");
		List<FormFormElement> formFormElements = formFormElementRepository.findByFormPidIn(formPids);
		return formFormElements.stream().map(FormFormElementDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<FormFormElementDTO> findByFormElementPid(String formElementPid) {
		List<FormFormElement> formFormElements = formFormElementRepository.findByFormElementPid(formElementPid);
		return formFormElements.stream().map(FormFormElementDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<FormFormElementDTO> findOneByFormPid(String formPid) {
		log.info("find Form FormElements by form formPid");
		List<FormFormElement> formFormElements = formFormElementRepository.findByFormPid(formPid);
		return formFormElements.stream().map(FormFormElementDTO::new).collect(Collectors.toList());
	}
}
