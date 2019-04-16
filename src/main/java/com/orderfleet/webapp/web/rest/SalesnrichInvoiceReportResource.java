package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.SalesnrichInvoiceHeaderService;
import com.orderfleet.webapp.web.rest.dto.SalesnrichInvoiceHeaderDTO;

/**
 * Web controller for managing Salesnrich Invoice Report.
 * 
 * @author Athul
 * @since April 6, 2018
 */

@Controller
@RequestMapping("/web")
public class SalesnrichInvoiceReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesnrichInvoiceReportResource.class);

	@Inject
	private SalesnrichInvoiceHeaderService salesnrichInvoiceHeaderService;

	@Inject
	private CompanyService companyService;

	/**
	 * GET /salesnrich-invoice-report : get all the invoice report.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of accounting
	 *         vouchers in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/salesnrich-invoice-report", method = RequestMethod.GET)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllSalesnrichInvoiceReport(Model model) {
		model.addAttribute("companies", companyService.findAllCompanySortedByName());
		return "site_admin/salesnrich-invoice-report";
	}

	@Timed
	@RequestMapping(value = "/salesnrich-invoice-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SalesnrichInvoiceHeaderDTO>> filterSalesnrichInvoiceReport(
			@RequestParam String companyPid) {
		log.debug("Web request to filter Salesnrich Invoice Report");
		List<SalesnrichInvoiceHeaderDTO> headerDTOs = new ArrayList<>();
		if (companyPid.equalsIgnoreCase("no")) {
			headerDTOs = salesnrichInvoiceHeaderService.findAll();
		} else if (!companyPid.equalsIgnoreCase("no")) {
			headerDTOs = salesnrichInvoiceHeaderService.findAllByCompanyPid(companyPid);
		}
		return new ResponseEntity<>(headerDTOs, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/salesnrich-invoice-report/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SalesnrichInvoiceHeaderDTO> getAccountingVoucher(@PathVariable Long pid) {
		log.debug("Web request to get SalesnrichInvoiceHeaderDTO by pid : {}", pid);
		Optional<SalesnrichInvoiceHeaderDTO> salesnrichInvoiceHeaderDTO = salesnrichInvoiceHeaderService
				.findOneById(pid);
		if (salesnrichInvoiceHeaderDTO.isPresent()) {
			return new ResponseEntity<>(salesnrichInvoiceHeaderDTO.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
