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
import com.orderfleet.webapp.service.EcomProductGroupService;
import com.orderfleet.webapp.service.UserEcomProductGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;

@Controller
@RequestMapping("/web")
public class UserEcomProductGroupResource {
	private final Logger log = LoggerFactory.getLogger(UserEcomProductGroupResource.class);

	@Inject
	private UserEcomProductGroupService userProductGroupService;

	@Inject
	private EcomProductGroupService productGroupService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-product-groups : get all the user productGroups.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         productGroups in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-ecom-product-groups", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserProductGroups(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User ProductGroups");

		List<UserDTO> users = userService.findAllEcomUsersByCompany();
		model.addAttribute("users", users);
		model.addAttribute("productGroups", productGroupService.findAllByCompany());

		return "company/userEcomProductGroups";
	}

	/**
	 * * GET /user-product-groups/save : save user productGroups.
	 * 
	 * @param pid
	 * @param assignedProductGroups
	 * @return
	 */
	@RequestMapping(value = "/user-ecom-product-groups/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String assignedProductGroups) {
		log.debug("REST request to save assigned assigned productGroups", userPid);
		userProductGroupService.save(userPid, assignedProductGroups);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-ecom-product-groups/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<EcomProductGroupDTO>> getUserProductGroups(@PathVariable String userPid) {
		log.debug("Web request to get get ProductGroups by user pid : {}", userPid);
		return new ResponseEntity<>(userProductGroupService.findProductGroupsByUserPid(userPid), HttpStatus.OK);
	}
}
