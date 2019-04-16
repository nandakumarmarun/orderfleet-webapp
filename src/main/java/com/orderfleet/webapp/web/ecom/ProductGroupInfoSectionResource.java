package com.orderfleet.webapp.web.ecom;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ProductGroupInfoSectionService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.web.ecom.dto.ProductGroupInfoSectionDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ProductGroupInfoSection.
 * 
 * @author Muhammed Riyas T
 * @since Sep 21, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductGroupInfoSectionResource {

	private final Logger log = LoggerFactory.getLogger(ProductGroupInfoSectionResource.class);

	@Inject
	private ProductGroupInfoSectionService productGroupInfoSectionService;

	@Inject
	private ProductGroupService productGroupService;

	/**
	 * GET /product-group-info-sections : get all the productGroupInfoSections.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         product-group-info-sections in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/product-group-info-sections", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductGroupInfoSections(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductGroupInfoSections");
		model.addAttribute("pageProductGroupInfoSection", productGroupInfoSectionService.findAllByCompany(pageable));
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		return "company/productGroupInfoSections";
	}

	/**
	 * POST /product-group-info-sections : Create a new productGroupInfoSection.
	 *
	 * @param productGroupInfoSectionDTO
	 *            the productGroupInfoSectionDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new productGroupInfoSectionDTO, or with status 400 (Bad Request)
	 *         if the productGroupInfoSection has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/product-group-info-sections", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ProductGroupInfoSectionDTO> createProductGroupInfoSection(
			@Valid @RequestBody ProductGroupInfoSectionDTO productGroupInfoSectionDTO) throws URISyntaxException {
		log.debug("Web request to save ProductGroupInfoSection : {}", productGroupInfoSectionDTO);
		if (productGroupInfoSectionDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productGroupInfoSection",
					"idexists", "A new productGroup Inf oSection cannot already have an ID")).body(null);
		}
		if (productGroupInfoSectionService.findByName(productGroupInfoSectionDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productGroupInfoSection",
					"nameexists", "Product Group Info Section already in use")).body(null);
		}
		ProductGroupInfoSectionDTO result = productGroupInfoSectionService.save(productGroupInfoSectionDTO);
		return ResponseEntity.created(new URI("/web/product-group-info-sections/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("productGroupInfoSection", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /product-group-info-sections : Updates an existing
	 * productGroupInfoSection.
	 *
	 * @param productGroupInfoSectionDTO
	 *            the productGroupInfoSectionDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         productGroupInfoSectionDTO, or with status 400 (Bad Request) if
	 *         the productGroupInfoSectionDTO is not valid, or with status 500
	 *         (Internal Server Error) if the productGroupInfoSectionDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/product-group-info-sections", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductGroupInfoSectionDTO> updateProductGroupInfoSection(
			@Valid @RequestBody ProductGroupInfoSectionDTO productGroupInfoSectionDTO) throws URISyntaxException {
		log.debug("Web request to update ProductGroupInfoSection : {}", productGroupInfoSectionDTO);
		if (productGroupInfoSectionDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productGroupInfoSection",
					"idNotexists", "ProductGroupInfoSection must have an ID")).body(null);
		}
		Optional<ProductGroupInfoSectionDTO> existingProductGroupInfoSection = productGroupInfoSectionService
				.findByName(productGroupInfoSectionDTO.getName());
		if (existingProductGroupInfoSection.isPresent()
				&& (!existingProductGroupInfoSection.get().getPid().equals(productGroupInfoSectionDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productGroupInfoSection",
					"nameexists", "Product Group Info Section already in use")).body(null);
		}
		ProductGroupInfoSectionDTO result = productGroupInfoSectionService.update(productGroupInfoSectionDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productGroupInfoSection",
					"idNotexists", "Invalid ProductGroupInfoSection ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("productGroupInfoSection",
				productGroupInfoSectionDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /product-group-info-sections/:id : get the "id"
	 * productGroupInfoSection.
	 *
	 * @param id
	 *            the id of the productGroupInfoSectionDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         productGroupInfoSectionDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/product-group-info-sections/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductGroupInfoSectionDTO> getProductGroupInfoSection(@PathVariable String pid) {
		log.debug("Web request to get ProductGroupInfoSection by pid : {}", pid);
		return productGroupInfoSectionService.findOneByPid(pid)
				.map(productGroupInfoSectionDTO -> new ResponseEntity<>(productGroupInfoSectionDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /product-group-info-sections/:id : delete the "id"
	 * productGroupInfoSection.
	 *
	 * @param id
	 *            the id of the productGroupInfoSectionDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/product-group-info-sections/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteProductGroupInfoSection(@PathVariable String pid) {
		log.debug("REST request to delete ProductGroupInfoSection : {}", pid);
		productGroupInfoSectionService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("productGroupInfoSection", pid.toString())).build();
	}

}
