package com.orderfleet.webapp.web.rest.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserProductCategoryRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * REST controller for getting product and productGroup and productCategory
 * assigned to a User.
 * 
 * @author sarath
 * @since July 12 2016
 */
@RestController
@RequestMapping(value = "/api")
@Transactional(readOnly = true)
public class UserProductController {

	private final Logger log = LoggerFactory.getLogger(UserProductController.class);

	@Inject
	private UserProductCategoryRepository userProductCategoryRepository;

	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	/**
	 * GET user-products : get all the getAllUserProducts.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of Product
	 *         in body
	 */
	@RequestMapping(value = "/user-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getAllUserProducts() {
		log.debug("REST request to get all products");
		List<ProductProfile> products = productProfileRepository.findAll();
		List<ProductProfileDTO> result = products.stream().map(ProductProfileDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET user-products-category's : get all the userProductCategorys.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         userProductCategorys in body
	 */
	@RequestMapping(value = "/user-productCategorys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductCategoryDTO>> getAllUserProductCategorys() {
		log.debug("REST request to get all product categorys");
		List<ProductCategory> productCategories = userProductCategoryRepository
				.findProductCategorysByUserIsCurrentUser();
		List<ProductCategoryDTO> result = productCategories.stream().map(ProductCategoryDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * GET user-product-groups : get all the getAllUserProductGroups.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of Product
	 *         Group in body
	 */
	@RequestMapping(value = "/user-productGroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductGroupDTO>> getAllUserProductGroups() {
		log.debug("REST request to get all product groups");
		List<ProductGroup> productGroups = userProductGroupRepository.findProductGroupsByUserIsCurrentUser();
		List<ProductGroupDTO> result = productGroups.stream().map(ProductGroupDTO::new).collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
