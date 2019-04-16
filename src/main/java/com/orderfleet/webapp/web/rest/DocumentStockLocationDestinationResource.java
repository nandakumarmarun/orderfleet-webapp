package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DocumentStockLocationDestinationService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Web controller for managing DocumentStockLocationDestination.
 * 
 * @author Sarath
 * @since July 19 2016
 *
 */
@Controller
@RequestMapping("/web")
public class DocumentStockLocationDestinationResource {

	private final Logger log = LoggerFactory.getLogger(DocumentStockLocationDestinationResource.class);

	@Inject
	private DocumentStockLocationDestinationService documentStockLocationDestinationService;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private DocumentService documentService;

	@RequestMapping(value = "/document-stock-location-destination", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDocumentStockLocationDestinationAssignment(Model model)
			throws URISyntaxException {
		log.debug("Web request to get a page of Document Stock Location Destination Assignment");

		List<DocumentDTO> pageUser = documentService.findAllByCompany();
		model.addAttribute("documents", pageUser);
		List<StockLocationDTO> stockLocations = stockLocationService.findAllByCompany();
		model.addAttribute("allStockLocations", stockLocations);
		return "company/documentStockLocationDestination";
	}

	@RequestMapping(value = "/document-stock-location-destination/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String documentPid,
			@RequestParam String assignedStockLocationDestinations, @RequestParam String defaultStockLocationPid) {
		log.debug("REST request to save  assigned Stock Location Destinations", documentPid);
		documentStockLocationDestinationService.save(documentPid, assignedStockLocationDestinations,
				defaultStockLocationPid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/document-stock-location-destination/{documentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentStockLocationDTO>> getDocumentStockLocations(@PathVariable String documentPid) {
		log.debug("Web request to get get Stock Location Destinations by Document pid : {}", documentPid);
		return new ResponseEntity<>(documentStockLocationDestinationService.findByDocumentPid(documentPid),
				HttpStatus.OK);
	}

}
