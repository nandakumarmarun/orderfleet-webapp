package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.SubFormElement;
import com.orderfleet.webapp.domain.enums.LoadMobileData;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.FormElementValueRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.SubFormElementService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
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
	private FormRepository formRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;
	
	@Inject
	private DocumentService documentService;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private DocumentFormsService documentFormService;
	
	@Inject
	private SubFormElementService subFormElementService;

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/subFormElements", method = RequestMethod.GET)
	public String getAllFormElements(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of FormElements");
		List<FormElementDTO> formElementDtos = formElementService.findAllByCompanyAndDeactivatedFormElement(true);

		List<FormDTO> formDtos = formService.findAllByCompanyAndDeactivatedForm(true);
		List<DocumentDTO> documentDtos = documentService.findAllByCompany();
		model.addAttribute("forms", formDtos);
		model.addAttribute("documents",documentDtos);
		model.addAttribute("formElements", formElementDtos);
		model.addAttribute("formElementTypes", formElementTypeRepository.findAll());
//		model.addAttribute("loadMobileDataList", LoadMobileData.values());
//		model.addAttribute("deactivatedFormElements",
//				formElementService.findAllByCompanyAndDeactivatedFormElement(false));
		return "company/subFormElements";
	}

	
	@RequestMapping(value = "/subFormElements/load-forms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentFormDTO>> getFormsByDocument(@RequestParam String documentPid){
		List<DocumentFormDTO> documentForms = documentFormService.findByDocumentPid(documentPid);
		log.info("document forms size " +documentForms.size());
		return new ResponseEntity<List<DocumentFormDTO>>(documentForms, HttpStatus.OK);
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
	
	@RequestMapping(value = "/subFormElements/load-form-elements-by-form", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementDTO>> getFormElements(@RequestParam String formPid) {
		log.debug("Web request to get FormElementType by id : {}", formPid);
		List<FormElement> filteredFormElements = new ArrayList<>();
		List<FormFormElement> formElementByForms = formFormElementRepository.findByFormPidAndReportOrderGreaterThanZero(formPid);
		
		filteredFormElements = formElementByForms.stream().map(FormFormElement :: getFormElement).collect(Collectors.toList());

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
	public ResponseEntity<List<FormElementDTO>> formElementValueFormElements(@RequestParam long formElementValueId,
			@RequestParam String documentPid ,@RequestParam String formPid,@RequestParam String formElementPid) {
		log.debug("REST request to form element value Form Elements : {}", formElementValueId);
		Long companyId  = SecurityUtils.getCurrentUsersCompanyId();
		List<String> subFormPids = subFormElementService.assignedSubFormElements(companyId, documentPid, formPid, formElementPid, formElementValueId);
		List<FormElementDTO> formElementDTOs = new ArrayList<>();
		if(!subFormPids.isEmpty()) {
			formElementDTOs = formElementService.findAllByCompanyIdAndFormElementPidIn(subFormPids);
		}
		return new ResponseEntity<List<FormElementDTO>>(formElementDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/subFormElements/assign-form-elements", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedFormElements(@RequestParam long formElementValueId,
			@RequestParam String assignedFormElements,@RequestParam String documentPid ,@RequestParam String formPid,
			@RequestParam String formElementPid) {
		log.debug("REST request to save assigned Form Element value : {}", formElementValueId);
		
		Optional<Document> opDocument = documentRepository.findOneByPid(documentPid);
		FormElementValue formElementValue = formElementValueRepository.findOne(formElementValueId);
		Optional<FormElement> opFormElement = formElementRepository.findOneByPid(formElementPid);
		Optional<Form> opForm = formRepository.findOneByPid(formPid);
		
		List<String> formElementPids = new ArrayList<>();

		String[] formElementString = assignedFormElements.split(",");
		log.info("array size : "+formElementString.length);
		log.info("array  : "+formElementString.toString());
		for (String pid : formElementString) {
			formElementPids.add(pid);
		}
		log.info("list size : "+formElementPids.size());
		log.info("list : "+formElementPids.toString());
		if(formElementPids.size()==1 && "".equals(formElementPids.get(0).trim())) {
			log.info("deleting all related sub form elements");
			subFormElementService.deleteAllAssociatedFormElements(opDocument.get(), opForm.get(), 
					opFormElement.get(), formElementValue);
		}else {
			List<FormElement> subFormElements = formElementRepository.findAllByCompanyIdAndFormElementPidIn(formElementPids);
			log.info("sub form element size "+subFormElements.size());
			if(opDocument.isPresent() && opForm.isPresent() && 
					opFormElement.isPresent() && formElementValueId != 0 && subFormElements.size() >0) {
				log.info("Saving sub form elements");
				subFormElementService.saveSubFormElements(opDocument.get(), opForm.get(), 
													opFormElement.get(), formElementValue, subFormElements);
			}else {
				log.info("Failed to Save sub form elements");
			}
		}

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
