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
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing ProductCategory.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductCategoryResource {

	private final Logger log = LoggerFactory.getLogger(ProductCategoryResource.class);

	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * POST /productCategories : Create a new productCategory.
	 *
	 * @param productCategoryDTO
	 *            the productCategoryDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new productCategoryDTO, or with status 400 (Bad Request) if the
	 *         productCategory has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/productCategories", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<ProductCategoryDTO> createProductCategory(
			@Valid @RequestBody ProductCategoryDTO productCategoryDTO) throws URISyntaxException {
		log.debug("Web request to save ProductCategory : {}", productCategoryDTO);
		if (productCategoryDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("productCategory", "idexists",
					"A new productCategory cannot already have an ID")).body(null);
		}
		if (productCategoryService.findByName(productCategoryDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productCategory", "nameexists", "Product Category already in use"))
					.body(null);
		}
		productCategoryDTO.setActivated(true);
		ProductCategoryDTO result = productCategoryService.save(productCategoryDTO);
		return ResponseEntity.created(new URI("/web/productCategories/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("productCategory", result.getPid().toString()))
				.body(result);
	}

	/**
	 * PUT /productCategories : Updates an existing productCategory.
	 *
	 * @param productCategoryDTO
	 *            the productCategoryDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         productCategoryDTO, or with status 400 (Bad Request) if the
	 *         productCategoryDTO is not valid, or with status 500 (Internal
	 *         Server Error) if the productCategoryDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/productCategories", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductCategoryDTO> updateProductCategory(
			@Valid @RequestBody ProductCategoryDTO productCategoryDTO) throws URISyntaxException {
		log.debug("Web request to update ProductCategory : {}", productCategoryDTO);
		if (productCategoryDTO.getPid() == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productCategory", "idNotexists", "Product Category must have an ID"))
					.body(null);
		}
		Optional<ProductCategoryDTO> existingProductCategory = productCategoryService
				.findByName(productCategoryDTO.getName());
		if (existingProductCategory.isPresent()
				&& (!existingProductCategory.get().getPid().equals(productCategoryDTO.getPid()))) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productCategory", "nameexists", "Product Category already in use"))
					.body(null);
		}
		ProductCategoryDTO result = productCategoryService.update(productCategoryDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("productCategory", "idNotexists", "Invalid Product Category ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("productCategory", productCategoryDTO.getPid().toString()))
				.body(result);
	}

	/**
	 * GET /productCategories : get all the productCategories.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productCategories in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/productCategories", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductCategorys(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Product Categories");
		List<ProductCategoryDTO> productCatagoryDeactive = productCategoryService.findAllByCompanyAndDeactivated(false);
		model.addAttribute("productCategorys", productCategoryService.findAllByCompanyAndDeactivated(true));
		model.addAttribute("productCatagoryDeactive", productCatagoryDeactive);
		return "company/productCategories";
	}

	/**
	 * GET /productCategories/:id : get the "id" productCategory.
	 *
	 * @param id
	 *            the id of the productCategoryDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         productCategoryDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/productCategories/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ProductCategoryDTO> getProductCategory(@PathVariable String pid) {
		log.debug("Web request to get ProductCategory by pid : {}", pid);
		return productCategoryService.findOneByPid(pid)
				.map(productCategoryDTO -> new ResponseEntity<>(productCategoryDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /productCategories/:id : delete the "id" productCategory.
	 *
	 * @param id
	 *            the id of the productCategoryDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/productCategories/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteProductCategory(@PathVariable String pid) {
		log.debug("REST request to delete ProductCategory : {}", pid);
		productCategoryService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("productCategory", pid.toString()))
				.build();
	}

	/**
	 * @author Fahad
	 * @since feb 6, 2017
	 * 
	 *        UPDATE STATUS /activities/changeStatus : update status
	 *        (Activated/Deactivated) of productCategory.
	 * @param productCategoryDTO
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         ProductCategoryDTO.
	 */
	@Timed
	@RequestMapping(value = "/productCategories/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductCategoryDTO> updateProductCategoryStatus(
			@Valid @RequestBody ProductCategoryDTO productCategoryDTO) {
		log.debug("REST request to update status of ProductCategory : {}", productCategoryDTO);
		ProductCategoryDTO result = productCategoryService.updateProductCategoryStatus(productCategoryDTO.getPid(),
				productCategoryDTO.getActivated());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since feb 11, 2017
	 * 
	 *        updateActivateProductCategoryStatus
	 *        /productCategories/activateProductCategory:productcategory :
	 *        Activate Product Category.
	 * 
	 * @param productcategory
	 *            the productcategory to activate
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/productCategories/activateProductCategory", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductCategoryDTO> updateActivateProductCategoryStatus(
			@Valid @RequestParam String productcategory) {
		String[] productCategories = productcategory.split(",");
		for (String productCategoryPid : productCategories) {
			productCategoryService.updateProductCategoryStatus(productCategoryPid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	/**
	 * @author Sarath
	 * @since mar 20, 2017
	 * 
	 *        update ThirdpartyUpdate.
	 * @param productCategoryDTO
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         ProductCategoryDTO.
	 */
	@Timed
	@RequestMapping(value = "/productCategories/changeThirdpartyUpdateStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProductCategoryDTO> updateProductCategoryThirdpartyUpdate(
			@Valid @RequestBody ProductCategoryDTO productCategoryDTO) {
		log.debug("REST request to update status of ProductCategory : {}", productCategoryDTO);
		ProductCategoryDTO result = productCategoryService.updateProductCategoryThirdpartyUpdateStatus(
				productCategoryDTO.getPid(), productCategoryDTO.getThirdpartyUpdate());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
