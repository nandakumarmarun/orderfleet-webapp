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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.service.CompanyService;

/**
 * WEB controller for managing the Company performance config
 * 
 * @author Muhammed Riyas T
 * @since Mar 08, 2017
 */
@Controller
@RequestMapping("/web")
public class CompanyPerformanceConfigResource {

	private final Logger log = LoggerFactory.getLogger(CompanyPerformanceConfigResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	/**
	 * GET /companyPerformanceConfig : get companyPerformanceConfig.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/companyPerformanceConfig", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllCompanyPerformanceConfig(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of companyPerformanceConfig");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/companyPerformanceConfig";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/companyPerformanceConfig/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody List<CompanyConfiguration> getCompanyPerformanceConfig(@PathVariable String companyPid) throws URISyntaxException {
		log.debug("Web request to get companyPerformanceConfig");
		
		 List<CompanyConfiguration> configurations=new ArrayList<>();
		
		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository.findByCompanyPidAndName(companyPid, CompanyConfig.COMPANY_PERFORMANCE_BASED_ON);
		Optional<CompanyConfiguration> optUsrWiseCompanyConfig = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.USER_WISE);
		Optional<CompanyConfiguration> optLocWiseCompanyConfig = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_WISE);
		if (optCompanyConfiguration.isPresent()) {
			configurations.add(optCompanyConfiguration.get());
		}
		if (optUsrWiseCompanyConfig.isPresent()) {
			configurations.add(optUsrWiseCompanyConfig.get());
		}
		if (optLocWiseCompanyConfig.isPresent()) {
			configurations.add(optLocWiseCompanyConfig.get());
		}
		return configurations;
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/companyPerformanceConfig", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveCompanyPerformanceConfig(@RequestParam String companyPid,
			@RequestParam String voucherType, @RequestParam String userLocationWise) throws URISyntaxException {
		log.debug("Web request to save AccountPurchaseHistoryDuration ");

		Optional<CompanyConfiguration> optUsrWiseCompanyConfig = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.USER_WISE);
		Optional<CompanyConfiguration> optLocWiseCompanyConfig = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.LOCATION_WISE);

		if (optUsrWiseCompanyConfig.isPresent()) {
			companyConfigurationRepository.delete(optUsrWiseCompanyConfig.get());
		} else if (optLocWiseCompanyConfig.isPresent()) {
			companyConfigurationRepository.delete(optLocWiseCompanyConfig.get());
		}

		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.COMPANY_PERFORMANCE_BASED_ON);

		CompanyConfiguration companyConfiguration = null;

		if (optCompanyConfiguration.isPresent()) {
			companyConfiguration = optCompanyConfiguration.get();
			companyConfiguration.setValue(voucherType);
		} else {
			companyConfiguration = new CompanyConfiguration();
			companyConfiguration.setName(CompanyConfig.COMPANY_PERFORMANCE_BASED_ON);
			companyConfiguration.setValue(voucherType);
			companyConfiguration.setCompany(companyRepository.findOneByPid(companyPid).get());
		}
		companyConfigurationRepository.save(companyConfiguration);

		Optional<CompanyConfiguration> optULWiseCompanyConfig = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.valueOf(userLocationWise));
		if (!optULWiseCompanyConfig.isPresent()) {
			CompanyConfiguration uLWiseCompanyConfig = new CompanyConfiguration();
			uLWiseCompanyConfig.setName(CompanyConfig.valueOf(userLocationWise));
			uLWiseCompanyConfig.setValue("TRUE");
			uLWiseCompanyConfig.setCompany(companyRepository.findOneByPid(companyPid).get());
			companyConfigurationRepository.save(uLWiseCompanyConfig);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}