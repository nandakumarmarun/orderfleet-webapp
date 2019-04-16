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
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.UserStockLocationService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;

/**
 * Web controller for managing UserStockLocation.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
@Controller
@RequestMapping("/web")
public class UserStockLocationResource {

	private final Logger log = LoggerFactory.getLogger(UserStockLocationResource.class);

	@Inject
	private UserStockLocationService userStockLocationService;

	@Inject
	private StockLocationService stockLocationService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-stock-locations : get all the user stock locations.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         stock locations in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-stock-locations", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserStockLocationAssignment(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Stock Location Group Assignment");

		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		model.addAttribute("stockLocations", stockLocationService.findAllByCompany());
		return "company/userStockLocations";
	}

	/**
	 * * GET /user-stock-locations/save : save user stock locations.
	 * 
	 * @param pid
	 * @param assignedStockLocations
	 * @return
	 */
	@RequestMapping(value = "/user-stock-locations/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String assignedStockLocations) {
		log.debug("REST request to save assigned assigned stock locations", userPid);
		userStockLocationService.save(userPid, assignedStockLocations);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-stock-locations/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StockLocationDTO>> getUserStockLocations(@PathVariable String userPid) {
		log.debug("Web request to get get Stock Locations by user pid : {}", userPid);
		return new ResponseEntity<>(userStockLocationService.findStockLocationsByUserPid(userPid), HttpStatus.OK);
	}

}
