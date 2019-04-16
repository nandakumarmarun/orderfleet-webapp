package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;

/**
 * Web controller for managing DocumentForm.
 * 
 * @author Sarath
 * @since July 22 2016
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentFormsResource {

	private final Logger log = LoggerFactory.getLogger(DocumentFormsResource.class);

	@Inject
	private DocumentFormsService documentFormService;

	@Inject
	private FormService formService;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/document-forms", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentFormAssignment(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Document Forms Assignment");

		List<DocumentDTO> documents = documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT);
		model.addAttribute("documents", documents);
		List<FormDTO> forms = formService.findAllByCompany();
		model.addAttribute("allforms1", forms);
		return "company/documentForms";
	}

	@RequestMapping(value = "/document-forms/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestBody List<DocumentFormDTO> documentFormDTOs) {
		log.debug("REST request to save  assigned forms");
		documentFormService.save(documentFormDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-forms/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentFormDTO>> getDocumentForms(@PathVariable String documentPid) {
		log.debug("Web request to get get Forms by Document pid : {}", documentPid);
		return new ResponseEntity<>(documentFormService.findByDocumentPid(documentPid), HttpStatus.OK);
	}

}
