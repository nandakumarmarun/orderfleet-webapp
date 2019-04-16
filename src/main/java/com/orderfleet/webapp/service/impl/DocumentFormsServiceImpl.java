package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.mapper.FormMapper;

/**
 * Service Implementation for managing DocumentForm.
 * 
 * @author Sarath
 * @since July 22, 2016
 */
@Service
@Transactional
public class DocumentFormsServiceImpl implements DocumentFormsService {
	private final Logger log = LoggerFactory.getLogger(DocumentFormsServiceImpl.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private DocumentFormsRepository documentFormRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private FormMapper formMapper;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Override
	public void save(List<DocumentFormDTO> documentFormLists) {
		log.debug("Request to save Document Forms");

		Document document = documentRepository.findOneByPid(documentFormLists.get(0).getDocumentPid()).get();
		
		List<DocumentForms> documentForms = new ArrayList<>();
		for (DocumentFormDTO documentFormDTO : documentFormLists) {
			Form form = formRepository.findOneByPid(documentFormDTO.getFormPid()).get();
			documentForms.add(new DocumentForms(document, form,documentFormDTO.getSortOrder()));
		}
		documentFormRepository.deleteByDocumentPid(document.getPid());
		documentFormRepository.save(documentForms);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FormDTO> findFormsByDocumentPid(String documentPid) {
		log.debug("Request to get all Forms under in a documents");
		List<Form> formList = documentFormRepository.findFormsByDocumentPid(documentPid);
		List<FormDTO> result = formMapper.formsToFormDTOs(formList);
		return result;
	}

	/**
	 * Get all the forms.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentForms> findAllByCompany() {
		log.debug("Request to get all Forms");
		List<DocumentForms> documentForms = documentFormRepository.findAllByCompanyId();
		return documentForms;
	}

	@Override
	public Map<FormDTO, List<FormElementDTO>> findFormFormElementByDocumentPid(String documentPid) {
		List<Form> formList = documentFormRepository.findFormsByDocumentPid(documentPid);
		if (formList.size() > 0) {
			List<FormFormElement> formFormElements = formFormElementRepository.findByCompanyIdAndFormsIn(formList);
			// group by form
			Map<FormDTO, List<FormElementDTO>> formFormElementsGrouped = formFormElements.stream()
					.collect(Collectors.groupingBy(ffe -> new FormDTO(ffe.getForm().getPid(), ffe.getForm().getName()),
							Collectors.mapping((FormFormElement ff) -> new FormElementDTO(ff.getFormElement().getPid(),
									ff.getFormElement().getName()), Collectors.toList())));
			return formFormElementsGrouped;
		}
		return null;
	}
	
	/**
	 * Get all the forms.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentForms> findAllByCompanyIdAndLastModifiedDate(LocalDateTime lastModifiedDate) {
		log.debug("Request to get all Forms");
		List<DocumentForms> documentForms = documentFormRepository.findAllByCompanyIdAndLastModifiedDate(lastModifiedDate);
		return documentForms;
	}

	@Override
	public List<DocumentFormDTO> findByDocumentPid(String documentPid) {
		List<DocumentForms>documentFormsList=documentFormRepository.findByDocumentPid(documentPid);
		List<DocumentFormDTO>documentFormDTOs=new ArrayList<>();
		for(DocumentForms documentForms:documentFormsList){
			DocumentFormDTO documentFormDTO=new DocumentFormDTO(documentForms);
			documentFormDTOs.add(documentFormDTO);
		}
		return documentFormDTOs;
	}
}
