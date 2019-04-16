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
import com.orderfleet.webapp.service.DocumentProductGroupService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

/**
 * Web controller for managing DocumentProductGroup.
 * 
 * @author Sarath
 * @since July 07 2016
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentProductGroupResource {

	private final Logger log = LoggerFactory.getLogger(DocumentProductGroupResource.class);

	@Inject
	private DocumentProductGroupService documentProductGroupService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/document-product-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentProductGroupAssignment( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Document Product Group Assignment");

		List<DocumentDTO> user = documentService.findAllByCompany();
		model.addAttribute("documents", user);
		List<ProductGroupDTO> productGroups = productGroupService.findAllByCompany();
		model.addAttribute("allproductGroups1", productGroups);
		return "company/documentProductGroup";
	}

	@RequestMapping(value = "/document-product-groups/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestBody List<DocumentProductGroupDTO> documentProductGroupDTOs) {
		log.debug("REST request to save  assigned product groups");
		documentProductGroupService.save(documentProductGroupDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-product-groups/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentProductGroupDTO>> getDocumentProductGroups(@PathVariable String documentPid) {
		log.debug("Web request to get get Product Groups by Document pid : {}", documentPid);
		return new ResponseEntity<>(documentProductGroupService.findByDocumentPid(documentPid), HttpStatus.OK);
	}

}
