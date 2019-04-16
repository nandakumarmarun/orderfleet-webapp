package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Web controller for managing PrimarySecondaryDocument.
 * 
 * @author Shaheer
 * @since December 31, 2016
 */

@Controller
@RequestMapping("/web")
public class PrimarySecondaryDocumentResource {

	private final Logger log = LoggerFactory.getLogger(PrimarySecondaryDocumentResource.class);

	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;

	@Inject
	private DocumentService documentService;

	/**
	 * GET /primarySecondaryDocuments : get all the primarySecondaryDocuments.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         primarySecondaryDocuments in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/primary-secondary-documents", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllPrimarySecondaryDocuments( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PrimarySecondaryDocuments");
		List<PrimarySecondaryDocument> primarySalesDocuments = primarySecondaryDocumentService
				.findAllByCompanyIdAndActivedTrue();
		List<VoucherType>voucherTypes=new ArrayList<>(Arrays.asList(VoucherType.values()));
		
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER));
		model.addAttribute("primarySecondaryDocuments", primarySalesDocuments);
		model.addAttribute("voucherTypes", voucherTypes);
		return "company/primarySecondaryDocuments";
	}
	
	@RequestMapping(value = "/primary-secondary-documents/getDocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getPrimarySalesOrderDocuments() {
		List<DocumentDTO> documents = primarySecondaryDocumentService.findAllDocumentsByCompanyId();
		return new ResponseEntity<>(documents, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/primary-secondary-documents/{voucherType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getPrimarySalesOrderDocuments(@PathVariable VoucherType voucherType) {
		List<DocumentDTO> documents = primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
		return new ResponseEntity<>(documents, HttpStatus.OK);
	}

	@RequestMapping(value = "/primary-secondary-documents/saveDocuments", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> savePrimarySalesOrder(@RequestParam String assignedDocuments, @RequestParam VoucherType voucherType) {
		log.debug("REST request to save assigned documents", assignedDocuments);
		primarySecondaryDocumentService.save(assignedDocuments, voucherType);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
