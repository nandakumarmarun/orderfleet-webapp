package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.SalesTargetGroupDocumentService;
import com.orderfleet.webapp.service.SalesTargetGroupProductService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SaleTargetGroupProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesTargetGroup.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesTargetGroupResource {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupResource.class);

	@Inject
	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private SalesTargetGroupDocumentService salesTargetGroupDocumentService;

	@Inject
	private DocumentService documentService;

	@Inject
	private SalesTargetGroupProductService salesTargetGrouProductService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private ProductGroupProductService productGroupProductService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	/**
	 * POST /salesTargetGroups : Create a new salesTargetGroup.
	 *
	 * @param salesTargetGroupDTO
	 *            the salesTargetGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         salesTargetGroupDTO, or with status 400 (Bad Request) if the
	 *         salesTargetGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesTargetGroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SalesTargetGroupDTO> createSalesTargetGroup(
			@Valid @RequestBody SalesTargetGroupDTO salesTargetGroupDTO) throws URISyntaxException {
		log.debug("Web request to save SalesTargetGroup : {}", salesTargetGroupDTO);
		if (salesTargetGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroup", "idexists",
					"A new salesTargetGroup cannot already have an ID")).body(null);
		}
		if (salesTargetGroupService.findByName(salesTargetGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetGroup", "nameexists", "SalesTargetGroup already in use"))
					.body(null);
		}
		SalesTargetGroupDTO result = salesTargetGroupService.save(salesTargetGroupDTO);
		return ResponseEntity.created(new URI("/web/salesTargetGroups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("salesTargetGroup", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /salesTargetGroups : Updates an existing salesTargetGroup.
	 *
	 * @param salesTargetGroupDTO
	 *            the salesTargetGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         salesTargetGroupDTO, or with status 400 (Bad Request) if the
	 *         salesTargetGroupDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the salesTargetGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/salesTargetGroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetGroupDTO> updateSalesTargetGroup(
			@Valid @RequestBody SalesTargetGroupDTO salesTargetGroupDTO) throws URISyntaxException {
		log.debug("REST request to update SalesTargetGroup : {}", salesTargetGroupDTO);
		if (salesTargetGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesTargetGroup", "idNotexists",
					"SalesTargetGroup must have an ID")).body(null);
		}
		Optional<SalesTargetGroupDTO> existingSalesTargetGroup = salesTargetGroupService
				.findByName(salesTargetGroupDTO.getName());
		if (existingSalesTargetGroup.isPresent()
				&& (!existingSalesTargetGroup.get().getPid().equals(salesTargetGroupDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetGroup", "nameexists", "SalesTargetGroup already in use"))
					.body(null);
		}
		SalesTargetGroupDTO result = salesTargetGroupService.update(salesTargetGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("salesTargetGroup", "idNotexists", "Invalid SalesTargetGroup ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(
						HeaderUtil.createEntityUpdateAlert("salesTargetGroup", salesTargetGroupDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /salesTargetGroups : get all the salesTargetGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         salesTargetGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/salesTargetGroups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesTargetGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesTargetGroups");
		model.addAttribute("salesTargetGroups", salesTargetGroupService.findAllByCompany());
		model.addAttribute("products", productProfileService.findAllByCompany());
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("targetSettingTypes", Arrays.asList(BestPerformanceType.values()));
		return "company/salesTargetGroups";
	}

	/**
	 * GET /salesTargetGroups/:pid : get the "pid" salesTargetGroup.
	 *
	 * @param pid
	 *            the pid of the salesTargetGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         salesTargetGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/salesTargetGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesTargetGroupDTO> getSalesTargetGroup(@PathVariable String pid) {
		log.debug("Web request to get SalesTargetGroup by pid : {}", pid);
		return salesTargetGroupService.findOneByPid(pid)
				.map(salesTargetGroupDTO -> new ResponseEntity<>(salesTargetGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /salesTargetGroups/:id : delete the "id" salesTargetGroup.
	 *
	 * @param id
	 *            the id of the salesTargetGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/salesTargetGroups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteSalesTargetGroup(@PathVariable String pid) {
		log.debug("REST request to delete SalesTargetGroup : {}", pid);
		salesTargetGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("salesTargetGroup", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/salesTargetGroups/getDocuments/{salesTargetGroupPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getSalesTargetGroupDocuments(@PathVariable String salesTargetGroupPid,
			@RequestParam BestPerformanceType targetSettingType) {
		log.debug("Web request to get get Documents by user salesTargetGroup pid : {}", salesTargetGroupPid);
		List<DocumentDTO> allDocumnts = new ArrayList<>();
		if (targetSettingType.equals(BestPerformanceType.SALES)) {
			allDocumnts = documentService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER);
		} else if (targetSettingType.equals(BestPerformanceType.RECEIPT)) {
			allDocumnts = documentService.findAllByDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		}
		List<DocumentDTO> trueDocuments = salesTargetGroupDocumentService
				.findSalesTargetGroupDocumentsBySalesTargetGroupPid(salesTargetGroupPid);
		List<DocumentDTO> result = new ArrayList<>();
		allDocumnts.forEach(allDoc -> {
			Optional<DocumentDTO> docDTO = trueDocuments.stream().filter(td -> td.getPid().equals(allDoc.getPid())).findAny();
			if (docDTO.isPresent()) {
				allDoc.setAlias("TRUE");
			}
			result.add(allDoc);
		});
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/salesTargetGroups/saveDocuments", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveDocuments(@RequestParam String salesTargetGroupPid,
			@RequestParam String assignedDocuments) {
		log.debug("REST request to save assigned documents", salesTargetGroupPid);
		salesTargetGroupDocumentService.save(salesTargetGroupPid, assignedDocuments);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/salesTargetGroups/findProducts/{salesTargetGroupPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getSalesTargetGroupProducts(
			@PathVariable String salesTargetGroupPid) {
		log.debug("Web request to get get Documents by salesTargetGroup pid : {}", salesTargetGroupPid);
		return new ResponseEntity<>(
				salesTargetGrouProductService.findSalesTargetGroupProductsBySalesTargetGroupPid(salesTargetGroupPid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/salesTargetGroups/saveProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveProducts(@RequestParam String salesTargetGroupPid,
			@RequestParam String assignedProducts) {
		log.debug("REST request to save assigned products", salesTargetGroupPid);
		salesTargetGrouProductService.save(salesTargetGroupPid, assignedProducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/salesTargetGroups/filterByCategoryGroup/{salesTargetGroupPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<SaleTargetGroupProductProfileDTO> filterProductProfilesByCategoryAndGroup(
			@RequestParam String categoryPids, @RequestParam String groupPids, @PathVariable String salesTargetGroupPid)
			throws URISyntaxException {
		SaleTargetGroupProductProfileDTO saleTargetGroupProductProfileDTO = new SaleTargetGroupProductProfileDTO();
		// none selected
		if (categoryPids.isEmpty() && groupPids.isEmpty()) {
			saleTargetGroupProductProfileDTO.setAllProductProfile(
					productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true));
			saleTargetGroupProductProfileDTO.setAssignedProductProfile(salesTargetGrouProductService
					.findSalesTargetGroupProductsBySalesTargetGroupPid(salesTargetGroupPid));
			return new ResponseEntity<>(saleTargetGroupProductProfileDTO, HttpStatus.OK);
		}
		// both selected
		if (!categoryPids.isEmpty() && !groupPids.isEmpty()) {
			saleTargetGroupProductProfileDTO.setAllProductProfile(
					productGroupProductService.findByProductGroupPidsAndCategoryPidsAndActivatedProductProfile(
							Arrays.asList(groupPids.split(",")), Arrays.asList(categoryPids.split(","))));
			saleTargetGroupProductProfileDTO.setAssignedProductProfile(salesTargetGrouProductService
					.findSalesTargetGroupProductsBySalesTargetGroupPid(salesTargetGroupPid));
			return new ResponseEntity<>(saleTargetGroupProductProfileDTO, HttpStatus.OK);

		}
		// category selected correct
		if (!categoryPids.isEmpty() && groupPids.isEmpty()) {
			saleTargetGroupProductProfileDTO.setAllProductProfile(productProfileService
					.findByProductCategoryPidsAndActivated(Arrays.asList(categoryPids.split(","))));
			saleTargetGroupProductProfileDTO.setAssignedProductProfile(salesTargetGrouProductService
					.findSalesTargetGroupProductsBySalesTargetGroupPid(salesTargetGroupPid));
			return new ResponseEntity<>(saleTargetGroupProductProfileDTO, HttpStatus.OK);
		}
		// group selected
		if (categoryPids.isEmpty() && !groupPids.isEmpty()) {
			saleTargetGroupProductProfileDTO.setAllProductProfile(
					productGroupProductService.findByProductGroupPidsAndActivated(Arrays.asList(groupPids.split(","))));
			saleTargetGroupProductProfileDTO.setAssignedProductProfile(salesTargetGrouProductService
					.findSalesTargetGroupProductsBySalesTargetGroupPid(salesTargetGroupPid));
			return new ResponseEntity<>(saleTargetGroupProductProfileDTO, HttpStatus.OK);
		}
		return new ResponseEntity<>(saleTargetGroupProductProfileDTO, HttpStatus.OK);
	}

}