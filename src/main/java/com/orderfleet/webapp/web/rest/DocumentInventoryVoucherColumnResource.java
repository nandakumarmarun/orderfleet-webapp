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
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.InventoryVoucherColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.InventoryVoucherColumnRepository;
import com.orderfleet.webapp.service.DocumentInventoryVoucherColumnService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentInventoryVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherColumnDTO;

/**
 * Web controller for managing DocumentInventoryVoucherColumn.
 * 
 * @author Muhammed Riyas T
 * @since July 25, 2016
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentInventoryVoucherColumnResource {

	private final Logger log = LoggerFactory.getLogger(DocumentInventoryVoucherColumnResource.class);

	@Inject
	private DocumentInventoryVoucherColumnService documentInventoryVoucherColumnService;

	@Inject
	private InventoryVoucherColumnRepository inventoryVoucherColumnRepository;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/document-inventory-voucher-columns", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentInventoryVoucherColumnAssignment( Model model)
			throws URISyntaxException {
		log.debug("Web request to get a page of Document InventoryVoucherColumn Assignment");

		List<DocumentDTO> user = documentService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER);
		model.addAttribute("documents", user);
		List<InventoryVoucherColumn> inventoryVoucherColumns = inventoryVoucherColumnRepository.findAll();
		model.addAttribute("inventoryVoucherColumns", inventoryVoucherColumns);
		return "company/documentInventoryVoucherColumns";
	}

	@RequestMapping(value = "/document-inventory-voucher-columns/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestBody DocumentInventoryVoucherColumnDTO documentInventoryVoucherColumnDTO) {
		log.debug("REST request to save  assigned InventoryVoucher Columns",
				documentInventoryVoucherColumnDTO.getDocumentPid());
		documentInventoryVoucherColumnService.save(documentInventoryVoucherColumnDTO.getDocumentPid(),
				documentInventoryVoucherColumnDTO.getInventoryVoucherColumns());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-inventory-voucher-columns/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InventoryVoucherColumnDTO>> getDocumentInventoryVoucherColumns(
			@PathVariable String documentPid) {
		log.debug("Web request to get get InventoryVoucherColumn by Document pid : {}", documentPid);
		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = documentInventoryVoucherColumnService
				.findByDocumentPid(documentPid);
		List<InventoryVoucherColumnDTO> inventoryVoucherColumnDTOs = documentInventoryVoucherColumns.stream()
				.map(InventoryVoucherColumnDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(inventoryVoucherColumnDTOs, HttpStatus.OK);
	}

}
