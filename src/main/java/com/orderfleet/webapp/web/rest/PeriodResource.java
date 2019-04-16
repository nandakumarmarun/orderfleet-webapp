package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;

/**
 * Web controller for managing Bank.
 * 
 * @author Muhammed Riyas T
 * @since Feb 03, 2017
 */
@Controller
@RequestMapping("/web")
public class PeriodResource {

	private final Logger log = LoggerFactory.getLogger(PeriodResource.class);

	@Inject
	private CompanyService companyService;

	/**
	 * GET /period : get all the company period.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the object of company
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/period", method = RequestMethod.GET)
	public String getPriod(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of period");
		model.addAttribute("company", companyService.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		return "company/period";
	}

	/**
	 * POST /period : save period
	 *
	 * @param fromDate
	 * @param toDate
	 * @return the ResponseEntity with status 200(Ok)
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/period", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> savePriod(@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate)
			throws URISyntaxException {
		log.debug("Web request to save  period");
		companyService.saveCompanyPeriod(fromDate, toDate);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
