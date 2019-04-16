package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.service.PriceTrendProductService;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing PriceTrendProductGroup.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Controller
@RequestMapping("/web")
public class PriceTrendProductGroupResource {

	private final Logger log = LoggerFactory.getLogger(PriceTrendProductGroupResource.class);

	@Inject
	private PriceTrendProductGroupService priceTrendProductGroupService;

	@Inject
	private PriceTrendProductService priceTrendProductService;

	/**
	 * POST /price-trend-product-groups : Create a new priceTrendProductGroup.
	 *
	 * @param priceTrendProductGroupDTO
	 *            the priceTrendProductGroupDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new priceTrendProductGroupDTO, or with status 400 (Bad Request)
	 *         if the priceTrendProductGroup has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-trend-product-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PriceTrendProductGroupDTO> createPriceTrendProductGroup(
			@Valid @RequestBody PriceTrendProductGroupDTO priceTrendProductGroupDTO) throws URISyntaxException {
		log.debug("Web request to save PriceTrendProductGroup : {}", priceTrendProductGroupDTO);
		if (priceTrendProductGroupDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProductGroup",
					"idexists", "A new priceTrendProductGroup cannot already have an ID")).body(null);
		}
		if (priceTrendProductGroupService.findByName(priceTrendProductGroupDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProductGroup",
					"nameexists", "PriceTrendProductGroup already in use")).body(null);
		}
		priceTrendProductGroupDTO.setActivated(true);
		PriceTrendProductGroupDTO result = priceTrendProductGroupService.save(priceTrendProductGroupDTO);
		return ResponseEntity.created(new URI("/web/price-trend-product-groups/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("priceTrendProductGroup", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /price-trend-product-groups : Updates an existing
	 * priceTrendProductGroup.
	 *
	 * @param priceTrendProductGroupDTO
	 *            the priceTrendProductGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         priceTrendProductGroupDTO, or with status 400 (Bad Request) if
	 *         the priceTrendProductGroupDTO is not valid, or with status 500
	 *         (Internal Server Error) if the priceTrendProductGroupDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-trend-product-groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceTrendProductGroupDTO> updatePriceTrendProductGroup(
			@Valid @RequestBody PriceTrendProductGroupDTO priceTrendProductGroupDTO) throws URISyntaxException {
		log.debug("REST request to update PriceTrendProductGroup : {}", priceTrendProductGroupDTO);
		if (priceTrendProductGroupDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProductGroup",
					"idNotexists", "PriceTrendProductGroup must have an ID")).body(null);
		}
		Optional<PriceTrendProductGroupDTO> existingPriceTrendProductGroup = priceTrendProductGroupService
				.findByName(priceTrendProductGroupDTO.getName());
		if (existingPriceTrendProductGroup.isPresent()
				&& (!existingPriceTrendProductGroup.get().getPid().equals(priceTrendProductGroupDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProductGroup",
					"nameexists", "PriceTrendProductGroup already in use")).body(null);
		}
		PriceTrendProductGroupDTO result = priceTrendProductGroupService.update(priceTrendProductGroupDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProductGroup",
					"idNotexists", "Invalid PriceTrendProductGroup ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("priceTrendProductGroup",
				priceTrendProductGroupDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /price-trend-product-groups : get all the priceTrendProductGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         priceTrendProductGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/price-trend-product-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllPriceTrendProductGroups(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PriceTrendProductGroups");
		Page<PriceTrendProductGroupDTO> priceTrendProductGroups = priceTrendProductGroupService
				.findAllByCompanyAndPriceTrendProductGroupActivatedOrderByName(pageable, true);
		List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs = priceTrendProductGroupService
				.findAllByCompanyIdAndPriceTrendProductGroupActivated(false);
		model.addAttribute("priceTrendProductGroups", priceTrendProductGroups);
		model.addAttribute("deactivatedpriceTrendProductGroups", priceTrendProductGroupDTOs);
		model.addAttribute("products", priceTrendProductService.findAllByCompanyIdAndPriceTrendProductActivated(true));
		return "company/priceTrendProductGroups";
	}

	/**
	 * GET /price-trend-product-groups/:pid : get the "pid"
	 * priceTrendProductGroup.
	 *
	 * @param pid
	 *            the pid of the priceTrendProductGroupDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         priceTrendProductGroupDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/price-trend-product-groups/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceTrendProductGroupDTO> getPriceTrendProductGroup(@PathVariable String pid) {
		log.debug("Web request to get PriceTrendProductGroup by pid : {}", pid);
		return priceTrendProductGroupService.findOneByPid(pid)
				.map(priceTrendProductGroupDTO -> new ResponseEntity<>(priceTrendProductGroupDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /price-trend-product-groups/:id : delete the "id"
	 * priceTrendProductGroup.
	 *
	 * @param id
	 *            the id of the priceTrendProductGroupDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-trend-product-groups/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deletePriceTrendProductGroup(@PathVariable String pid) {
		log.debug("REST request to delete PriceTrendProductGroup : {}", pid);
		priceTrendProductGroupService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("priceTrendProductGroup", pid.toString())).build();
	}

	@RequestMapping(value = "/price-trend-product-groups/assign-products", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedProducts(@RequestParam String pid, @RequestParam String assignedProducts) {
		log.debug("REST request to save assigned Products : {}", pid);
		priceTrendProductGroupService.saveAssignedProducts(pid, assignedProducts);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS
	 *        /price-trend-product-groups/changeStatus:priceTrendProductGroupDTO
	 *        : update status of priceTrendProductGroup.
	 * 
	 * @param priceTrendProductGroupDTO
	 *            the priceTrendProductGroupDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-trend-product-groups/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PriceTrendProductGroupDTO> updatePriceTrendProductGroupStatus(
			@Valid @RequestBody PriceTrendProductGroupDTO priceTrendProductGroupDTO) {
		log.debug("Web request to update status of PriceTrendProductGroup: {}", priceTrendProductGroupDTO);
		PriceTrendProductGroupDTO res = priceTrendProductGroupService.updatePriceTrendProductGroupStatus(
				priceTrendProductGroupDTO.getPid(), priceTrendProductGroupDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        Activate STATUS
	 *        /price-trend-product-groups/activatepriceTrendProductGroup :
	 *        activate status of priceTrendProductGroupDTO.
	 * 
	 * @param pricetrendproductgroup
	 *            the pricetrendproductgroup to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/price-trend-product-groups/activatepriceTrendProductGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PriceTrendProductGroupDTO> activatePriceTrendProductGroup(
			@Valid @RequestParam String pricetrendproductgroup) {
		String[] pricetrendgroup = pricetrendproductgroup.split(",");
		for (String price : pricetrendgroup) {
			priceTrendProductGroupService.updatePriceTrendProductGroupStatus(price, true);
		}
		return new ResponseEntity<PriceTrendProductGroupDTO>(HttpStatus.OK);
	}
}
