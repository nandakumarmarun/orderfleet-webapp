package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.StaticFormJSCodeRepository;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.StaticFormJSCodeService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * CompanyViewDTO Web controller for managing Company.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
@Controller
@RequestMapping("/web")
public class StaticJsCodeResource {

	private final Logger log = LoggerFactory.getLogger(StaticJsCodeResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private StaticFormJSCodeRepository staticFormJSCodeRepository;

	@Inject
	private StaticFormJSCodeService staticFormJSCodeService;

	/**
	 * GET /static-js-code
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of companies
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/static-js-code", method = RequestMethod.GET)
	public String getAllCompanies(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Companies");
		model.addAttribute("companies", companyService.findAll());
		return "site_admin/staticJsCode";
	}

	/**
	 * POST /companies : Create a new company.
	 *
	 * @param companyDTO
	 *            the companyDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new companyDTO, or with status 400 (Bad Request) if the company
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@Timed
	@RequestMapping(value = "/static-js-code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createCompany(@RequestParam String companyPid, @RequestParam String documentPid,
			@RequestParam String jsCode) throws URISyntaxException {
		log.debug("Web request to save jsCode : {}", documentPid);
		staticFormJSCodeService.save(companyPid, documentPid, jsCode);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/static-js-code/change-company/{companyPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DocumentDTO>> changeCompany(@PathVariable String companyPid) {
		log.debug("Web request to get Company Documents : {}", companyPid);
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(companyPid);
		return new ResponseEntity<>(documents.stream().map(DocumentDTO::new).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/static-js-code/change-document/{companyPid}/{documentPid}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> changeDocument(@PathVariable String companyPid, @PathVariable String documentPid) {
		log.debug("Web request to get Document js code : {}", documentPid);
		String jSCode = "";
		StaticFormJSCode staticFormJSCode = staticFormJSCodeRepository.findByCompanyPidAndDocumentPid(companyPid,
				documentPid);
		if (staticFormJSCode != null) {
			jSCode = staticFormJSCode.getJsCode();
		}
		return new ResponseEntity<>(jSCode, HttpStatus.OK);
	}

}
