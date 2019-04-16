package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.EcomProductProfile;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.EcomProductProfileProductRepository;
import com.orderfleet.webapp.repository.ProductGroupEcomProductsRepository;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductProfileMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing PriceLevelList.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
@Controller
@RequestMapping("/web")
public class PriceLevelListResource {

	private final Logger log = LoggerFactory.getLogger(PriceLevelListResource.class);

	@Inject
	private PriceLevelListService priceLevelListService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private ProductGroupProductService productGroupProductService;

	@Inject
	private ProductGroupEcomProductsRepository productGroupEcomProductsRepository;
	
	@Inject
	private EcomProductProfileProductRepository ecomProductProfileProductRepository;
	
	@Inject
	private ProductProfileMapper productProfileMapper;
	

	/**
	 * POST /price-level-list : Create a new priceLevelList.
	 *
	 * @param priceLevelListDTO
	 *            the priceLevelListDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new priceLevelListDTO, or with status 400 (Bad Request) if the
	 *         priceLevelList has already an IDF
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-level-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PriceLevelListDTO> createPriceLevelList(
			@Valid @RequestBody PriceLevelListDTO priceLevelListDTO) throws URISyntaxException {
		log.debug("Web request to save PriceLevelList : {}", priceLevelListDTO);
		if (priceLevelListDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceLevelList", "idexists",
					"A new priceLevelList cannot already have an ID")).body(null);
		}
		if (priceLevelListDTO.getRangeFrom() > priceLevelListDTO.getRangeTo()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("priceLevelList", "rangeInvalid", "Invalid range"))
					.body(null);
		}
		List<PriceLevelListDTO> priceLevelListDTOs = priceLevelListService
				.findAllByCompanyIdAndPriceLevelPid(priceLevelListDTO.getPriceLevelPid());
		if (priceLevelListDTOs.size() == 0) {
			if (priceLevelListDTO.getRangeFrom() > 1 || priceLevelListDTO.getRangeFrom() < 0) {
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("priceLevelList", "rangeInvalid", "Invalid range"))
						.body(null);
			}
		}
		PriceLevelListDTO result = priceLevelListService.save(priceLevelListDTO);
		return ResponseEntity.created(new URI("/web/price-level-list/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("priceLevelList", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /price-level-list : Updates an existing priceLevelList.
	 *
	 * @param priceLevelListDTO
	 *            the priceLevelListDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         priceLevelListDTO, or with status 400 (Bad Request) if the
	 *         priceLevelListDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the priceLevelListDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-level-list", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceLevelListDTO> updatePriceLevelList(
			@Valid @RequestBody PriceLevelListDTO priceLevelListDTO) throws URISyntaxException {
		log.debug("Web request to update PriceLevelList : {}", priceLevelListDTO);
		if (priceLevelListDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("priceLevelList", "idNotexists", "PriceLevelList must have an ID"))
					.body(null);
		}
		if (priceLevelListDTO.getRangeFrom() > priceLevelListDTO.getRangeTo()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("priceLevelList", "rangeInvalid", "Invalid range"))
					.body(null);
		}
		PriceLevelListDTO result = priceLevelListService.update(priceLevelListDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("priceLevelList", "idNotexists", "Invalid PriceLevelList ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("priceLevelList", priceLevelListDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /price-level-list : get all the priceLevelLists.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         priceLevelLists in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/price-level-list", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllPriceLevelLists(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PriceLevelLists");
		model.addAttribute("categories", productCategoryService.findAllByCompany());
		model.addAttribute("groups", productGroupService.findAllByCompany());
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());
		return "company/priceLevelList";
	}

	/**
	 * GET /price-level-list/load-products : get product list.
	 *
	 * @param filterBy
	 * @param pid
	 *            t
	 * @return the ResponseEntity with status 200 (OK) and with body the list
	 *         productDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/price-level-list/load-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getProducts(@RequestParam String filterBy,
			@RequestParam String pid) {
		log.debug("Web request to get product list : {}", filterBy, pid);
		List<ProductProfileDTO> productProfileDTOs = null;
		if (filterBy.equals("Category")) {
			productProfileDTOs = productProfileService.findByProductCategoryPid(pid);
		} else {
			List<EcomProductProfile> ecomProductProfiles = productGroupEcomProductsRepository.findEcomProductByProductGroupPid(pid);
			if (ecomProductProfiles.isEmpty()) {
				 productProfileDTOs = productGroupProductService
						.findProductByProductGroupPid(pid);
			} else {
				//if ecom productgroup, then find productprofiles from ecomProductProfileProduct
				Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid).collect(Collectors.toSet());
				List<ProductProfile> productProfiles = ecomProductProfileProductRepository.findProductByEcomProductProfilePidIn(ecomProductProfilesPids);
				productProfileDTOs=productProfileMapper.productProfilesToProductProfileDTOs(productProfiles);
			}
		}
		return new ResponseEntity<>(productProfileDTOs, HttpStatus.OK);
	}

	/**
	 * GET /price-level-list/:id : get the "id" priceLevelList.
	 *
	 * @param id
	 *            the id of the priceLevelListDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         priceLevelListDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/price-level-list/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceLevelListDTO> getPriceLevelList(@PathVariable String pid) {
		log.debug("Web request to get PriceLevelList by pid : {}", pid);
		return priceLevelListService.findOneByPid(pid)
				.map(priceLevelListDTO -> new ResponseEntity<>(priceLevelListDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /price-level-list/:id : delete the "id" priceLevelList.
	 *
	 * @param id
	 *            the id of the priceLevelListDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-level-list/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deletePriceLevelList(@PathVariable String pid) {
		log.debug("REST request to delete PriceLevelList : {}", pid);
		priceLevelListService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("priceLevelList", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/price-level-list/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceLevelListDTO>> filterPriceLevelList(
			@RequestParam("priceLevelPid") String priceLevelPid, @RequestParam("productPid") String productPid,
			@RequestParam("filterBy") String filterBy, @RequestParam("filterByPid") String filterByPid) {
		log.debug("Web request to filter Price Level List");

		List<PriceLevelListDTO> priceLevelList = new ArrayList<PriceLevelListDTO>();
		if (filterBy.equals("Category")) {
			if (priceLevelPid.equals("no") && productPid.equals("no") && filterByPid.equals("no")) {
				priceLevelList = priceLevelListService.findAllByCompany();
			} else if (priceLevelPid.equals("no") && productPid.equals("no") && !filterByPid.equals("no")) {
				priceLevelList = priceLevelListService.findAllByCompanyIdAndProductCategoryPid(filterByPid);
			} else if (priceLevelPid.equals("no") && !productPid.equals("no") && !filterByPid.equals("no")) {
				priceLevelList = priceLevelListService
						.findAllByCompanyIdAndProductProfilePidAndProductCategoryPid(productPid, filterByPid);
			} else if (!priceLevelPid.equals("no") && productPid.equals("no") && filterByPid.equals("no")) {
				priceLevelList = priceLevelListService.findAllByCompanyIdAndPriceLevelPid(priceLevelPid);
			} else if (!priceLevelPid.equals("no") && productPid.equals("no") && !filterByPid.equals("no")) {
				priceLevelList = priceLevelListService
						.findAllByCompanyIdAndPriceLevelPidAndProductCategoryPid(priceLevelPid, filterByPid);
			} else if (!priceLevelPid.equals("no") && !productPid.equals("no") && !filterByPid.equals("no")) {
				priceLevelList = priceLevelListService
						.findAllByCompanyIdAndPriceLevelPidAndProductProfilePidAndProductCategoryPid(priceLevelPid,
								productPid, filterByPid);
			}
		}
		else {
			if (priceLevelPid.equals("no") && productPid.equals("no") && filterByPid.equals("no")) {
				priceLevelList = priceLevelListService.findAllByCompany();
			} else if (priceLevelPid.equals("no") && productPid.equals("no") && !filterByPid.equals("no")) {
				//Find ecomproductGroup
				List<EcomProductProfile> ecomProductProfiles = productGroupEcomProductsRepository.findEcomProductByProductGroupPid(filterByPid);
				List<String> productProfilePids = new ArrayList<>();
				if (ecomProductProfiles.isEmpty()) {
					List<ProductProfileDTO> productProfileDTOs = productGroupProductService
							.findProductByProductGroupPid(filterByPid);
					productProfilePids = productProfileDTOs.stream().map(ProductProfileDTO::getPid).collect(Collectors.toList());
				} else {
					//if ecom productgroup, then find productprofiles from ecomProductProfileProduct
					Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid).collect(Collectors.toSet());
					List<ProductProfile> productProfiles = ecomProductProfileProductRepository.findProductByEcomProductProfilePidIn(ecomProductProfilesPids);
					List<String>	productProfilePidsDuplicate = productProfiles.stream().map(ProductProfile::getPid)
							.collect(Collectors.toList());
					productProfilePids= productProfilePidsDuplicate.stream().distinct().collect(Collectors.toList());
				}
				priceLevelList = priceLevelListService.findAllByCompanyIdAndProductProfilePidIn(productProfilePids);
			} else if (priceLevelPid.equals("no") && !productPid.equals("no") && !filterByPid.equals("no")) {
				priceLevelList = priceLevelListService.findAllByCompanyIdAndProductProfilePid(productPid);
			} else if (!priceLevelPid.equals("no") && productPid.equals("no") && filterByPid.equals("no")) {
				priceLevelList = priceLevelListService.findAllByCompanyIdAndPriceLevelPid(priceLevelPid);
			} else if (!priceLevelPid.equals("no") && productPid.equals("no") && !filterByPid.equals("no")) {
				//Find ecomproductGroup
				List<EcomProductProfile> ecomProductProfiles = productGroupEcomProductsRepository.findEcomProductByProductGroupPid(filterByPid);
				List<String> productProfilePids = new ArrayList<>();
				if (ecomProductProfiles.isEmpty()) {
					List<ProductProfileDTO> productProfileDTOs = productGroupProductService
							.findProductByProductGroupPid(filterByPid);
					productProfilePids = productProfileDTOs.stream().map(ProductProfileDTO::getPid).collect(Collectors.toList());
				} else {
					//if ecom productgroup, then find productprofiles from ecomProductProfileProduct
					Set<String> ecomProductProfilesPids = ecomProductProfiles.stream().map(EcomProductProfile::getPid).collect(Collectors.toSet());
					List<ProductProfile> productProfiles = ecomProductProfileProductRepository.findProductByEcomProductProfilePidIn(ecomProductProfilesPids);
					List<String>	productProfilePidsDuplicate= productProfiles.stream().map(ProductProfile::getPid)
							.collect(Collectors.toList());
					productProfilePids= productProfilePidsDuplicate.stream().distinct().collect(Collectors.toList());
				}
				priceLevelList = priceLevelListService
						.findAllByCompanyIdAndPriceLevelPidAndProductProfilePidIn(priceLevelPid, productProfilePids);
			} else if (!priceLevelPid.equals("no") && !productPid.equals("no") && !filterByPid.equals("no")) {
				priceLevelList = priceLevelListService
						.findAllByCompanyIdPriceLevelPidAndProductProfilePid(priceLevelPid, productPid);
			}
		}

		return new ResponseEntity<>(priceLevelList, HttpStatus.OK);
	}

}
