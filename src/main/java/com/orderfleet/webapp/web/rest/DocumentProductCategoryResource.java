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
import com.orderfleet.webapp.service.DocumentProductCategoryService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;

@Controller
@RequestMapping("/web")
public class DocumentProductCategoryResource {

	private final Logger log = LoggerFactory.getLogger(DocumentProductCategoryResource.class);

	@Inject
	private DocumentService documentService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	DocumentProductCategoryService documentProductCategoryService;

	@RequestMapping(value = "/document-product-categories", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumnentProductCategoryAssignment( Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Document Product category Assignment");

		List<DocumentDTO> user = documentService.findAllByCompany();
		model.addAttribute("documents", user);
		List<ProductCategoryDTO> productCategories = productCategoryService.findAllByCompany();
		model.addAttribute("productCategories", productCategories);
		return "company/documentProductCategory";
	}

	@RequestMapping(value = "/document-product-categories/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String documentPid, @RequestParam String assignedProductCategories) {
		log.debug("REST request to save  assigned product categorys", documentPid);
		documentProductCategoryService.save(documentPid, assignedProductCategories);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-product-categories/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductCategoryDTO>> getDocumentProductCategories(@PathVariable String documentPid) {
		log.debug("Web request to get get Product Categories by Document pid : {}", documentPid);
		return new ResponseEntity<>(documentProductCategoryService.findProductCategoriesByDocumentPid(documentPid),
				HttpStatus.OK);
	}

}
