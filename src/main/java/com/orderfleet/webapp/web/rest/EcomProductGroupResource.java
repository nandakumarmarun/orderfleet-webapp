package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.EcomProductGroupService;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.ProductGroupEcomProductsService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing EcomProductGroup.
 * 
 * @author Anish
 * @since May 17, 2020
 */
@Controller
@RequestMapping("/web")
public class EcomProductGroupResource {

	private final Logger log = LoggerFactory.getLogger(EcomProductGroupResource.class);

	@Inject
	private EcomProductGroupService ecomProductGroupService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private EcomProductProfileService ecomProductProfileService;

	@Inject
	private ProductGroupProductService productGroupProductService;

	@Inject
	private ProductGroupEcomProductsService productGroupEcomProductsService;

	@Inject
	private TaxMasterService taxmasterService;

	/**
	 * POST /productGroups : Create a new productGroup.
	 *
	 * @param productGroupDTO
	 *            the productGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new productGroupDTO, or with status 400 (Bad Request) if the
	 *         productGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/ecomProductGroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<EcomProductGroupDTO> createProductGroup(@Valid @RequestBody EcomProductGroupDTO productGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save EcomProductGroup : {}", productGroupDTO);
		if (productGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("ecomProductGroup", "idexists",
					"A new ecomProductGroup cannot already have an ID")).body(null);
		}
		if (ecomProductGroupService.findByName(productGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("productGroup", "nameexists", "Product Group already in use"))
					.body(null);
		}
		productGroupDTO.setActivated(true);
		EcomProductGroupDTO result = ecomProductGroupService.save(productGroupDTO);
		return ResponseEntity.created(new URI("/web/productGroups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("productGroup", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /productGroups : Updates an existing productGroup.
	 *
	 * @param productGroupDTO
	 *            the productGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         productGroupDTO, or with status 400 (Bad Request) if the
	 *         productGroupDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the productGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/ecomProductGroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EcomProductGroupDTO> updateProductGroup(@Valid @RequestBody EcomProductGroupDTO productGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to update ProductGroup : {}", productGroupDTO);
		if (productGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productGroup", "idNotexists", "Ecom Product Group must have an ID"))
					.body(null);
		}
		Optional<EcomProductGroupDTO> existingProductGroup = ecomProductGroupService.findByName(productGroupDTO.getName());
		if (existingProductGroup.isPresent()
				&& (!existingProductGroup.get().getPid().equals(productGroupDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("productGroup", "nameexists", "Product Group already in use"))
					.body(null);
		}
		EcomProductGroupDTO result = ecomProductGroupService.update(productGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("ecomProductGroup", "idNotexists", "Invalid Product Group ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("ecomProductGroup", productGroupDTO.getPid().toString()))
				.body(result);
	}

	@Timed
	@RequestMapping(value = "/ecomProductGroups/assign-tax", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedTax(@RequestBody SetTaxRate setTaxRate) {
		log.debug("REST request to save assigned tax rate : {}", setTaxRate);
		ecomProductGroupService.saveTaxRate(setTaxRate);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ecomProductGroups/assign-unit-quantity", method = RequestMethod.POST)
	public ResponseEntity<Void> assignUnitQuantity(@RequestBody SetTaxRate setUnitQty) {
		log.debug("REST request to save assigned unit quantity : {}", setUnitQty);
		ecomProductGroupService.saveUnitQuantity(setUnitQty);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /productGroups : get all the productGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/ecomProductGroups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductGroups");
		List<EcomProductGroupDTO> pageProductGroup = ecomProductGroupService.findAllByCompanyAndDeactivatedProductGroup(true);
		model.addAttribute("pageProductGroup", pageProductGroup);
		model.addAttribute("deactivatedProductGroups",
				ecomProductGroupService.findAllByCompanyAndDeactivatedProductGroup(false));
		model.addAttribute("productGroups", ecomProductGroupService.findAllByCompany());
		model.addAttribute("products",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true));
		model.addAttribute("ecomProducts",
				ecomProductProfileService.findAllByCompanyAndActivatedOrDeactivatedEcomProductProfile(true));
		model.addAttribute("taxMasters", taxmasterService.findAllByCompany());
		return "company/productGroups";
	}

	/**
	 * GET /productGroups/:id : get the "id" productGroup.
	 *
	 * @param id
	 *            the id of the productGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         productGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/ecomProductGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<EcomProductGroupDTO> getProductGroup(@PathVariable String pid) {
		log.debug("Web request to get ProductGroup by pid : {}", pid);
		return ecomProductGroupService.findOneByPid(pid)
				.map(productGroupDTO -> new ResponseEntity<>(productGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /productGroups/:id : delete the "id" productGroup.
	 *
	 * @param id
	 *            the id of the productGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/ecomProductGroups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteProductGroup(@PathVariable String pid) {
		log.debug("REST request to delete EcomProductGroup : {}", pid);
		ecomProductGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ecomProductGroup", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/ecomProductGroups/assignProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProducts(@RequestParam String pid, @RequestParam String assignedproducts) {
		log.debug("REST request to save assigned account type : {}", pid);
		productGroupProductService.save(pid, assignedproducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/ecomProductGroups/findProducts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProducts(@PathVariable String pid) {
		log.debug("REST request to get Products by ecomProducyGroupPid : {}", pid);
		List<ProductProfileDTO> productGroupProducts = productGroupProductService.findProductByProductGroupPid(pid);
		return new ResponseEntity<>(productGroupProducts, HttpStatus.OK);

	}

	@RequestMapping(value = "/ecomProductGroups/assignEcomProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedEcomProducts(@RequestParam String pid,
			@RequestParam String assignedEcomProducts) {
		log.debug("REST request to save assigned ecomProductGroups : {}", pid);
		productGroupEcomProductsService.save(pid, assignedEcomProducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/ecomProductGroups/findEcomProducts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<EcomProductProfileDTO>> getEcomProducts(@PathVariable String pid) {
		log.debug("REST request to get Products by producyGroupPid : {}", pid);
		List<EcomProductProfileDTO> ecomProductGroupProducts = productGroupEcomProductsService
				.findEcomProductByProductGroupPid(pid);
		return new ResponseEntity<>(ecomProductGroupProducts, HttpStatus.OK);

	}

	@RequestMapping(value = "/ecomProductGroups/not-assigned-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getNotAssignedProducts() {
		log.debug("REST request to get Products by not in product product group");
		List<ProductProfileDTO> productGroupProducts = productProfileService.findProductsAssignedInProductGroup();
		return new ResponseEntity<>(productGroupProducts, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /activities/changeStatus : update status
	 *        (Activated/Deactivated) of productGroup.
	 * 
	 * @param productGroupDTO
	 *            the productGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         ProductGroupDTO
	 */

