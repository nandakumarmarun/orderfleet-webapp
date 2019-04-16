/**
 * 
 */
package com.orderfleet.webapp.web.rest;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyIntegrationModule;
import com.orderfleet.webapp.domain.IntegrationModule;
import com.orderfleet.webapp.repository.CompanyIntegrationModuleRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.IntegrationModuleRepository;

/**
 * @author Anish
 * @since Aug 23, 2018
 *
 */

@Controller
@RequestMapping("/web")
public class CompanyIntegrationModuleResource {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Inject
	CompanyRepository companyRepository;
	
	@Inject
	IntegrationModuleRepository integrationModuleRepository;
	
	@Inject
	CompanyIntegrationModuleRepository companyIntegrationModuleRepository;
	
	@RequestMapping(value = "/company-integration-module" , method = RequestMethod.GET)
	public String getIntegrationModuleForm(Model model)
	{
		log.debug("Web request for company integration module form");
		
		model.addAttribute("companies", companyRepository.findAllCompaniesByActivatedTrue());
		model.addAttribute("integration_module",integrationModuleRepository.findAll());
		
		return "site_admin/company-integration-module";
	}
	
	@RequestMapping(value = "/company-integration-module" , method = RequestMethod.POST)
	public @ResponseBody CompanyIntegrationModule saveIntegrationModule(@RequestParam String companyPid,@RequestParam String integrationModuleName,
																		@RequestParam String baseUrl, @RequestParam boolean autoUpdate)
	{
		log.debug("Web request to add company Integration Module");
		CompanyIntegrationModule companyIntegrationModule = new CompanyIntegrationModule();
		Company company = companyRepository.findOneByPid(companyPid).get();
		companyIntegrationModule.setCompany(company);
		IntegrationModule integrationModule = integrationModuleRepository.findOne(integrationModuleName);
		companyIntegrationModule.setIntegrationModule(integrationModule);
		companyIntegrationModule.setBaseUrl(baseUrl);
		companyIntegrationModule.setCreatedDate(LocalDateTime.now());
		companyIntegrationModule.setAutoUpdate(autoUpdate);
	
		return companyIntegrationModuleRepository.save(companyIntegrationModule);
	}
	
}
