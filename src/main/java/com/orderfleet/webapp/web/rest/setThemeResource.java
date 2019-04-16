package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.CompanyConfigurationDTO;

/**
 * Web controller for set company wise theme.
 *
 * @author Sarath
 * @since Jun 23, 2017
 *
 */

@Controller
@RequestMapping("/web")
public class setThemeResource {

	private final Logger log = LoggerFactory.getLogger(setThemeResource.class);

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ServletContext servletContext;
	
	@RequestMapping(value = "/set-theme", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getCompanyThemes(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of company theme");
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<CompanyConfiguration> companyConfiguration = companyConfigurationRepository
				.findByCompanyIdAndName(companyId, CompanyConfig.THEME);
		
		if (companyConfiguration.isPresent()) {
			servletContext.setAttribute("currentcss", companyConfiguration.get().getValue());
		} else {
			servletContext.setAttribute("currentcss", "white.css");
		}
		return "company/set-theme";
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/set-theme", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveCompanyTheme(@RequestParam String themeColor) throws URISyntaxException {
		log.debug("Web request to save Company Theme");
		Company company = null;
		Company optionalCompany = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		if (optionalCompany != null) {
			company = optionalCompany;
		} else {
			return new ResponseEntity<>("Invalid Company", HttpStatus.BAD_REQUEST);
		}
		Optional<CompanyConfiguration> opTheame = companyConfigurationRepository
				.findByCompanyPidAndName(company.getPid(), CompanyConfig.THEME);
		CompanyConfiguration companyTheme = null;

		if (opTheame.isPresent()) {
			companyTheme = opTheame.get();
			companyTheme.setValue(themeColor);
		} else {
			companyTheme = new CompanyConfiguration();
			companyTheme.setCompany(company);
			companyTheme.setName(CompanyConfig.THEME);
			companyTheme.setValue(themeColor);
		}
		companyConfigurationRepository.save(companyTheme);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/set-theme/getCurrentTheme", method = RequestMethod.GET)
	public @ResponseBody CompanyConfigurationDTO getMobileConfiguration() throws URISyntaxException {
		log.debug("Web request to get company theme");
		Company optionalCompany = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<CompanyConfiguration> opTheme = companyConfigurationRepository
				.findByCompanyPidAndName(optionalCompany.getPid(), CompanyConfig.THEME);
		CompanyConfigurationDTO companyConfigurationDTO = new CompanyConfigurationDTO();
		if (opTheme.isPresent()) {
			companyConfigurationDTO.setValue(opTheme.get().getValue());
		}
		return companyConfigurationDTO;
	}

}
