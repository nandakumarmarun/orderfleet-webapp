package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;

/**
 * Web controller for managing Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Controller
@RequestMapping("/web")
public class PriceTrendConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(PriceTrendConfigurationResource.class);

	@Inject
	private PriceTrendConfigurationService priceTrendConfigurationService;

	/**
	 * POST /price-trend-configuration : Create a new priceTrendConfiguration.
	 *
	 * @param priceTrendConfigurationDTOs
	 *            the priceTrendConfigurationDTOs to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/price-trend-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<?> priceTrendConfiguration(
			@Valid @RequestBody List<PriceTrendConfigurationDTO> priceTrendConfigurationDTOs)
			throws URISyntaxException {
		log.debug("Web request to save priceTrendConfigurationDTOs");
		priceTrendConfigurationService.save(priceTrendConfigurationDTOs);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * GET /price-trend-configuration : get Price Trend Configuration.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activities in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/price-trend-configuration", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getPriceTrendConfiguration(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Price Trend Configuration");
		return "company/priceTrendConfigurations";
	}

	@Timed
	@RequestMapping(value = "/price-trend-configuration/edit", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<PriceTrendConfigurationDTO>> getPriceTrendConfiguration() {
		priceTrendConfigurationService.findAllByCompany();
		return new ResponseEntity<>(priceTrendConfigurationService.findAllByCompany(), HttpStatus.OK);
	}

}