	@Timed
	@RequestMapping(value = "/ecomProductGroups/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EcomProductGroupDTO> updateProductGroupStatus(
			@Valid @RequestBody EcomProductGroupDTO productGroupDTO) {
		log.debug("REST request to change status of ProductsGroup ", productGroupDTO);
		EcomProductGroupDTO productDTO = ecomProductGroupService.updateProductGroupStatus(productGroupDTO.getPid(),
				productGroupDTO.getActivated());
		return new ResponseEntity<>(productDTO, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /productGroups/activateProductGroup : activate
	 *        status of ProductGroup.
	 * 
	 * @param productgroups
	 *            the productgroups to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/ecomProductGroups/activateProductGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EcomProductGroupDTO> activateProductGroup(@Valid @RequestParam String productgroups) {
		log.debug("REST request to activate ProductsGroup ");
		String[] productGroups = productgroups.split(",");
		for (String productGrouppid : productGroups) {
			ecomProductGroupService.updateProductGroupStatus(productGrouppid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/ecomProductGroups/changeThirdpartyUpdateStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EcomProductGroupDTO> updateProductGroupThirdpartyUpdate(
			@Valid @RequestBody EcomProductGroupDTO productGroupDTO) {
		log.debug("REST request to change ThirdpartyUpdate of ProductsGroup ", productGroupDTO);
		System.out.println(productGroupDTO);
		EcomProductGroupDTO productDTO = ecomProductGroupService.updateProductGroupThirdpartyUpdate(productGroupDTO.getPid(),
				productGroupDTO.getThirdpartyUpdate());
		return new ResponseEntity<>(productDTO, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/ecomProductGroups/assign-tax-master/{productGroupPids}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveAssignedTax(@RequestParam String selectedTax,
			@PathVariable String productGroupPids) {
		log.debug("Web request to save ProductProfile by productGroupPids : {}", productGroupPids);
		List<String> productGpPid = new ArrayList<>();
		String[] productGroups = productGroupPids.split(",");
		for (String productGrouppid : productGroups) {
			productGpPid.add(productGrouppid);
		}
		List<String> taxMasterPids = new ArrayList<>();
		String[] taxMasterpids = selectedTax.split(",");
		for (String taxMasterpid : taxMasterpids) {
			taxMasterPids.add(taxMasterpid);
		}
		ecomProductGroupService.saveTaxMaster(taxMasterPids, productGpPid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/ecomProductGroups/findTaxMaster", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<EcomProductGroupDTO>> getTaxMasters() {
		log.debug("REST request to get Products by producyGroupPid : {}");
		List<EcomProductGroupDTO> productGroupDTOs = ecomProductGroupService.findAllProductGroupByCompanyOrderByName();
		return new ResponseEntity<>(productGroupDTOs, HttpStatus.OK);

	}

}
