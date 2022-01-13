package com.orderfleet.webapp.web.rest;



import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.CompanyService;


@RequestMapping("/web")
@Controller
public class SummaryOfCompanyResource {
	@Inject
	private CompanyService companyService;
	
	@GetMapping("/company-count")
	@Timed
	public String getCompanyUser(Model model) {
		System.out.println("Entered in to controller");
		model.addAttribute("companyActive",companyService.findActiveCompanyCount());
		model.addAttribute("companyDeactive",companyService.findDeactiveCompanyCount());
		model.addAttribute("companyActiveName",companyService.findActiveCompanyName());
		model.addAttribute("companyDeactiveName",companyService.findDeactiveCompanyName());
		return "site_admin/company-count";
	}
}
