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
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.ProductGroupEcomProductsService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ProductGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductGroupResource {

	private final Logger log = LoggerFactory.getLogger(ProductGroupResource.class);

	@Inject
	private ProductGroupService productGroupService;

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
	
	@Inject
	private	CompanyConfigurationRepository companyConfigurationRepository;

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
	@RequestMapping(value = "/productGroups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ProductGroupDTO> createProductGroup(@Valid @RequestBody ProductGroupDTO productGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save ProductGroup : {}", productGroupDTO);
		if (productGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productGroup", "idexists",
					"A new productGroup cannot already have an ID")).body(null);
		}
		if (productGroupService.findByName(productGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("productGroup", "nameexists", "Product Group already in use"))
					.body(null);
		}
		productGroupDTO.setActivated(true);
		ProductGroupDTO result = productGroupService.save(productGroupDTO);
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
	@RequestMapping(value = "/productGroups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductGroupDTO> updateProductGroup(@Valid @RequestBody ProductGroupDTO productGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to update ProductGroup : {}", productGroupDTO);
		if (productGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productGroup", "idNotexists", "Product Group must have an ID"))
					.body(null);
		}
		Optional<ProductGroupDTO> existingProductGroup = productGroupService.findByName(productGroupDTO.getName());
		if (existingProductGroup.isPresent()
				&& (!existingProductGroup.get().getPid().equals(productGroupDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("productGroup", "nameexists", "Product Group already in use"))
					.body(null);
		}
		ProductGroupDTO result = productGroupService.update(productGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("productGroup", "idNotexists", "Invalid Product Group ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("productGroup", productGroupDTO.getPid().toString()))
				.body(result);
	}

	@Timed
	@RequestMapping(value = "/productGroups/assign-tax", method = RequestMethod.POST)
	public ResponseEntity<Void> saveAssignedTax(@RequestBody SetTaxRate setTaxRate) {
		log.debug("REST request to save assigned tax rate : {}", setTaxRate);
		productGroupService.saveTaxRate(setTaxRate);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/productGroups/assign-unit-quantity", method = RequestMethod.POST)
	public ResponseEntity<Void> assignUnitQuantity(@RequestBody SetTaxRate setUnitQty) {
		log.debug("REST request to save assigned unit quantity : {}", setUnitQty);
		productGroupService.saveUnitQuantity(setUnitQty);
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
	@RequestMapping(value = "/productGroups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductGroups");
		List<ProductGroupDTO> pageProductGroup = productGroupService.findAllByCompanyAndDeactivatedProductGroup(true);
		model.addAttribute("pageProductGroup", pageProductGroup);
		model.addAttribute("deactivatedProductGroups",
				productGroupService.findAllByCompanyAndDeactivatedProductGroup(false));
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("products",
				productProfileService.findAllByCompanyAndActivatedProductProfileOrderByName(true));
		model.addAttribute("ecomProducts",
				ecomProductProfileService.findAllByCompanyAndActivatedOrDeactivatedEcomProductProfile(true));
		model.addAttribute("taxMasters", taxmasterService.findAllByCompany());
		//companyConfiguration based name
		Optional<CompanyConfiguration> optLocationVariance = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optLocationVariance.isPresent()) {
			model.addAttribute("companyConfiguration",  Boolean.valueOf(optLocationVariance.get().getValue()));		
		}
		
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
	@RequestMapping(value = "/productGroups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductGroupDTO> getProductGroup(@PathVariable String pid) {
		log.debug("Web request to get ProductGroup by pid : {}", pid);
		return productGroupService.findOneByPid(pid)
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
	@RequestMapping(value = "/productGroups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteProductGroup(@PathVariable String pid) {
		log.debug("REST request to delete ProductGroup : {}", pid);
		productGroupService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("productGroup", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/productGroups/assignProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProducts(@RequestParam String pid, @RequestParam String assignedproducts) {
		log.debug("REST request to save assigned account type : {}", pid);
		productGroupProductService.save(pid, assignedproducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/productGroups/findProducts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProducts(@PathVariable String pid) {
		log.debug("REST request to get Products by producyGroupPid : {}", pid);
		List<ProductProfileDTO> productGroupProducts = productGroupProductService.findProductByProductGroupPid(pid);
		return new ResponseEntity<>(productGroupProducts, HttpStatus.OK);

	}

	@RequestMapping(value = "/productGroups/assignEcomProducts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedEcomProducts(@RequestParam String pid,
			@RequestParam String assignedEcomProducts) {
		log.debug("REST request to save assigned productGroups : {}", pid);
		productGroupEcomProductsService.save(pid, assignedEcomProducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/productGroups/findEcomProducts/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<EcomProductProfileDTO>> getEcomProducts(@PathVariable String pid) {
		log.debug("REST request to get Products by producyGroupPid : {}", pid);
		List<EcomProductProfileDTO> ecomProductGroupProducts = productGroupEcomProductsService
				.findEcomProductByProductGroupPid(pid);
		return new ResponseEntity<>(ecomProductGroupProducts, HttpStatus.OK);

	}

	@RequestMapping(value = "/productGroups/not-assigned-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
	@RequestMapping(value = "/productGroups/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductGroupDTO> updateProductGroupStatus(
			@Valid @RequestBody ProductGroupDTO productGroupDTO) {
		log.debug("REST request to change status of ProductsGroup ", productGroupDTO);
		ProductGroupDTO productDTO = productGroupService.updateProductGroupStatus(productGroupDTO.getPid(),
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
	@RequestMapping(value = "/productGroups/activateProductGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductGroupDTO> activateProductGroup(@Valid @RequestParam String productgroups) {
		log.debug("REST request to activate ProductsGroup ");
		String[] productGroups = productgroups.split(",");
		for (String productGrouppid : productGroups) {
			productGroupService.updateProductGroupStatus(productGrouppid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/productGroups/changeThirdpartyUpdateStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductGroupDTO> updateProductGroupThirdpartyUpdate(
			@Valid @RequestBody ProductGroupDTO productGroupDTO) {
		log.debug("REST request to change ThirdpartyUpdate of ProductsGroup ", productGroupDTO);
		System.out.println(productGroupDTO);
		ProductGroupDTO productDTO = productGroupService.updateProductGroupThirdpartyUpdate(productGroupDTO.getPid(),
				productGroupDTO.getThirdpartyUpdate());
		return new ResponseEntity<>(productDTO, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/productGroups/assign-tax-master/{productGroupPids}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
		productGroupService.saveTaxMaster(taxMasterPids, productGpPid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/productGroups/findTaxMaster", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductGroupDTO>> getTaxMasters() {
		log.debug("REST request to get Products by producyGroupPid : {}");
		List<ProductGroupDTO> productGroupDTOs = productGroupService.findAllProductGroupByCompanyOrderByName();
		return new ResponseEntity<>(productGroupDTOs, HttpStatus.OK);

	}

}
