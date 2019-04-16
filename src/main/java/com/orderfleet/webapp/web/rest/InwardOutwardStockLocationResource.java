package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.InwardOutwardStockLocationService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Web controller for managing InwardOutwardStockLocation.
 *
 * @author fahad
 * @since Feb 22, 2017
 */

@Controller
@RequestMapping("/web")
public class InwardOutwardStockLocationResource {

	private final Logger log = LoggerFactory.getLogger(InwardOutwardStockLocationResource.class);

	@Inject
	private StockLocationService stocklocationService;

	@Inject
	private InwardOutwardStockLocationService inwardOutwardStockLocationService;

	@RequestMapping(value = "/inward-outward-stock-location", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentFormAssignment(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of InwardOutwardStockLocation Forms Assignment");
		Page<StockLocationDTO> inwardOutwardStockLocations = inwardOutwardStockLocationService
				.findAllByCompany(pageable);
		List<StockLocationDTO> stockLocationDTOs = stocklocationService.findAllByCompany();
		model.addAttribute("stockLocations", stockLocationDTOs);
		model.addAttribute("inwardOutwardStockLocations", inwardOutwardStockLocations);
		return "company/inwardOutwardStockLocations";
	}

	@RequestMapping(value = "/inward-outward-stock-location/assignStockLocations", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccounts(@RequestParam String assignedStockLocations) {
		log.debug("REST request to save assigned stock location : {}");
		inwardOutwardStockLocationService.saveInwardOutwardStockLocation(assignedStockLocations);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/inward-outward-stock-location/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StockLocationDTO>> assignStocklocations() {
		log.debug("Web request to assigned status stockLocation : {}");
		List<StockLocationDTO> stockLocationDTOs = inwardOutwardStockLocationService.findAllByCompany();
		return new ResponseEntity<>(stockLocationDTOs, HttpStatus.OK);
	}
}
