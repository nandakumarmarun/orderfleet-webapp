package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EcomProductProfileProductService;
import com.orderfleet.webapp.service.PriceLevelAccountProductGroupService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductGroupEcomProductsService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class PriceLevelAccountProductGroupResource {
	private final Logger log = LoggerFactory.getLogger(PriceLevelAccountProductGroupResource.class);

	@Inject
	private PriceLevelAccountProductGroupService priceLevelAccountProductGroupService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private PriceLevelListService priceLevelListService;

	@Inject
	private EcomProductProfileProductService ecomProductProfileProductService;

	@Inject
	private ProductGroupEcomProductsService productGroupEcomProductsService;

	/**
	 * POST /price-level-account-product-group : Create a new
	 * priceLevelAccountProductGroup.
	 *
	 * @param priceLevelAccountProductGroupDTO
	 *            the priceLevelAccountProductGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new priceLevelAccountProductGroupDTO, or with status 400 (Bad
	 *         Request) if the priceLevelAccountProductGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-level-account-product-group", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PriceLevelAccountProductGroupDTO> createPriceLevelAccountProductGroup(
			@Valid @RequestBody PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO)
			throws URISyntaxException {
		log.debug("Web request to save PriceLevelAccountProductGroup : {}", priceLevelAccountProductGroupDTO);
		if (priceLevelAccountProductGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceLevelAccountProductGroup",
					"pidexists", "A new priceLevelAccountProductGroup cannot already have an ID")).body(null);
		}

		PriceLevelAccountProductGroupDTO result = priceLevelAccountProductGroupService
				.save(priceLevelAccountProductGroupDTO);
		return ResponseEntity
				.created(new URI("/web/price-level-account-product-group/" + result.getPid())).headers(HeaderUtil
						.createEntityCreationAlert("priceLevelAccountProductGroup", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /price-level-account-product-group : Updates an existing
	 * priceLevelAccountProductGroup.
	 *
	 * @param priceLevelAccountProductGroupDTO
	 *            the priceLevelAccountProductGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         priceLevelAccountProductGroupDTO, or with status 400 (Bad
	 *         Request) if the priceLevelAccountProductGroupDTO is not valid, or
	 *         with status 500 (Internal Server Error) if the
	 *         priceLevelAccountProductGroupDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-level-account-product-group", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceLevelAccountProductGroupDTO> updatePriceLevelAccountProductGroup(
			@Valid @RequestBody PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO)
			throws URISyntaxException {
		log.debug("REST request to update PriceLevelAccountProductGroup : {}", priceLevelAccountProductGroupDTO);
		if (priceLevelAccountProductGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceLevelAccountProductGroup",
					"pidNotexists", "PriceLevelAccountProductGroup must have an ID")).body(null);
		}

		PriceLevelAccountProductGroupDTO result = priceLevelAccountProductGroupService
				.update(priceLevelAccountProductGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceLevelAccountProductGroup",
					"idNotexists", "Invalid PriceLevelAccountProductGroup ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("priceLevelAccountProductGroup",
				priceLevelAccountProductGroupDTO.getPid().toString())).body(result);
	}

	@RequestMapping(value = "/price-level-account-product-group/saveAccountAndProductGroup", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<List<PriceLevelAccountProductGroupDTO>> filterAccountProfilesByAccountTypes(
			@RequestParam String accountPids, @RequestParam String productGroupPids, @RequestParam String priceLevelPid)
			throws URISyntaxException {
		priceLevelAccountProductGroupService.saveMultiPriceLevelAccountProductGroup(accountPids, productGroupPids,
				priceLevelPid);
		return ResponseEntity.ok().body(null);
	}

	/**
	 * GET /price-level-account-product-group : get all the
	 * priceLevelAccountProductGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         priceLevelAccountProductGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/price-level-account-product-group", method = RequestMethod.GET)
	@Timed
	public String getAllPriceLevelAccountProductGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PriceLevelAccountProductGroups");
		List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroups = priceLevelAccountProductGroupService
				.findAllByCompany();
		model.addAttribute("priceLevelAccountProductGroups", priceLevelAccountProductGroups);
		model.addAttribute("productGroups", productGroupService.findAllByCompany());
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/priceLevelAccountProductGroup";
	}

	@RequestMapping(value = "/price-level-account-product-group/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceLevelAccountProductGroupDTO> findOnePriceLevelAccountProductGroup(
			@PathVariable String pid) {
		log.debug("REST request to find PriceLevelAccountProductGroup : {}", pid);
		PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO = priceLevelAccountProductGroupService
				.findOneByPid(pid).get();
		return new ResponseEntity<PriceLevelAccountProductGroupDTO>(priceLevelAccountProductGroupDTO, HttpStatus.OK);
	}

	/**
	 * DELETE /price-level-account-product-group/:pid : delete the "pid"
	 * priceLevelAccountProductGroup.
	 *
	 * @param pid
	 *            the pid of the priceLevelAccountProductGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-level-account-product-group/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deletePriceLevelAccountProductGroup(@PathVariable String pid) {
		log.debug("REST request to delete PriceLevelAccountProductGroup : {}", pid);
		priceLevelAccountProductGroupService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("priceLevelAccountProductGroup", pid.toString())).build();
	}

	@RequestMapping(value = "/price-level-account-product-group/load-productgroup-by-price", method = RequestMethod.GET)
	public @ResponseBody List<ProductGroupDTO> getProductGroupsByPriceLevel(
			@RequestParam(value = "priceLevelPid") String priceLevelPid) {
		log.debug("REST request to load Product Group by Price Level : {}", priceLevelPid);
		List<ProductProfileDTO> productProfileDTOs = priceLevelListService
				.findProductProfileByPriceLevel(priceLevelPid);
		
		List<String> ecomProductProfilePids = ecomProductProfileProductService
				.findEcomProductProfilePidsByProductPorfileIn(productProfileDTOs);
		
		List<ProductGroupDTO> productGroupDTOs = productGroupEcomProductsService
				.findAllProductGroupByEcomProductPidIn(ecomProductProfilePids);
		return productGroupDTOs;
	}
}
