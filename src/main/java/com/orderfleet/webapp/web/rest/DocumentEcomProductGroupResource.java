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
import com.orderfleet.webapp.service.DocumentEcomProductGroupService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EcomProductGroupService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentEcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;

/**
 * Web controller for managing DocumentEcomProductGroup.
 * 
 * @author Anish
 * @since June 10 2020
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentEcomProductGroupResource {

	private final Logger log = LoggerFactory.getLogger(DocumentEcomProductGroupResource.class);

	@Inject
	private DocumentEcomProductGroupService documentProductGroupService;

	@Inject
	private EcomProductGroupService productGroupService;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/document-ecom-product-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentProductGroupAssignment( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Document Ecom Product Group Assignment");

		List<DocumentDTO> user = documentService.findAllByCompany();
		model.addAttribute("documents", user);
		List<EcomProductGroupDTO> productGroups = productGroupService.findAllByCompany();
		model.addAttribute("allproductGroups1", productGroups);
		return "company/documentEcomProductGroup";
	}

	@RequestMapping(value = "/document-ecom-product-groups/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestBody List<DocumentEcomProductGroupDTO> documentProductGroupDTOs) {
		log.debug("REST request to save  assigned ecom product groups");
		documentProductGroupService.save(documentProductGroupDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-ecom-product-groups/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentEcomProductGroupDTO>> getDocumentProductGroups(@PathVariable String documentPid) {
		log.debug("Web request to get get Ecom Product Groups by Document pid : {}", documentPid);
		return new ResponseEntity<>(documentProductGroupService.findByDocumentPid(documentPid), HttpStatus.OK);
	}

}
