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
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.UserPriceLevelService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;

/**
 * Web controller for managing UserPriceLevel.
 * 
 * @author Muhammed Riyas T
 * @since August 29, 2016
 */
@Controller
@RequestMapping("/web")
public class UserPriceLevelResource {

	private final Logger log = LoggerFactory.getLogger(UserPriceLevelResource.class);

	@Inject
	private UserPriceLevelService userPriceLevelService;

	@Inject
	private PriceLevelService priceLevelService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-price-levels : get all the user priceLevels.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         priceLevels in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-price-levels", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserPriceLevels(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User PriceLevels");

		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		model.addAttribute("priceLevels", priceLevelService.findAllByCompany());

		return "company/userPriceLevels";
	}

	/**
	 * * GET /user-price-levels/save : save user priceLevels.
	 * 
	 * @param pid
	 * @param assignedPriceLevels
	 * @return
	 */
	@RequestMapping(value = "/user-price-levels/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String assignedPriceLevels) {
		log.debug("REST request to save assigned assigned priceLevels", userPid);
		userPriceLevelService.save(userPid, assignedPriceLevels);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-price-levels/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceLevelDTO>> getUserPriceLevels(@PathVariable String userPid) {
		log.debug("Web request to get get PriceLevels by user pid : {}", userPid);
		return new ResponseEntity<>(userPriceLevelService.findPriceLevelsByUserPid(userPid), HttpStatus.OK);
	}

}
