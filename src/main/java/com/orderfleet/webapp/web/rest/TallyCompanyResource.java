package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.TallyCompany;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.TallyCompanyService;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Resource for TallyCompany.
 *
 * @author Sarath
 * @since Feb 12, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class TallyCompanyResource {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private TallyCompanyService tallyCompanyService;

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@RequestMapping(value = "/tallyCompany", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({AuthoritiesConstants.SITE_ADMIN,AuthoritiesConstants.PARTNER})
	public String getAllTallyCompanys(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of tallyCompany");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("tallyCompanies", tallyCompanyService.findAll());
		return "site_admin/tallyCompany";
	}

	@RequestMapping(value = "/tallyCompany", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public TallyCompany createTallyCompany(@Valid @RequestParam(value = "companyPid") String companyPid,
			@Valid @RequestParam(value = "tallyCompanyName") String tallyCompanyName,
			@Valid @RequestParam(value = "description") String description) throws URISyntaxException {
		log.debug("Web request to save tally company : {}", companyPid);
		TallyCompany tallyCompany = new TallyCompany();
		Optional<Company> opCompany = companyRepository.findOneByPid(companyPid);
		if (opCompany.isPresent()) {
			Optional<TallyCompany> opTallyCompany = tallyCompanyService.findByCompanyId(opCompany.get().getId());
			if (opTallyCompany.isPresent()) {
				tallyCompany = opTallyCompany.get();
				tallyCompany.setDescription(description);
				tallyCompany.setTallyCompanyName(tallyCompanyName);
				tallyCompany = tallyCompanyService.save(tallyCompany);
			} else {
				tallyCompany = new TallyCompany();
				tallyCompany.setDescription(description);
				tallyCompany.setTallyCompanyName(tallyCompanyName);
				tallyCompany.setCompany(opCompany.get());
				tallyCompany = tallyCompanyService.save(tallyCompany);
			}
		}
		return tallyCompany;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/tallyCompany/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody TallyCompany getTallyCompany(@PathVariable String companyPid) throws URISyntaxException {
		log.debug("Web request to get tally Company ");
		Optional<TallyCompany> opTallyCompany = tallyCompanyService.findByCompanyPid(companyPid);
		if (opTallyCompany.isPresent()) {
			return opTallyCompany.get();
		}
		return null;
	}

	@Timed
	@RequestMapping(value = "/tallyCompany/{companyPid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteTallyCompany(@PathVariable String companyPid) throws URISyntaxException {
		log.debug("Web request to delete tally company by compantPid : {}", companyPid);
		Optional<TallyCompany> opTallyCompany = tallyCompanyService.findByCompanyPid(companyPid);
		if (opTallyCompany.isPresent()) {
			tallyCompanyService.deleteTallyCompany(opTallyCompany.get());
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tallyCompany", companyPid.toString()))
				.build();
	}
}
