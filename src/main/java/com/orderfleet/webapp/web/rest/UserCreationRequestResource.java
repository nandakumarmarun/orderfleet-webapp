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
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.Feature;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FeaturesDataRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;
import com.orderfleet.webapp.service.SnrichPartnerCompanyService;
import com.orderfleet.webapp.service.async.MailService;

@Controller
@RequestMapping("/web")
public class UserCreationRequestResource {

	private final Logger log = LoggerFactory.getLogger(UserCreationRequestResource.class);
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private CompanyTrialSetUpService companyTrialSetUpService;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SnrichPartnerCompanyService snrichPartnerCompanyService;
	
	@Inject
	private FeaturesDataRepository featuresDataRepository;
	
	@Inject
	private MailService mailService;
	
	private static final String EMAIL_SUBJECT = "SalesNrich - Company Verification Completed!";
	
	
	@RequestMapping(value = "/user-creation-request", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN})
	public String getAllSnrichParnterCompany(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Partners with Companies");
		model.addAttribute("snrichPartners", snrichPartnerCompanyService.findByUsersCountGreaterThanZeroAndUserAdminExist(0));
		return "site_admin/user-creation-request";
	}
	
	@RequestMapping(value = "/user-creation-request/create-users", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> createUsers(@RequestParam String companyPid,@RequestParam int usersCount,@RequestParam Long id){
		log.debug("Web request to create Users");
		Optional<Company> company = companyRepository.findOneByPid(companyPid);
		if(company.isPresent()){
			Set<Authority> authorities = new HashSet<>();
			authorities.add(new Authority("ROLE_EXECUTIVE"));
			Long userCount = userRepository.countByCompanyPidAndAuthoritiesIn(companyPid, authorities);
			
			
			if(!featureExist(company.get(),Feature.Create_Users)){
				companyTrialSetUpService.createExecutiveUsers(company.get().getAlias(), company.get(), usersCount, userCount+1);
				saveFeatures(company.get(), Feature.Create_Users);
			}
			if(!featureExist(company.get(),Feature.Default_Company_Data_SetUp)){
				companyTrialSetUpService.setUpDefaultCompanyData(company.get());
				companyTrialSetUpService.createDefaultProductGroup(company.get());
				companyTrialSetUpService.createDefaultProductCategory(company.get());
				saveFeatures(company.get(), Feature.Default_Company_Data_SetUp);
			}
			
//			if(!featureExist(company.get(),Feature.Employee_Created)){
//				companyTrialSetUpService.createEmployees(company.get().getLegalName(), company.get());
//				saveFeatures(company.get(), Feature.Employee_Created);
//			}else{
//				companyTrialSetUpService.createAdditionalEmployees(company.get().getLegalName(), company.get(), usersCount);
//				saveFeatures(company.get(), Feature.Employee_Created);
//			}
//			if(!featureExist(company.get(),Feature.Employee_Hierarchy_Created)){
//				companyTrialSetUpService.createEmployeeHierarchies(company.get().getLegalName(), company.get());
//				saveFeatures(company.get(), Feature.Employee_Hierarchy_Created);
//			}else{
//				companyTrialSetUpService.createAdditionalEmployeeHierarchies(company.get().getLegalName(), company.get(), usersCount);
//				saveFeatures(company.get(), Feature.Employee_Hierarchy_Created);
//			}
			if(!featureExist(company.get(),Feature.Document_Company_Data_SetUp)){
				companyTrialSetUpService.setUpDocumentCompanyData(company.get());
				companyTrialSetUpService.assignDocumentProductCategory(company.get());
				companyTrialSetUpService.assignDocumentProductGroup(company.get());
				saveFeatures(company.get(), Feature.Document_Company_Data_SetUp);
			}
			if(!featureExist(company.get(),Feature.Activity_Company_Data_SetUp)){
				companyTrialSetUpService.setUpOrderActivityCompanyData(company.get());
				saveFeatures(company.get(), Feature.Activity_Company_Data_SetUp);
			}
//			if(!featureExist(company.get(),Feature.Employee_To_Location_Assigned)){
//				companyTrialSetUpService.assignEmployeeToLocations(company.get());
//				saveFeatures(company.get(), Feature.Employee_To_Location_Assigned);
//			}
//			else{
//				companyTrialSetUpService.assignAdditionalEmployeeToLocations(company.get(), usersCount);
//				saveFeatures(company.get(), Feature.Employee_To_Location_Assigned);
//			}
			if(!featureExist(company.get(),Feature.Default_Data_To_Users_Assigned)){
				companyTrialSetUpService.assignToOrderProUsers(company.get().getLegalName(), company.get(), usersCount);
				companyTrialSetUpService.assignDefaultProductGroups(company.get().getPid(), usersCount);
				companyTrialSetUpService.assignDefaultProductCategories(company.get().getPid(), usersCount);
				saveFeatures(company.get(), Feature.Default_Data_To_Users_Assigned);
			}	
//			}else{
//				companyTrialSetUpService.assignToAdditionalUsers(company.get(), usersCount);
//				saveFeatures(company.get(), Feature.Default_Data_To_Users_Assigned);
//			}
//			if(!featureExist(company.get(),Feature.Dashboard_SetUp)){
//				companyTrialSetUpService.dashboardUsers(company.get().getLegalName(), company.get());
//				saveFeatures(company.get(), Feature.Dashboard_SetUp);
//			}else{
//				companyTrialSetUpService.dashboardAdditionalUsers(company.get().getLegalName(), 
//						company.get(), usersCount, userCount.intValue());
//				saveFeatures(company.get(), Feature.Dashboard_SetUp);
//			}
			if(!featureExist(company.get(),Feature.Document_Company_Data_Assigned)){
				companyTrialSetUpService.assignDocumentCompanyData(company.get());
				saveFeatures(company.get(), Feature.Document_Company_Data_Assigned);
			}
			if(!featureExist(company.get(),Feature.MenuItem_To_Users_Assigned)){
				companyTrialSetUpService.assignCustomMenuItemToUsers(company.get().getLegalName(), company.get());
				saveFeatures(company.get(), Feature.MenuItem_To_Users_Assigned);
			}
//			else{
//				companyTrialSetUpService.assignMenuItemToAdditionalUsers(company.get(), usersCount);
//				saveFeatures(company.get(), Feature.MenuItem_To_Users_Assigned);
//			}
			if(!featureExist(company.get(),Feature.Mobile_MenuItem_To_Users_Assigned)){
				companyTrialSetUpService.assignCustomMobileMenuItemToUsers(company.get().getLegalName(), company.get());
				saveFeatures(company.get(), Feature.Mobile_MenuItem_To_Users_Assigned);
			}
//			else{
//				companyTrialSetUpService.assignMobileMenuItemToAdditionalUsers(company.get(), usersCount);
//				saveFeatures(company.get(), Feature.Mobile_MenuItem_To_Users_Assigned);
//			}
			
			List<User> users = userRepository.findAllByCompanyPid(companyPid);
			StringBuilder userCredentials = new StringBuilder();
			for(User user : users){
				if(!user.getFirstName().equals("Admin")){
					userCredentials.append("Username : "+user.getLogin()+"    Password : "+user.getPassword()+"\n");
				}
			}
			mailService.sendEmailCompanyRegistered(company.get().getEmail(),EMAIL_SUBJECT,"Dear Sir/Madam,\n\n\tThe Company "
					+company.get().getLegalName()+" has been activated successfully, you may login using the following user credentials.\n\n"
					+"Mobile users :\n"+userCredentials, 
					false, false, false);
			
			
			SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findOne(id);
			snrichPartnerCompany.setUsersCount(0);
			snrichPartnerCompanyRepository.save(snrichPartnerCompany);
			return new ResponseEntity<>("Success",HttpStatus.OK);
		}else{
			return new ResponseEntity<>("Company doesnot exist",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	@RequestMapping(value = "/user-creation-request/create-admin-users", method = RequestMethod.GET)
//	@Timed
//	public ResponseEntity<String> createAdminUsers(@RequestParam String companyPid){
//		log.debug("Web request to create Admin Users");
//		Optional<Company> company = companyRepository.findOneByPid(companyPid);
//		if(company.isPresent()){
//			companyTrialSetUpService.createOrderProAdminUser(company.get());
//			return new ResponseEntity<>("Success",HttpStatus.OK);
//		}else{
//			return new ResponseEntity<>("Company doesnot exist",HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	@RequestMapping(value = "/user-creation-request/admin-users-exist", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<Long> adminUsersExist(@RequestParam String companyPid){
		log.debug("Web request to check Admin Users exist");
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority("ROLE_OP_ADMIN"));
		Long userAdminCount = userRepository.countByCompanyPidAndAuthoritiesIn(companyPid, authorities);
		if(userAdminCount == 0){
			return new ResponseEntity<>(0L,HttpStatus.OK);
		}else{
			return new ResponseEntity<>(userAdminCount,HttpStatus.OK);
		}
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
