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
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.UserProductCategoryService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;

/**
 * Web controller for managing UserProductCategory.
 * 
 * @author Sarath
 * @since July 8 2016
 */
@Controller
@RequestMapping("/web")
public class UserProductCategoryResource {

	private final Logger log = LoggerFactory.getLogger(UserProductCategoryResource.class);

	@Inject
	private UserProductCategoryService userProductCategoryService;

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
	private UserService userService;

	/**
	 * GET /user-productCategories : get all the user productCategories.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users and
	 *         productCategories in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/user-product-categories", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserProductCategories(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User ProductCategories");
		List<UserDTO> users = userService.findAllByCompany();
		model.addAttribute("users", users);
		model.addAttribute("productCategories", productCategoryService.findAllByCompany());

		return "company/userProductCategories";
	}

	/**
	 * * GET /user-product-categories/save : save user productCategories.
	 * 
	 * @param pid
	 * @param assignedProductCategories
	 * @return
	 */
	@RequestMapping(value = "/user-product-categories/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String userPid, @RequestParam String assignedProductCategories) {
		log.debug("REST request to save assigned assigned productCategories", userPid);
		userProductCategoryService.save(userPid, assignedProductCategories);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user-product-categories/{userPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductCategoryDTO>> getUserProductCategories(@PathVariable String userPid) {
		log.debug("Web request to get get ProductCategories by user pid : {}", userPid);
		return new ResponseEntity<>(userProductCategoryService.findProductCategoriesByUserPid(userPid), HttpStatus.OK);
	}
}
