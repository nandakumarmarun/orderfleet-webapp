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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ReferenceDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

@Controller
@RequestMapping("/web")
public class ReferenceDocumentResource {

	private final Logger log = LoggerFactory.getLogger(ReferenceDocumentResource.class);

	@Inject
	private DocumentService documentService;

	@Inject
	private ReferenceDocumentService referenceDocumentService;

	@RequestMapping(value = "/reference-documents", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getReferenceDocumnentsAssignment(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Referenece Document Assignment");
		List<DocumentDTO> documents = documentService.findAllByCompany();
		model.addAttribute("documents", documents);
		model.addAttribute("documents", documentService.findAllByCompany());
		return "company/referenceDocuments";
	}

	@RequestMapping(value = "/reference-documents/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveReferennceDocuments(@RequestParam String documentPid, @RequestParam String assignedProductGroups) {
		log.debug("REST request to save  assigned refrence documents", documentPid);
		referenceDocumentService.saveReferenceDocument(documentPid, assignedProductGroups);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/reference-documents/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getDocumentReferenceDocuments(@PathVariable String documentPid) {
		log.debug("Web request to get get Product Groups by Document pid : {}", documentPid);
		return new ResponseEntity<>(referenceDocumentService.findReferenceDocumentsByDocumentPid(documentPid),
				HttpStatus.OK);
	}
}
