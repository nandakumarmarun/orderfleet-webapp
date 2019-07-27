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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Web controller for managing SalesTargetGroup.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Controller
@RequestMapping("/web")
public class ActivatedLocationResource {

	private final Logger log = LoggerFactory.getLogger(ActivatedLocationResource.class);

	@Inject
	private LocationService locationService;

	@Inject
	private LocationRepository locationRepository;

	@RequestMapping(value = "/activatedLocations", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesTargetGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Activated Locations");
		model.addAttribute("locations", locationRepository.findAllByCompanyIdAndLocationActivatedOrDeactivated(true));
		return "company/activatedLocations";
	}

	@RequestMapping(value = "/activatedLocations/saveActivatedLocations", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveLocations(@RequestParam String assignedLocations) {
		log.debug("REST request to save assigned locations", assignedLocations);
		locationService.saveActivatedLocations(assignedLocations);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/activatedLocations/getLocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationDTO>> getLocations() {
		log.debug("Web request to get all activated Locations");
		return new ResponseEntity<>(locationService.findAllLocationsByCompanyAndActivatedLocations(), HttpStatus.OK);
	}

}