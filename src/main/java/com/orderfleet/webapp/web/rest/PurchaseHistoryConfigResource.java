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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.PurchaseHistoryConfigService;
import com.orderfleet.webapp.web.rest.dto.PurchaseHistoryConfigDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing PurchaseHistoryConfig.
 * 
 * @author Muhammed Riyas T
 * @since Feb 06, 2017
 */
@Controller
@RequestMapping("/web")
public class PurchaseHistoryConfigResource {

	private final Logger log = LoggerFactory.getLogger(PurchaseHistoryConfigResource.class);

	@Inject
	private PurchaseHistoryConfigService purchaseHistoryConfigService;

	/**
	 * GET /purchaseHistoryConfigs : get all the purchaseHistoryConfigs.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         purchaseHistoryConfigs in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/purchaseHistoryConfigs", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllPurchaseHistoryConfigs(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of PurchaseHistoryConfigs");
		model.addAttribute("pagePurchaseHistoryConfigs", purchaseHistoryConfigService.findAllByCompany());
		return "company/purchaseHistoryConfigs";
	}

	/**
	 * POST /purchaseHistoryConfigs : Create a new purchaseHistoryConfig.
	 *
	 * @param purchaseHistoryConfigDTO
	 *            the purchaseHistoryConfigDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new purchaseHistoryConfigDTO, or with status 400 (Bad Request) if
	 *         the purchaseHistoryConfig has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/purchaseHistoryConfigs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PurchaseHistoryConfigDTO> createPurchaseHistoryConfig(
			@Valid @RequestBody PurchaseHistoryConfigDTO purchaseHistoryConfigDTO) throws URISyntaxException {
		log.debug("Web request to save PurchaseHistoryConfig : {}", purchaseHistoryConfigDTO);
		if (purchaseHistoryConfigDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("purchaseHistoryConfig",
					"idexists", "A new purchaseHistoryConfig cannot already have an ID")).body(null);
		}
		if (purchaseHistoryConfigService.findByName(purchaseHistoryConfigDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("purchaseHistoryConfig", "nameexists", "Account type already in use"))
					.body(null);
		}
		PurchaseHistoryConfigDTO result = purchaseHistoryConfigService.save(purchaseHistoryConfigDTO);
		return ResponseEntity.created(new URI("/web/purchaseHistoryConfigs/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("purchaseHistoryConfig", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /purchaseHistoryConfigs : Updates an existing purchaseHistoryConfig.
	 *
	 * @param purchaseHistoryConfigDTO
	 *            the purchaseHistoryConfigDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         purchaseHistoryConfigDTO, or with status 400 (Bad Request) if the
	 *         purchaseHistoryConfigDTO is not valid, or with status 500
	 *         (Internal Server Error) if the purchaseHistoryConfigDTO couldnt
	 *         be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/purchaseHistoryConfigs", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PurchaseHistoryConfigDTO> updatePurchaseHistoryConfig(
			@Valid @RequestBody PurchaseHistoryConfigDTO purchaseHistoryConfigDTO) throws URISyntaxException {
		log.debug("Web request to update PurchaseHistoryConfig : {}", purchaseHistoryConfigDTO);
		if (purchaseHistoryConfigDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("purchaseHistoryConfig",
					"idNotexists", "Account Type must have an ID")).body(null);
		}
		Optional<PurchaseHistoryConfigDTO> existingPurchaseHistoryConfig = purchaseHistoryConfigService
				.findByName(purchaseHistoryConfigDTO.getName());
		if (existingPurchaseHistoryConfig.isPresent()
				&& (!existingPurchaseHistoryConfig.get().getPid().equals(purchaseHistoryConfigDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("purchaseHistoryConfig", "nameexists", "Account type already in use"))
					.body(null);
		}
		PurchaseHistoryConfigDTO result = purchaseHistoryConfigService.update(purchaseHistoryConfigDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("purchaseHistoryConfig", "idNotexists", "Invalid account type ID"))
					.body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("purchaseHistoryConfig",
				purchaseHistoryConfigDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /purchaseHistoryConfigs/:id : get the "id" purchaseHistoryConfig.
	 *
	 * @param id
	 *            the id of the purchaseHistoryConfigDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         purchaseHistoryConfigDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/purchaseHistoryConfigs/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PurchaseHistoryConfigDTO> getPurchaseHistoryConfig(@PathVariable String pid) {
		log.debug("Web request to get PurchaseHistoryConfig by pid : {}", pid);
		return purchaseHistoryConfigService.findOneByPid(pid)
				.map(purchaseHistoryConfigDTO -> new ResponseEntity<>(purchaseHistoryConfigDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /purchaseHistoryConfigs/:id : delete the "id"
	 * purchaseHistoryConfig.
	 *
	 * @param id
	 *            the id of the purchaseHistoryConfigDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/purchaseHistoryConfigs/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deletePurchaseHistoryConfig(@PathVariable String pid) {
		log.debug("REST request to delete PurchaseHistoryConfig : {}", pid);
		purchaseHistoryConfigService.delete(pid);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("purchaseHistoryConfig", pid.toString())).build();
	}

}
