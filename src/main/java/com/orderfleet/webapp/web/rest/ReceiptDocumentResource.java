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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ReceiptDocument;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ReceiptDocumentService;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Web controller for managing ReceiptDocument.
 * 
 * @author Sarath
 * @since Sep 6, 2016
 */

@Controller
@RequestMapping("/web")
public class ReceiptDocumentResource {

	private final Logger log = LoggerFactory.getLogger(ReceiptDocumentResource.class);

	@Inject
	private ReceiptDocumentService receiptDocumentService;

	@Inject
	private CompanyService companyService;

	@Inject
	private DocumentService documentService;

	/**
	 * GET /receiptDocuments : get all the receiptDocuments.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         receiptDocuments in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/receipt_documents", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllReceiptDocuments( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ReceiptDocuments");
		List<ReceiptDocument> receiptDocuments = receiptDocumentService.findAllByCompanyIdAndActivedTrue();
		List<CompanyViewDTO> companies = companyService.findAll();
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.ACCOUNTING_VOUCHER));
		model.addAttribute("receiptDocuments", receiptDocuments);
		model.addAttribute("companies", companies);

		return "company/receiptDocuments";
	}

	@RequestMapping(value = "/receipt_documents/getDocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getUserReceiptTarget() {
		List<DocumentDTO> documents = receiptDocumentService.findAllDocumentsByCompanyId();
		return new ResponseEntity<>(documents, HttpStatus.OK);
	}

	@RequestMapping(value = "/receipt_documents/saveDocuments", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String assignedDocuments) {
		log.debug("REST request to save assigned documents", assignedDocuments);
		receiptDocumentService.save(assignedDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
