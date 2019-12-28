package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ProductNameTextSettingsService;
import com.orderfleet.webapp.web.rest.dto.ProductNameTextSettingsDTO;

/**
 * Web controller for managing ProductNameTextSettings.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductNameTextSettingsResource {

	private final Logger log = LoggerFactory.getLogger(ProductNameTextSettingsResource.class);

	@Inject
	private ProductNameTextSettingsService productNameTextSettingsService;

	/**
	 * GET /productNameTextSettings : get all the productNameTextSettingss.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productNameTextSettingss in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/productNameTextSettings", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllProductNameTextSettings(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of ProductNameTextSettingss");
		model.addAttribute("productNameTextSettings", productNameTextSettingsService.findAllByCompany());
		return "company/productNameTextSettings";
	}

	/**
	 * POST /productNameTextSettings : Create a new productNameTextSettings.
	 *
	 * @param productNameTextSettingsDTO the productNameTextSettingsDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         productNameTextSettingsDTO, or with status 400 (Bad Request) if the
	 *         productNameTextSettings has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/productNameTextSettings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createProductNameTextSettings(
			@Valid @RequestBody List<ProductNameTextSettingsDTO> productNameTextSettingsDTOs)
			throws URISyntaxException {
		log.debug("Web request to save ProductNameTextSettings ");
		productNameTextSettingsService.save(productNameTextSettingsDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@PostMapping(value = "/productNameTextSettings/defValues", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> getAllProductNameTextSettings() throws URISyntaxException {
		log.debug("web request to save default productnametextsettings ");
		List<String> defTexts = Arrays.asList("STOCK", "DESCRIPTION", "SELLING RATE", "MRP", "PRODUCT DESCRIPTION",
				"BARCODE", "REMARKS");
		List<ProductNameTextSettingsDTO> textSettingsDTOs = new ArrayList<>();
		for (String text : defTexts) {
			textSettingsDTOs.add(new ProductNameTextSettingsDTO(text, false));
		}
		productNameTextSettingsService.saveDefault(textSettingsDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
