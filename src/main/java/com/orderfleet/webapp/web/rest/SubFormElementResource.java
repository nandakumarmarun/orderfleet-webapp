package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.enums.LoadMobileData;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.FormElementValueRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;

/**
 * Web controller for managing FormElement.
 * 
 * @author Muhammed Riyas T
 * @since June 23, 2016
 */
@Controller
@RequestMapping("/web")
public class SubFormElementResource {

	private final Logger log = LoggerFactory.getLogger(SubFormElementResource.class);

	@Inject
	private FormElementService formElementService;

	@Inject
	private FormElementTypeRepository formElementTypeRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private FormElementValueRepository formElementValueRepository;

	@Inject
	private FormService formService;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/subFormElements", method = RequestMethod.GET)
	public String getAllFormElements(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of FormElements");
		List<FormElementDTO> formElements = formElementService.findAllByCompanyAndDeactivatedFormElement(true);

		List<FormDTO> forms = formService.findAllByCompanyAndDeactivatedForm(true);

		model.addAttribute("forms", forms);

		model.addAttribute("formElements", formElements);
		model.addAttribute("formElementTypes", formElementTypeRepository.findAll());
		model.addAttribute("loadMobileDataList", LoadMobileData.values());
		model.addAttribute("deactivatedFormElements",
				formElementService.findAllByCompanyAndDeactivatedFormElement(false));
		return "company/subFormElements";
	}

	@RequestMapping(value = "/subFormElements/load-form-elements", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementDTO>> getFormElements(@RequestParam long formElementTypeId,
			@RequestParam String formPid) {
		log.debug("Web request to get FormElementType by id : {}", formElementTypeId);

		List<FormElement> formElements = formElementRepository
				.findAllByCompanyIdAndFormElementTypeId(SecurityUtils.getCurrentUsersCompanyId(), formElementTypeId);

		List<FormFormElement> formElementByForms = formFormElementRepository
				.findByFormPidAndReportOrderGreaterThanZero(formPid);

		List<FormElement> filteredFormElements = new ArrayList<>();
		for (FormElement formElement : formElements) {
			Optional<FormFormElement> opFormFormElement = formElementByForms.stream()
					.filter(ffe -> ffe.getFormElement().getPid().equalsIgnoreCase(formElement.getPid())).findAny();

			if (opFormFormElement.isPresent()) {
				filteredFormElements.add(opFormFormElement.get().getFormElement());
			}
		}

		List<FormElementDTO> formElementDtos = convertFormElementtoFormElementDtOs(filteredFormElements);

		return new ResponseEntity<List<FormElementDTO>>(formElementDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/subFormElements/load-all-form-element-values", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementValueDTO>> getFormElementValues(@RequestParam String formElementPid) {
		log.debug("Web request to get FormElement by pid : {}", formElementPid);

		List<FormElementValue> formElementValues = formElementValueRepository.findAllByFormElementPid(formElementPid);

		List<FormElementValueDTO> formElementDtos = convertFormElementValuetoFormElementValueDtOs(formElementValues);

		return new ResponseEntity<List<FormElementValueDTO>>(formElementDtos, HttpStatus.OK);
	}

	@RequestMapping(value = "/subFormElements/formElements", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<FormElementDTO>> formElementValueFormElements(@RequestParam long formElementValueId) {
		log.debug("REST request to form element value Form Elements : {}", formElementValueId);

		FormElementValue formElementValue = formElementValueRepository.findOne(formElementValueId);
		List<FormElement> formElements = formElementValue.getFormElements();

		List<FormElementDTO> formElementDTOs = convertFormElementtoFormElementDtOs(formElements);

		return new ResponseEntity<List<FormElementDTO>>(formElementDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/subFormElements/assign-form-elements", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedFormElements(@RequestParam long formElementValueId,
			@RequestParam String assignedFormElements) {
		log.debug("REST request to save assigned Form Element value : {}", formElementValueId);

		FormElementValue formElementValue = formElementValueRepository.findOne(formElementValueId);

		List<String> formElementPids = new ArrayList<>();

		String[] formElementString = assignedFormElements.split(",");

		for (String pid : formElementString) {
			formElementPids.add(pid);
		}

		List<FormElement> formElements = formElementRepository.findAllByCompanyIdAndFormElementPidIn(formElementPids);

		formElementValue.setFormElements(formElements);

		formElementValueRepository.save(formElementValue);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private List<FormElementDTO> convertFormElementtoFormElementDtOs(List<FormElement> formElements) {
		List<FormElementDTO> formElementDTOs = new ArrayList<>();

		for (FormElement formElement : formElements) {

			FormElementDTO formElementDTO = new FormElementDTO();

			formElementDTO.setPid(formElement.getPid());
			formElementDTO.setName(formElement.getName());
			formElementDTO.setFormElementTypeName(formElement.getFormElementType().getName());
			formElementDTOs.add(formElementDTO);

		}

		return formElementDTOs;
	}

	private List<FormElementValueDTO> convertFormElementValuetoFormElementValueDtOs(
			List<FormElementValue> formElementValues) {
		List<FormElementValueDTO> formElementValueDTOs = new ArrayList<>();

		for (FormElementValue formElementValue : formElementValues) {

			FormElementValueDTO formElementValueDTO = new FormElementValueDTO();

			formElementValueDTO.setId(String.valueOf(formElementValue.getId()));
			formElementValueDTO.setName(formElementValue.getName());

			formElementValueDTOs.add(formElementValueDTO);
		}

		return formElementValueDTOs;
	}

}
