package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.orderfleet.webapp.service.StockLocationService;

import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing StockLocation.
 * 
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
@Controller
@RequestMapping("/web")
public class StockLocationResource {

	private final Logger log = LoggerFactory.getLogger(StockLocationResource.class);

	@Inject
	private StockLocationService stockLocationService;

	/**
	 * POST /stock-locations : Create a new stockLocation.
	 *
	 * @param stockLocationDTO
	 *            the stockLocationDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new stockLocationDTO, or with status 400 (Bad Request) if the
	 *         stockLocation has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/stock-locations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<StockLocationDTO> createStockLocation(@Valid @RequestBody StockLocationDTO stockLocationDTO)
			throws URISyntaxException {
		log.debug("Web request to save StockLocation : {}", stockLocationDTO);
		if (stockLocationDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("stockLocation", "idexists",
					"A new stockLocation cannot already have an ID")).body(null);
		}
		if (stockLocationService.findByName(stockLocationDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("stockLocation", "nameexists", "StockLocation already in use"))
					.body(null);
		}
		stockLocationDTO.setActivated(true);
		StockLocationDTO result = stockLocationService.save(stockLocationDTO);
		return ResponseEntity.created(new URI("/web/stock-locations/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("stockLocation", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /stock-locations : Updates an existing stockLocation.
	 *
	 * @param stockLocationDTO
	 *            the stockLocationDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         stockLocationDTO, or with status 400 (Bad Request) if the
	 *         stockLocationDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the stockLocationDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/stock-locations", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StockLocationDTO> updateStockLocation(@Valid @RequestBody StockLocationDTO stockLocationDTO)
			throws URISyntaxException {
		log.debug("REST request to update StockLocation : {}", stockLocationDTO);
		if (stockLocationDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("stockLocation", "idNotexists", "StockLocation must have an ID"))
					.body(null);
		}
		Optional<StockLocationDTO> existingStockLocation = stockLocationService.findByName(stockLocationDTO.getName());
		if (existingStockLocation.isPresent()
				&& (!existingStockLocation.get().getPid().equals(stockLocationDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("stockLocation", "nameexists", "StockLocation already in use"))
					.body(null);
		}
		StockLocationDTO result = stockLocationService.update(stockLocationDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("stockLocation", "idNotexists", "Invalid StockLocation ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("stockLocation", stockLocationDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /stock-locations : get all the stockLocations.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         stockLocations in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/stock-locations", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllStockLocations(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of StockLocations");
		
		List<StockLocationDTO> deactivatedStockLocations = stockLocationService
				.findAllByCompanyAndDeactivatedStockLocation(false);
		model.addAttribute("stockLocations", stockLocationService.findAllByCompanyAndDeactivatedStockLocation(true));
		model.addAttribute("deactivatedStockLocations", deactivatedStockLocations);
		return "company/stockLocations";
	}

	/**
	 * GET /stock-locations/:pid : get the "pid" stockLocation.
	 *
	 * @param pid
	 *            the pid of the stockLocationDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         stockLocationDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/stock-locations/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StockLocationDTO> getStockLocation(@PathVariable String pid) {
		log.debug("Web request to get StockLocation by pid : {}", pid);
		return stockLocationService.findOneByPid(pid)
				.map(stockLocationDTO -> new ResponseEntity<>(stockLocationDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /stock-locations/:id : delete the "id" stockLocation.
	 *
	 * @param id
	 *            the id of the stockLocationDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/stock-locations/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteStockLocation(@PathVariable String pid) {
		log.debug("REST request to delete StockLocation : {}", pid);
		stockLocationService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("stockLocation", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /stock-locations/changeStatus:stockLocationDTO:
	 *        update status of stockLocation.
	 * 
	 * @param stockLocationDTO
	 *            the stockLocationDTO to update
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/stock-locations/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StockLocationDTO> updateDesignationStatus(
			@Valid @RequestBody StockLocationDTO stockLocationDTO) {
		log.debug("Web request to update status stockLocation : {}", stockLocationDTO);
		StockLocationDTO res = stockLocationService.updateStockLocationStatus(stockLocationDTO.getPid(),
				stockLocationDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /stock-locations/activateStockLocation : activate
	 *        status of stockLocation.
	 * 
	 * @param stocklocations
	 *            the stockLocations to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/stock-locations/activateStockLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StockLocationDTO> activateStockLocation(@Valid @RequestParam String stocklocations) {
		log.debug("Web request to activate stockLocation : {}");
		String[] stocklocation = stocklocations.split(",");
		for (String stockpid : stocklocation) {
			stockLocationService.updateStockLocationStatus(stockpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
