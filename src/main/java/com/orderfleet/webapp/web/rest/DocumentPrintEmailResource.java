package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentPrintEmailService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintEmailDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class DocumentPrintEmailResource {

	private final Logger log = LoggerFactory.getLogger(DocumentPrintEmailResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private DocumentPrintEmailService documentPrintEmailService;

	@Inject
	private DocumentRepository documentRepository;

	@RequestMapping(value = "/document-print-email", method = RequestMethod.GET)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	@Timed
	public String getDocumentPrintEmail(Model model) throws URISyntaxException {
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/document-print-emails";
	}

	@RequestMapping(value = "/document-print-email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DocumentPrintEmailDTO> saveDocumentPrintEmails(
			@Valid @RequestBody DocumentPrintEmailDTO documentPrintEmailDTO) throws URISyntaxException {
		if (documentPrintEmailService
				.findOneByDocumentPidAndName(documentPrintEmailDTO.getDocumentPid(), documentPrintEmailDTO.getName())
				.isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentPrintEmail",
					"document exists", "Document Print Email already in use")).body(null);
		}
		DocumentPrintEmailDTO result = documentPrintEmailService.save(documentPrintEmailDTO);
		return ResponseEntity.created(new URI("/web/document-print-email/" + result.getDocumentPid()))
				.headers(HeaderUtil.createEntityCreationAlert("documentPrintEmail", result.getDocumentPid()))
				.body(result);
	}

	@RequestMapping(value = "/document-print-email", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DocumentPrintEmailDTO> updateDocumentPrintEmails(
			@Valid @RequestBody DocumentPrintEmailDTO documentPrintEmailDTO) throws URISyntaxException {

		log.debug("Web request to update documentPrintEmail : {}", documentPrintEmailDTO);
		if (documentPrintEmailDTO.getDocumentPrintEmailId() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentPrintEmail",
					"idNotexists", "Document Print Email must have an ID")).body(null);
		}

		Optional<DocumentPrintEmailDTO> existingActivity = documentPrintEmailService
				.findOneByDocumentPidAndName(documentPrintEmailDTO.getDocumentPid(), documentPrintEmailDTO.getName());
		if (existingActivity.isPresent() && (!existingActivity.get().getDocumentPrintEmailId()
				.equals(documentPrintEmailDTO.getDocumentPrintEmailId()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentPrintEmail", "nameexists",
					" Document Print-Email already in use")).body(null);
		}
		DocumentPrintEmailDTO result = documentPrintEmailService.update(documentPrintEmailDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("documentPrintEmail",
					"idNotexists", "Invalid Document Print-Email ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("documentPrintEmail",
				documentPrintEmailDTO.getDocumentPrintEmailId().toString())).body(result);

	}

	@RequestMapping(value = "/document-print-email/getDocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam String companyPid) throws URISyntaxException {
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(companyPid);
		List<DocumentDTO> documentDTOs1=documents.stream().map(DocumentDTO::new).collect(Collectors.toList());
		return documentDTOs1;
	}

	@RequestMapping(value = "/document-print-email/getDocumentPrintEmail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentPrintEmailDTO> getDocumentPrintEmails(@Valid @RequestParam String companyPid)
			throws URISyntaxException {
		List<DocumentPrintEmailDTO> documentPrintEmailDTOs = documentPrintEmailService
				.findDocumentPrintEmailByCompanyPid(companyPid);
		return documentPrintEmailDTOs;
	}

	@RequestMapping(value = "/document-print-email/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public DocumentPrintEmailDTO getDocumentPrintEmail(@Valid @PathVariable long id) throws URISyntaxException {
		DocumentPrintEmailDTO documentPrintEmailDTO = documentPrintEmailService
				.findDocumentPrintEmailByDocumentPrintEmailId(id);
		return documentPrintEmailDTO;
	}
}
