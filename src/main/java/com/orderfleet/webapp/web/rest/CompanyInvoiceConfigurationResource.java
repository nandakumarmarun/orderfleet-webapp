package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyInvoiceConfigurationService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.web.rest.dto.CompanyInvoiceConfigurationDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Service Implementation for managing CompanyInvoiceConfiguration.
 *
 * @author Athul
 * @since Apr 14, 2018
 *
 */

@Controller
@RequestMapping("/web")
public class CompanyInvoiceConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(CompanyInvoiceConfigurationResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyInvoiceConfigurationService companyInvoiceConfigurationService;

	@RequestMapping(value = "/company-invoice-configuration", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getCompanies(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Companies");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("companyInvoiceConfigurations", companyInvoiceConfigurationService.findAll());
		return "site_admin/company-invoice-configuration";
	}

	@RequestMapping(value = "/company-invoice-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<CompanyInvoiceConfigurationDTO> createCompanyInvoiceConfiguration(
			@Valid @RequestBody CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO)
			throws URISyntaxException {
		log.debug("Web request to save Company Invoice Configuration : {}", companyInvoiceConfigurationDTO);
		if (companyInvoiceConfigurationDTO.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("companyInvoiceConfigurationDTO",
					"idexists", "A new companyInvoiceConfigurationDTO cannot already have an ID")).body(null);
		}
		if (companyInvoiceConfigurationService.findOneByCompanyPid(companyInvoiceConfigurationDTO.getCompanyPid())
				.isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("companyInvoiceConfigurationDTO",
					"nameexists", "CompanyInvoiceConfigurationDTO already in use")).body(null);
		}
		CompanyInvoiceConfigurationDTO result = companyInvoiceConfigurationService.save(companyInvoiceConfigurationDTO);
		return ResponseEntity.created(new URI("/web/company-invoice-configuration/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("companyInvoiceConfiguration", result.getId().toString()))
				.body(result);
	}

	@RequestMapping(value = "/company-invoice-configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyInvoiceConfigurationDTO> updateCompanyInvoiceConfiguration(
			@Valid @RequestBody CompanyInvoiceConfigurationDTO companyInvoiceConfigurationDTO)
			throws URISyntaxException {
		log.debug("REST request to update Company Invoice Configuration : {}", companyInvoiceConfigurationDTO);
		if (companyInvoiceConfigurationDTO.getId() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("companyInvoiceConfiguration",
					"idNotexists", "CompanyInvoiceConfiguration must have an ID")).body(null);
		}
		Optional<CompanyInvoiceConfigurationDTO> existingCompanyInvoiceConfigurationDTO = companyInvoiceConfigurationService
				.findOneByCompanyPid(companyInvoiceConfigurationDTO.getCompanyPid());
		if (existingCompanyInvoiceConfigurationDTO.isPresent() && (!existingCompanyInvoiceConfigurationDTO.get().getId()
				.equals(companyInvoiceConfigurationDTO.getId()))) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("companyInvoiceConfiguration",
					"nameexists", "CompanyInvoiceConfigurationDTO already in use")).body(null);
		}
		CompanyInvoiceConfigurationDTO result = companyInvoiceConfigurationService
				.update(companyInvoiceConfigurationDTO);
		if (result == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("companyInvoiceConfiguration",
					"idNotexists", "Invalid CompanyInvoiceConfiguration ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("companyInvoiceConfiguration",
				companyInvoiceConfigurationDTO.getId().toString())).body(result);
	}

	@RequestMapping(value = "/company-invoice-configuration/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyInvoiceConfigurationDTO> getCompanyInvoiceConfigurationDTO(@PathVariable Long id) {
		log.debug("Web request to get Company Invoice Configuration by id : {}", id);
		return companyInvoiceConfigurationService.findOneById(id).map(
				companyInvoiceConfigurationDTO -> new ResponseEntity<>(companyInvoiceConfigurationDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/company-invoice-configuration/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteCompanyInvoiceConfiguration(@PathVariable Long id) {
		log.debug("REST request to delete Company Invoice Configuration : {}", id);
		companyInvoiceConfigurationService.delete(id);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityDeletionAlert("companyInvoiceConfiguration", id.toString())).build();
	}

}
