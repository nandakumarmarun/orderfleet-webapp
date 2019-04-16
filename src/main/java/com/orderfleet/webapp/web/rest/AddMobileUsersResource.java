package com.orderfleet.webapp.web.rest;


import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FeaturesData;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.Feature;
import com.orderfleet.webapp.domain.enums.OrderProPaymentMode;
import com.orderfleet.webapp.domain.enums.PartnerIntegrationSystem;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FeaturesDataRepository;
import com.orderfleet.webapp.repository.SnrichProductCompanyRepository;
import com.orderfleet.webapp.repository.SnrichProductRateRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;
import com.orderfleet.webapp.service.async.MailService;

@Controller
@RequestMapping("/web/orderpro")
public class AddMobileUsersResource {
	
	private final Logger log = LoggerFactory.getLogger(AddMobileUsersResource.class);
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private CompanyTrialSetUpService companyTrialSetUpService;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private FeaturesDataRepository featuresDataRepository;
	
	@Inject
	private SnrichProductCompanyRepository snrichProductCompanyRepository;
	
	@Inject
	private SnrichProductRateRepository snrichProductRateRepository;
	
	@Inject
	private MailService mailService;
	
	private static final String EMAIL_SUBJECT = "SalesNrich - Company Verification Completed!";
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getToOrderProDashboard(){
		
		return "company/dashboard-orderpro";
	}

	@RequestMapping(value = "/add-mobile-users", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllMobileUsers(Model model) throws URISyntaxException{
		model.addAttribute("snrichProduct", snrichProductCompanyRepository
				.findSnrichProductByCompanyId(SecurityUtils.getCurrentUsersCompanyId()));
		model.addAttribute("orderProPaymentModes", OrderProPaymentMode.values());
		return "company/add-mobile-users";
	}
	
	@RequestMapping(value = "/add-mobile-users/product-rate", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<Double> getProductRates(@RequestParam OrderProPaymentMode orderProPaymentMode, 
			@RequestParam String snrichProductPid){
		log.debug("Web request to get Product Rates by Payment mode");
		Optional<Double> rate = snrichProductRateRepository
				.findRateBySnrichProductPidAndOrderProPaymentMode(snrichProductPid, orderProPaymentMode);
		if(rate.isPresent()){
			return new ResponseEntity<Double>(rate.get(), HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
		
	
	@RequestMapping(value = "/add-mobile-users/create-users", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> createMobileUsers(@RequestParam int usersCount){
		log.debug("Web request to create Users");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			Set<Authority> authorities = new HashSet<>();
			authorities.add(new Authority("ROLE_EXECUTIVE"));
			Long userCount = userRepository.countByCompanyPidAndAuthoritiesIn(company.getPid(), authorities);
			PartnerIntegrationSystem partnerIntergrationSystem = snrichProductCompanyRepository
					.findPartnerIntegrationSystemByCompanyId(company.getId());
			
			
			if(!featureExist(company,Feature.Create_Users)){
				companyTrialSetUpService.createExecutiveUsers(company.getAlias(), company, usersCount, userCount+1);
				saveFeatures(company, Feature.Create_Users);
			}else{
				companyTrialSetUpService.createExecutiveUsers(company.getAlias(), company, usersCount, userCount+1);
			}
			
			
			//creating employee for tally integrated partners 
			if(partnerIntergrationSystem.equals(PartnerIntegrationSystem.TALLY)){
				if(!featureExist(company,Feature.Employee_Created)){
					companyTrialSetUpService.createEmployees(company.getLegalName(), company);
					saveFeatures(company, Feature.Employee_Created);
				}else{
					companyTrialSetUpService.createAdditionalEmployees(company.getLegalName(), company, usersCount);
				}
				
				if(!featureExist(company,Feature.Employee_To_Location_Assigned)){
					companyTrialSetUpService.assignEmployeeToLocationsForTally(company);
					saveFeatures(company, Feature.Employee_To_Location_Assigned);
				}else{
					companyTrialSetUpService.assignEmployeeToLocationsForTally(company);
				}
			}
			
			if(!featureExist(company,Feature.Default_Data_To_Users_Assigned)){
				companyTrialSetUpService.assignToOrderProUsers(company.getLegalName(), company, usersCount);
				companyTrialSetUpService.assignDefaultProductGroups(company.getPid(), usersCount);
				companyTrialSetUpService.assignDefaultProductCategories(company.getPid(), usersCount);
				saveFeatures(company, Feature.Default_Data_To_Users_Assigned);
			}else{
				companyTrialSetUpService.assignToOrderProUsers(company.getLegalName(), company, usersCount);
				companyTrialSetUpService.assignDefaultProductGroups(company.getPid(), usersCount);
				companyTrialSetUpService.assignDefaultProductCategories(company.getPid(), usersCount);
			}
			
			
			if(!featureExist(company,Feature.MenuItem_To_Users_Assigned)){
				companyTrialSetUpService.assignCustomMenuItemToUsers(company.getLegalName(), company);
				saveFeatures(company, Feature.MenuItem_To_Users_Assigned);
			} else {
				companyTrialSetUpService.assignCustomMenuItemToAdditionalUsers(company, usersCount);
			}
			
			//for tally partners mobile menu item is Primary Mobile Menu (PMM)
			if(partnerIntergrationSystem.equals(PartnerIntegrationSystem.TALLY)){
				if(!featureExist(company,Feature.Mobile_MenuItem_To_Users_Assigned)){
					companyTrialSetUpService.assignMobileMenuItemToUsers(company.getLegalName(), company);
					saveFeatures(company, Feature.Mobile_MenuItem_To_Users_Assigned);
				} else {
					companyTrialSetUpService.assignMobileMenuItemToAdditionalUsers(company, usersCount);
				}
			} 
			//for others Orderpro Mobile Menu (OMM)
			else {
				if(!featureExist(company,Feature.Mobile_MenuItem_To_Users_Assigned)){
					companyTrialSetUpService.assignCustomMobileMenuItemToUsers(company.getLegalName(), company);
					saveFeatures(company, Feature.Mobile_MenuItem_To_Users_Assigned);
				} else {
					companyTrialSetUpService.assignCustomMobileMenuItemToAdditionalUsers(company, usersCount);
				}
			}
				
			
			List<User> users = userRepository.findAllByCompanyPid(company.getPid());
			StringBuilder userCredentials = new StringBuilder();
			if(users.size() > 1){
				for(User user : users){
					if(!user.getFirstName().equals("Admin")){
						userCredentials.append("Username : "+user.getLogin()+"    Password : "+user.getPassword()+"\n");
					}
				}
				mailService.sendEmailCompanyRegistered(company.getEmail(),EMAIL_SUBJECT,"Dear Sir/Madam,\n\n\tThe Company "
						+company.getLegalName()+" has been activated successfully, you may login using the following user credentials.\n\n"
						+"Mobile users :\n"+userCredentials, 
						false, false, false);
			}
			return new ResponseEntity<>("Success",HttpStatus.OK);
		
	}
	
	public void saveFeatures(Company company, Feature feature){
		FeaturesData featuresData = new FeaturesData();
		featuresData.setCompany(company);
		featuresData.setFeature(feature);
		featuresDataRepository.save(featuresData);
	}
	
	public boolean featureExist(Company company, Feature feature){
		Optional<FeaturesData> featuresData = featuresDataRepository
				.findByCompanyIdAndFeature(company.getId(), feature);
		return featuresData.isPresent();
	}
}
