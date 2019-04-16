package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.AccountingVoucherColumn;
import com.orderfleet.webapp.repository.AccountingVoucherColumnRepository;
import com.orderfleet.webapp.service.DocumentAccountingVoucherColumnService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentAccountingVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherColumnDTO;

/**
 * Web controller for managing DocumentAccountingVoucherColumn.
 * 
 * @author Muhammed Riyas T
 * @since July 26, 2016
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentAccountingVoucherColumnResource {

	private final Logger log = LoggerFactory.getLogger(DocumentAccountingVoucherColumnResource.class);

	@Inject
	private DocumentAccountingVoucherColumnService documentAccountingVoucherColumnService;

	@Inject
	private AccountingVoucherColumnRepository accountingVoucherColumnRepository;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/document-accounting-voucher-columns", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentAccountingVoucherColumnAssignment(Model model)
			throws URISyntaxException {
		log.debug("Web request to get a page of Document AccountingVoucherColumn Assignment");

		List<DocumentDTO> pageUser = documentService.findAllByDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		model.addAttribute("documents", pageUser);
		List<AccountingVoucherColumn> accountingVoucherColumns = accountingVoucherColumnRepository.findAll();
		model.addAttribute("accountingVoucherColumns", accountingVoucherColumns);
		return "company/documentAccountingVoucherColumns";
	}

	@RequestMapping(value = "/document-accounting-voucher-columns/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(
			@RequestBody DocumentAccountingVoucherColumnDTO documentAccountingVoucherColumnDTO) {
		log.debug("REST request to save  assigned AccountingVoucher Columns",
				documentAccountingVoucherColumnDTO.getDocumentPid());
		documentAccountingVoucherColumnService.save(documentAccountingVoucherColumnDTO.getDocumentPid(),
				documentAccountingVoucherColumnDTO.getAccountingVoucherColumns());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-accounting-voucher-columns/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountingVoucherColumnDTO>> getDocumentAccountingVoucherColumns(
			@PathVariable String documentPid) {
		log.debug("Web request to get get AccountingVoucherColumn by Document pid : {}", documentPid);
		List<DocumentAccountingVoucherColumn> documentAccountingVoucherColumns = documentAccountingVoucherColumnService
				.findByDocumentPid(documentPid);
		List<AccountingVoucherColumnDTO> accountingVoucherColumnDTOs = documentAccountingVoucherColumns.stream()
				.map(AccountingVoucherColumnDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(accountingVoucherColumnDTOs, HttpStatus.OK);
	}

}
