package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;

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
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.TallyConfigurationService;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;

/**
 * Web controller for managing TallyConfiguration.
 *
 * @author Sarath
 * @since Jul 28, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class TallyConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(CompanyPerformanceConfigResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TallyConfigurationService tallyConfigurationService;

	@RequestMapping(value = "/tally-configuration", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllTallyConfiguration(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Tally Configuration");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("tallyConfigurations", tallyConfigurationService.findAll());
		return "site_admin/tallyConfiguration";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/tally-configuration/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody TallyConfigurationDTO getTallyConfiguration(@PathVariable String companyPid)
			throws URISyntaxException {
		log.debug("Web request to get Tally Configuration");
		Optional<Company> optionalCompany = companyRepository.findOneByPid(companyPid);
		if (optionalCompany.isPresent()) {
			return tallyConfigurationService.findByCompanyId(optionalCompany.get().getId());
		}
		return null;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/tally-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveTallyConfiguration(@RequestBody TallyConfigurationDTO tallyConfigurationDTO)
			throws URISyntaxException {
		log.debug("Web request to save Tally Configuration ");

		Optional<Company> optionalCompany = companyRepository.findOneByPid(tallyConfigurationDTO.getCompanyPid());
		if (!optionalCompany.isPresent()) {
			return new ResponseEntity<>("Invalid Company", HttpStatus.BAD_REQUEST);
		}
		Optional<TallyConfigurationDTO> opConfigurationDTO = tallyConfigurationService
				.findOneByCompanyId(optionalCompany.get().getId());

		if (opConfigurationDTO.isPresent()) {
			tallyConfigurationDTO.setPid(opConfigurationDTO.get().getPid());
			tallyConfigurationService.updateTallyConfiguration(tallyConfigurationDTO);
		} else {
			tallyConfigurationService.saveTallyConfiguration(tallyConfigurationDTO);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/tally-configuration/delete/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteTallyConfiguration(@PathVariable String pid) throws URISyntaxException {
		log.debug("Web request to delete Tally Configuration compantPid : {}", pid);
		tallyConfigurationService.deleteByPid(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tallyConfiguration", pid.toString()))
				.build();
	}
}
