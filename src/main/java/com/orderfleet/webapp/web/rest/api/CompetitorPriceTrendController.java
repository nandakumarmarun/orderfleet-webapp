package com.orderfleet.webapp.web.rest.api;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.CompetitorPriceTrendService;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendListDTO;

/**
 * REST controller for managing CompetitorPriceTrend.
 * 
 * @author Muhammed Riyas T
 * @since September 05, 2016
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompetitorPriceTrendController {

	private final Logger log = LoggerFactory.getLogger(CompetitorPriceTrendController.class);

	@Inject
	private CompetitorPriceTrendService competitorPriceTrendService;
	
	/**
	 * POST /executive-day-plan : Create a new Competitor PriceTrend.
	 * 
	 * @param competitorPriceTrendDTO
	 *            the competitorPriceTrendDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new competitorPriceTrendDTO
	 */
	@RequestMapping(value = "/competitor-price-trend", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createCompetitorPriceTrend(
			@RequestBody CompetitorPriceTrendListDTO competitorPriceTrendListDTO) {
		log.debug("Rest request to save CompetitorPriceTrend : {}", competitorPriceTrendListDTO);
		competitorPriceTrendService.save(competitorPriceTrendListDTO.getCompetitorPriceTrends());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
