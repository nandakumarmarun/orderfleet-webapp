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
import com.orderfleet.webapp.service.CompetitorProfileService;
import com.orderfleet.webapp.service.PriceTrendProductCompetitorService;
import com.orderfleet.webapp.service.PriceTrendProductService;

import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing PriceTrendProduct.
 * 
 * @author Sarath
 * @since Aug 27, 2016
 */
@Controller
@RequestMapping("/web")
public class PriceTrendProductResource {

	private final Logger log = LoggerFactory.getLogger(PriceTrendProductResource.class);

	@Inject
	private PriceTrendProductService priceTrendProductService;

	@Inject
	private CompetitorProfileService competitorProfileService;

	@Inject
	private PriceTrendProductCompetitorService priceTrendProductCompetitorService;

	/**
	 * POST /price-trend-products : Create a new priceTrendProduct.
	 *
	 * @param priceTrendProductDTO
	 *            the priceTrendProductDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new priceTrendProductDTO, or with status 400 (Bad Request) if the
	 *         priceTrendProduct has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-trend-products", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PriceTrendProductDTO> createPriceTrendProduct(
			@Valid @RequestBody PriceTrendProductDTO priceTrendProductDTO) throws URISyntaxException {
		log.debug("Web request to save PriceTrendProduct : {}", priceTrendProductDTO);
		if (priceTrendProductDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProduct", "idexists",
					"A new priceTrendProduct cannot already have an ID")).body(null);
		}
		if (priceTrendProductService.findByName(priceTrendProductDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProduct", "nameexists",
					"PriceTrendProduct already in use")).body(null);
		}
		priceTrendProductDTO.setActivated(true);
		PriceTrendProductDTO result = priceTrendProductService.save(priceTrendProductDTO);
		return ResponseEntity.created(new URI("/web/price-trend-products/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("priceTrendProduct", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /price-trend-products : Updates an existing priceTrendProduct.
	 *
	 * @param priceTrendProductDTO
	 *            the priceTrendProductDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         priceTrendProductDTO, or with status 400 (Bad Request) if the
	 *         priceTrendProductDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the priceTrendProductDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-trend-products", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceTrendProductDTO> updatePriceTrendProduct(
			@Valid @RequestBody PriceTrendProductDTO priceTrendProductDTO) throws URISyntaxException {
		log.debug("REST request to update PriceTrendProduct : {}", priceTrendProductDTO);
		if (priceTrendProductDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProduct", "idNotexists",
					"PriceTrendProduct must have an ID")).body(null);
		}
		Optional<PriceTrendProductDTO> existingPriceTrendProduct = priceTrendProductService
				.findByName(priceTrendProductDTO.getName());
		if (existingPriceTrendProduct.isPresent()
				&& (!existingPriceTrendProduct.get().getPid().equals(priceTrendProductDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceTrendProduct", "nameexists",
					"PriceTrendProduct already in use")).body(null);
		}
		PriceTrendProductDTO result = priceTrendProductService.update(priceTrendProductDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("priceTrendProduct", "idNotexists", "Invalid PriceTrendProduct ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityUpdateAlert("priceTrendProduct", priceTrendProductDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * 
	 * 
	 * GET /price-trend-products : get all the priceTrendProducts.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         priceTrendProducts in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/price-trend-products", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllPriceTrendProducts(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PriceTrendProducts");
		Page<PriceTrendProductDTO> priceTrendProducts = priceTrendProductService
				.findAllByCompanyAndPriceTrendProductActivatedOrderByName(pageable, true);
		List<PriceTrendProductDTO> priceTrendProductDTOs = priceTrendProductService
				.findAllByCompanyIdAndPriceTrendProductActivated(false);
		model.addAttribute("deactivatedPriceTrendProducts", priceTrendProductDTOs);
		model.addAttribute("priceTrendProducts", priceTrendProducts);
		model.addAttribute("competitors",
				competitorProfileService.findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(true));
		return "company/priceTrendProducts";
	}

	/**
	 * GET /price-trend-products/:pid : get the "pid" priceTrendProduct.
	 *
	 * @param pid
	 *            the pid of the priceTrendProductDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         priceTrendProductDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/price-trend-products/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceTrendProductDTO> getPriceTrendProduct(@PathVariable String pid) {
		log.debug("Web request to get PriceTrendProduct by pid : {}", pid);
		return priceTrendProductService.findOneByPid(pid)
				.map(priceTrendProductDTO -> new ResponseEntity<>(priceTrendProductDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /price-trend-products/:id : delete the "id" priceTrendProduct.
	 *
	 * @param id
	 *            the id of the priceTrendProductDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-trend-products/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deletePriceTrendProduct(@PathVariable String pid) {
		log.debug("REST request to delete PriceTrendProduct : {}", pid);
		priceTrendProductService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("priceTrendProduct", pid.toString()))
				.build();
	}

	@RequestMapping(value = "/price-trend-products/competitors", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<CompetitorProfileDTO>> priceTrendProductCompetitors(
			@RequestParam String priceTrendProductPid) {
		log.debug("REST request to priceTrendProduct competitors : {}", priceTrendProductPid);
		List<CompetitorProfileDTO> competitorProfileDTOs = priceTrendProductCompetitorService
				.findCompetitorsByPriceTrendProductPid(priceTrendProductPid);
		return new ResponseEntity<>(competitorProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/price-trend-products/assign-competitors", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedPriceLevels(@RequestParam String priceTrendProductPid,
			@RequestParam String assignedCompetitors) {
		log.debug("REST request to save assigned Price Levels : {}", priceTrendProductPid);
		priceTrendProductCompetitorService.save(priceTrendProductPid, assignedCompetitors);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS
	 *        /price-trend-products/changeStatus:priceTrendProductDTO : update
	 *        status of priceTrendProduct.
	 * 
	 * @param priceTrendProductDTO
	 *            the priceTrendProductDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-trend-products/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PriceTrendProductDTO> updatePriceTrendProductStatus(
			@Valid @RequestBody PriceTrendProductDTO priceTrendProductDTO) {
		log.debug("Web request to update status PriceTrendProduct : {}", priceTrendProductDTO);
		PriceTrendProductDTO res = priceTrendProductService.updatePriceTrendProductStatus(priceTrendProductDTO.getPid(),
				priceTrendProductDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 11, 2017
	 * 
	 *        Activate STATUS
	 *        /price-trend-products/activatePriceTrendProduct:pricetrendproduct
	 *        : update status of priceTrendProduct.
	 * 
	 * @param pricetrendproduct
	 *            the pricetrendproduct to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/price-trend-products/activatePriceTrendProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PriceTrendProductDTO> activatePriceTrendProduct(
			@Valid @RequestParam String pricetrendproduct) {
		String[] pricetrendproductString = pricetrendproduct.split(",");
		for (String pricetrendpid : pricetrendproductString) {
			priceTrendProductService.updatePriceTrendProductStatus(pricetrendpid, true);
		}
		return new ResponseEntity<PriceTrendProductDTO>(HttpStatus.OK);

	}
}
