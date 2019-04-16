package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.orderfleet.webapp.service.PriceLevelService;

import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing PriceLevel.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
@Controller
@RequestMapping("/web")
public class PriceLevelResource {

	private final Logger log = LoggerFactory.getLogger(PriceLevelResource.class);

	@Inject
	private PriceLevelService priceLevelService;

	/**
	 * POST /price-levels : Create a new priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the priceLevelDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new priceLevelDTO, or with status 400 (Bad Request) if the
	 *         priceLevel has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-levels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PriceLevelDTO> createPriceLevel(@Valid @RequestBody PriceLevelDTO priceLevelDTO)
			throws URISyntaxException {
		log.debug("Web request to save PriceLevel : {}", priceLevelDTO);
		if (priceLevelDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceLevel", "idexists",
					"A new priceLevel cannot already have an ID")).body(null);
		}
		if (priceLevelService.findByName(priceLevelDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("priceLevel", "nameexists", "PriceLevel already in use"))
					.body(null);
		}
		priceLevelDTO.setActivated(true);
		PriceLevelDTO result = priceLevelService.save(priceLevelDTO);
		return ResponseEntity.created(new URI("/web/price-levels/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("priceLevel", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /price-levels : Updates an existing priceLevel.
	 *
	 * @param priceLevelDTO
	 *            the priceLevelDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         priceLevelDTO, or with status 400 (Bad Request) if the
	 *         priceLevelDTO is not valid, or with status 500 (Internal Server
	 *         Error) if the priceLevelDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-levels", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceLevelDTO> updatePriceLevel(@Valid @RequestBody PriceLevelDTO priceLevelDTO)
			throws URISyntaxException {
		log.debug("Web request to update PriceLevel : {}", priceLevelDTO);
		if (priceLevelDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("priceLevel", "idNotexists", "PriceLevel must have an ID"))
					.body(null);
		}
		Optional<PriceLevelDTO> existingPriceLevel = priceLevelService.findByName(priceLevelDTO.getName());
		if (existingPriceLevel.isPresent() && (!existingPriceLevel.get().getPid().equals(priceLevelDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("priceLevel", "nameexists", "PriceLevel already in use"))
					.body(null);
		}
		PriceLevelDTO result = priceLevelService.update(priceLevelDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("priceLevel", "idNotexists", "Invalid PriceLevel ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("priceLevel", priceLevelDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /price-levels : get all the priceLevels.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         priceLevels in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/price-levels", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllPriceLevels(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PriceLevels");
		model.addAttribute("priceLevels",
				priceLevelService.findAllByCompanyIdAndDeactivatedPriceLevel(true));
		model.addAttribute("deactivatedPriceLevels",
				priceLevelService.findAllByCompanyIdAndDeactivatedPriceLevel(false));
		return "company/priceLevels";
	}

	/**
	 * GET /price-levels/:id : get the "id" priceLevel.
	 *
	 * @param id
	 *            the id of the priceLevelDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         priceLevelDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/price-levels/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PriceLevelDTO> getPriceLevel(@PathVariable String pid) {
		log.debug("Web request to get PriceLevel by pid : {}", pid);
		return priceLevelService.findOneByPid(pid)
				.map(priceLevelDTO -> new ResponseEntity<>(priceLevelDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /price-levels/:id : delete the "id" priceLevel.
	 *
	 * @param id
	 *            the id of the priceLevelDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/price-levels/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deletePriceLevel(@PathVariable String pid) {
		log.debug("REST request to delete PriceLevel : {}", pid);
		priceLevelService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("priceLevel", pid.toString())).build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /price-levels/changeStatus:priceLevelDTO : update
	 *        status of priceLevel.
	 * 
	 * @param priceLevelDTO
	 *            the priceLevelDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/price-levels/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PriceLevelDTO> updateKnowledgebaseStatus(@Valid @RequestBody PriceLevelDTO priceLevelDTO) {
		log.debug("Web request to update status priceLevel : {}", priceLevelDTO);
		PriceLevelDTO result = priceLevelService.updatePriceLevelStatus(priceLevelDTO.getPid(),
				priceLevelDTO.getActivated());
		return new ResponseEntity<PriceLevelDTO>(result, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /price-levels/activatePriceLevel : activate status
	 *        of pricelevel.
	 * 
	 * @param pricelevels
	 *            the priceLevels to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/price-levels/activatePriceLevel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PriceLevelDTO> activatePriceLevel(@Valid @RequestParam String pricelevels) {
		log.debug("Web request to activate priceLevel : {}");
		String[] pricelevel = pricelevels.split(",");
		for (String pricelevelpid : pricelevel) {
			priceLevelService.updatePriceLevelStatus(pricelevelpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
