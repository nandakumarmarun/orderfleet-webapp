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
import com.orderfleet.webapp.service.MobileConfigurationService;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing MobileConfiguration.
 *
 * @author Sarath
 * @since Jul 28, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class MobileConfigurationResource {

	private final Logger log = LoggerFactory.getLogger(CompanyPerformanceConfigResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private MobileConfigurationService mobileConfigurationService;

	@RequestMapping(value = "/mobile-configuration", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllMobileConfiguration(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Mobile Configuration");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("mobileConfigurations", mobileConfigurationService.findAll());
		return "site_admin/mobileConfiguration";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/mobile-configuration/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody MobileConfigurationDTO getMobileConfiguration(@PathVariable String companyPid)
			throws URISyntaxException {
		log.debug("Web request to get Mobile Configuration");
		Optional<Company> optionalCompany = companyRepository.findOneByPid(companyPid);
		if (optionalCompany.isPresent()) {
			return mobileConfigurationService.findByCompanyId(optionalCompany.get().getId());
		} 
		return null;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/mobile-configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveMobileConfiguration(@RequestBody MobileConfigurationDTO mobileConfigurationDTO)
			throws URISyntaxException {
		log.debug("Web request to save Mobile Configuration ");

		Optional<Company> optionalCompany = companyRepository.findOneByPid(mobileConfigurationDTO.getCompanyPid());
		if (!optionalCompany.isPresent()) {
			return new ResponseEntity<>("Invalid Company", HttpStatus.BAD_REQUEST);
		}
		Optional<MobileConfigurationDTO> opConfigurationDTO = mobileConfigurationService
				.findOneByCompanyId(optionalCompany.get().getId());
		
		if (opConfigurationDTO.isPresent()) {
			mobileConfigurationDTO.setPid(opConfigurationDTO.get().getPid());
			mobileConfigurationService.updateMobileConfiguration(mobileConfigurationDTO);
		} else {
			mobileConfigurationService.saveMobileConfiguration(mobileConfigurationDTO);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/mobile-configuration/delete/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteMobileConfiguration(@PathVariable String pid) throws URISyntaxException {
		log.debug("Web request to delete Mobile Configuration By compantPid : {}", pid);
		mobileConfigurationService.deleteByPid(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("mobileConfiguration", pid.toString()))
				.build();
	}
}
