package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyBilling;
import com.orderfleet.webapp.repository.CompanyBillingRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.impl.CompanyBillingServiceImpl;
import com.orderfleet.webapp.web.rest.dto.CompanyBillingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
@RequestMapping("/web")
public class BillingSettingResource {
	
	private final Logger log = LoggerFactory.getLogger(BillingSettingResource.class);
	
	
	@Inject
	private CompanyService companyService;
	
	@Inject
	private CompanyBillingServiceImpl companyBillingServiceImpl;
	
	@Inject
	private CompanyBillingRepository companyBillingRepository;
	
	
	
	@RequestMapping(value = "/billing-setting", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.PARTNER })
	public String getAllCompanies(Model model) throws URISyntaxException {
		log.info("Web Request to get company billing Details");
		
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("billList",companyBillingRepository.findAll());
	
		return "site_admin/billingSetting";
	}
	
	@RequestMapping(value = "/billing-setting", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<CompanyBillingDTO> createCompanyBilling(@Valid @RequestBody CompanyBillingDTO companyDTO)
			throws URISyntaxException {
		log.debug("Web request to save Company : {}");
		
		  Optional<CompanyBilling> comp = companyBillingServiceImpl.findByCompanyPid(companyDTO.getCompanyPid());
		  if(comp.isPresent())
		  {
			 return  ResponseEntity.badRequest().headers(
						HeaderUtil.createFailureAlert("company", "This company already have billing Details", "A company already have billing Details"))
						.body(null);
		  }
		
		CompanyBilling billing = companyBillingServiceImpl.save(companyDTO);
		CompanyBillingDTO result = new CompanyBillingDTO(billing);
		System.out.println("Result :"+result);
		log.debug("Successfully Saved");
		
		return new ResponseEntity<>(result,HttpStatus.OK);
		
	
	}
	
	@RequestMapping(value = "/billing-setting", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyBillingDTO> updateCompany(@Valid @RequestBody CompanyBillingDTO companyDTO)
			throws URISyntaxException {
		log.debug("REST request to update Company : {}", companyDTO);
	
		CompanyBillingDTO result = companyBillingServiceImpl.update(companyDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "Invalid Company ID")).body(null);
		}
		return new ResponseEntity<>(result,HttpStatus.OK);
				
	}

	
	@RequestMapping(value = "/billing-setting/changeDueBillDate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Void> changeBillDate(@RequestParam String companyPid)
	{
		CompanyBilling billing = companyBillingServiceImpl.saveNewBillDate(companyPid);
		
		CompanyBillingDTO result = new CompanyBillingDTO(billing);
		System.out.println("Result :"+result);
		log.debug("Successfully Saved");
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	@RequestMapping(value = "/billing-setting/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyBillingDTO> getCompanyBilling(@PathVariable String pid) {
		log.debug("Web request to get Company by pid : {}", pid);
		return companyBillingServiceImpl.findOneByPid(pid).map(companybillingDTO -> new ResponseEntity<>(companybillingDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
