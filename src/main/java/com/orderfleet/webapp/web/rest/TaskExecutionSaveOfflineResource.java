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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;

@Controller
@RequestMapping("/web")
public class TaskExecutionSaveOfflineResource {

	private final Logger log = LoggerFactory.getLogger(CompanyPerformanceConfigResource.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@RequestMapping(value = "/task-execution-save-offline", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllTaskExecutionSaveOffline(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of taskExecutionSaveOffline");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("saveofflineCompany",
				companyConfigurationRepository.findAllCompanyByName(CompanyConfig.TASK_EXECUTION_OFFLINE_SAVE));
		return "site_admin/taskExecutionSaveOffline";
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/task-execution-save-offline", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> saveTaskExecutionSaveOffline(@RequestParam String companyPid,
			@RequestParam String booleanType) throws URISyntaxException {
		log.debug("Web request to save task execution save offline ");
		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.TASK_EXECUTION_OFFLINE_SAVE);
		CompanyConfiguration companyConfiguration = null;
		if (optCompanyConfiguration.isPresent()) {
			companyConfiguration = optCompanyConfiguration.get();
			companyConfiguration.setValue(booleanType);
		} else {
			companyConfiguration = new CompanyConfiguration();
			companyConfiguration.setCompany(companyRepository.findOneByPid(companyPid).get());
			companyConfiguration.setName(CompanyConfig.TASK_EXECUTION_OFFLINE_SAVE);
			companyConfiguration.setValue(booleanType);
		}
		companyConfigurationRepository.save(companyConfiguration);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/task-execution-save-offline/{companyPid}", method = RequestMethod.GET)
	public @ResponseBody String getTaskExecutionSaveOffline(@PathVariable String companyPid) throws URISyntaxException {
		log.debug("Web request to get task execution save offline");
		Optional<CompanyConfiguration> optCompanyConfiguration = companyConfigurationRepository
				.findByCompanyPidAndName(companyPid, CompanyConfig.TASK_EXECUTION_OFFLINE_SAVE);
		if (optCompanyConfiguration.isPresent()) {
			return optCompanyConfiguration.get().getValue();
		}
		return null;
	}
}
