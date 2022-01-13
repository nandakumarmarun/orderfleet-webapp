package com.orderfleet.webapp.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyUserCountDTO;

@RequestMapping("/web")
@Controller
public class CompanyDeactiveUserCountResource {

private final Logger log = LoggerFactory.getLogger(CompanyUserDeviceResource.class);
	
	
	@Inject
	private CompanyService companyService;
	
	@Inject
	private UserService userService;
	
	@GetMapping("/company-deactiveuser-count")
	@Timed
	public String getCompanyUser(Model model) {
		model.addAttribute("companies",companyService.findAllCompanySortedByName());
		return "site_admin/company-deactiveuser-count";
	}
	
	@RequestMapping(value="/company-deactiveuser-count/loadAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<CompanyUserCountDTO>> getAllCompaniesDeActivatedUser()
	{ 
		log.info("web request to find all company deactive user count");
		
	List<CompanyUserCountDTO> companyUser = userService.findAllCompanyDeActivatedUsercount();
	
		 
		return new ResponseEntity<>( companyUser, HttpStatus.OK);
		
	}
	@RequestMapping(value="/company-deactiveuser-count/loadUserCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	
	@Timed
	public ResponseEntity<List<CompanyUserCountDTO>> getActivatedUser(@RequestParam("companyId")Long companyid )
	{ 
		log.info("web request to find user count");
		
	List<CompanyUserCountDTO> usercount = userService.findDeactivatedUsercountByCompanyId(companyid);
	
		 
		return new ResponseEntity<>(usercount, HttpStatus.OK);
		
	}
}
