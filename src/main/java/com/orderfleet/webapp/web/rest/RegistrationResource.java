package com.orderfleet.webapp.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.crypto.KeyGenerator;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FeaturesData;
import com.orderfleet.webapp.domain.ReceivablePayableColumn;
import com.orderfleet.webapp.domain.ReceivablePayableColumnConfig;
import com.orderfleet.webapp.domain.SnrichPartner;
import com.orderfleet.webapp.domain.SnrichProduct;
import com.orderfleet.webapp.domain.SnrichProductCompany;
import com.orderfleet.webapp.domain.enums.Feature;
import com.orderfleet.webapp.domain.enums.PartnerIntegrationSystem;
import com.orderfleet.webapp.repository.FeaturesDataRepository;
import com.orderfleet.webapp.repository.ReceivablePayableColumnConfigRepository;
import com.orderfleet.webapp.repository.ReceivablePayableColumnRepository;
import com.orderfleet.webapp.repository.SnrichPartnerRepository;
import com.orderfleet.webapp.repository.SnrichProductCompanyRepository;
import com.orderfleet.webapp.repository.SnrichProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;
import com.orderfleet.webapp.service.SnrichPartnerCompanyService;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.service.impl.OtpService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;

/**
 * WEB controller for managing the current user's account.
 * 
 * @author Shaheer
 * @since September 07, 2016
 */
@Controller
@SessionAttributes("registrationDto")
public class RegistrationResource {

	@Inject
	private FeaturesDataRepository featuresDataRepository;

	@Inject
	private OtpService otpService;
	
	@Inject
	private CompanyService companyService;
	
	@Inject
	private SnrichPartnerRepository snrichPartnerRepository;
	
	@Inject
	private SnrichPartnerCompanyService snrichPartnerCompanyService;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private CompanyTrialSetUpService companyTrialSetUpService;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SnrichProductRepository snrichProductRepository;
	
	@Inject
	private SnrichProductCompanyRepository snrichProductCompanyRepository;
	
	@Inject
	private ReceivablePayableColumnRepository receivablePayableColumnRepository;
	
	@Inject
	private ReceivablePayableColumnConfigRepository receivablePayableColumnConfigRepository;
	
	private static final String VIEW_REGISTRATION = "account/register";
	private static final String VIEW_REGISTRATION_SUCCESS = "account/register-success";
	private static final String VIEW_REGISTRATION_SUCCESS_TALLY = "account/register-success-tally";
	private static final String VIEW_OTP_VALIDATE = "account/otp";
	//private static final String EMAIL_CONTENT_USER = " has been successfully registered with us. You will be able to login after the verification process, which is normally upto 48 hours maximum. Upon successful completion of verification user details will be emailed to you.";
	private static final String EMAIL_CONTENT_PARTNER = " has been successfully registered. Company can login after the verification process, which is normally upto 48 hours maximum.";
	private static final String FILE_NAME = "salesnrich.tcp";
	private static final String FILE_PATH = "C:/TDL/";
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerAccountPage(Model model,@RequestParam(value="pkey",required=false) String productKey,
													@RequestParam(value="cCode",required=false) String companyCode,
													@RequestParam(value="partnerKey",required=false) String partnerKey,
													@RequestParam(value="cName",required=false) String companyLegalName,
													@RequestParam(value="sProduct",required=false) String snrichProductKey) {
		RegistrationDto registrationDto = new RegistrationDto();
		
		if( (productKey !=null && !productKey.equals("")) && 
			(companyCode !=null && !companyCode.equals("")) &&	
			(partnerKey !=null && !partnerKey.equals("")) && 
			(companyLegalName !=null && !companyLegalName.equals("")) ) 
		{
			Optional<SnrichPartner> partner = snrichPartnerRepository.findOneByPid(partnerKey);
			if(partner.isPresent()) {
				registrationDto.setPartnerKey(partner.get().getPid());
				registrationDto.setPartnerName(partner.get().getName());
				registrationDto.setPkey(productKey);
				registrationDto.setcCode(companyCode);
				registrationDto.setLegalName(companyLegalName);
				registrationDto.setPartnerIntegrationSystem(PartnerIntegrationSystem.THIRD_PARTY_APP);
			}else {
				registrationDto.setPkey("None");
				registrationDto.setcCode("None");
				registrationDto.setPartnerKey("None");
				registrationDto.setPartnerName("None");
			}
		}
		else {
			
			registrationDto.setPkey("None");
			registrationDto.setcCode("None");
			registrationDto.setPartnerKey("None");
			registrationDto.setPartnerName("None");
			//salesNrich partner
//			Optional<SnrichPartner> partner = snrichPartnerRepository.findOneByPid("PTNR-u12SYgkeLM1544182102061");
//			if(partner.isPresent()) {
//				registrationDto.setPartnerKey(partner.get().getPid());
//				registrationDto.setPartnerName(partner.get().getName());
//			}
		}
		
		if(snrichProductKey != null && !snrichProductKey.equals("")) {
			Optional<SnrichProduct> snrichProduct = snrichProductRepository.findOneByPid(snrichProductKey);
			if(snrichProduct.isPresent()) {
				registrationDto.setSnrichProduct(snrichProduct.get().getName());
				registrationDto.setSnrichProductKey(snrichProduct.get().getPid());
			}else {
				registrationDto.setSnrichProduct("None");
				registrationDto.setSnrichProductKey("None");
			}
		}else {
			registrationDto.setSnrichProduct("None");
			registrationDto.setSnrichProductKey("None");
		}
		
		Map<String,String> productMap = new HashMap<String,String>();
		List<SnrichProduct> products = snrichProductRepository.findAll();
		for(SnrichProduct product : products) {
			productMap.put(product.getPid(), product.getName());
		}
		model.addAttribute("snrichProductlist",productMap);
		System.out.println("reg : "+registrationDto.toString());
		model.addAttribute("registrationDto", registrationDto);
//		model.addAttribute("tallyConfig",tallyConfig);
		populatePartnerModel(model);
		return VIEW_REGISTRATION;
	}

	@PostMapping("/register")
	public String registerAccount(@Valid RegistrationDto registrationDto, BindingResult result, Model model) {
		if (result.hasErrors()) {
			populatePartnerModel(model);
			populateProductList(model);
			if("None".equals(registrationDto.getPartnerKey())){
				registrationDto.setSnrichProductKey("None");
			}
			model.addAttribute("registrationDto", registrationDto);
			 return VIEW_REGISTRATION;
		} 
		if(!validRegistrationDetails(registrationDto, model)) {
			populatePartnerModel(model);
			populateProductList(model);
			registrationDto.setSnrichProductKey("None");
			model.addAttribute("registrationDto", registrationDto);
			return VIEW_REGISTRATION;
		}
		int otp = otpService.generateOTP(registrationDto.getLegalName());
		//send otp to mobile
		otpService.sendOtpMessage(registrationDto.getPhone(),otp);
		model.addAttribute("registrationDto", registrationDto);
		return VIEW_OTP_VALIDATE;
	}
	
	@RequestMapping(value = "/validateOtp", method = RequestMethod.GET)
	@Transactional
	public String validateOtpAndCompleteRegistration(@RequestParam("otpnum") int otpnum, SessionStatus status, 
			@ModelAttribute("registrationDto") RegistrationDto registrationDto, Model model) {
		int serverOtp = otpService.getOtp(registrationDto.getLegalName());
		
		if(otpnum == serverOtp) {
			otpService.clearOTP(registrationDto.getLegalName());
			Company company = companyService.createCompany(registrationDto);
			createCompanyFeatures(company); 
			Optional<SnrichProduct> opSnrichProduct =snrichProductRepository.findOneByPid(registrationDto.getSnrichProductKey());
			String activationKey = RandomUtil.generateActivationKey();//for tally activation
			if(opSnrichProduct.isPresent()) {
				//also include tally activation key
				SnrichProductCompany snrichProductCompany = new SnrichProductCompany(opSnrichProduct.get(),company,activationKey,registrationDto.getPartnerIntegrationSystem());
				snrichProductCompanyRepository.save(snrichProductCompany);
			}
			
			
			//partner company association
			if(!registrationDto.getPkey().equals("None") || !registrationDto.getcCode().equals("None"))
			{
				//only for users who register using the link provided by the partner application
				snrichPartnerCompanyService.save(registrationDto, company, 0);
			}
			//admin user creation
			companyTrialSetUpService.createOrderProAdminUser(company,registrationDto);
			companyTrialSetUpService.assignCustomMenuItemToUsers(company.getLegalName(), company);
			//creating mail for company and partner
			Optional<SnrichPartner> partner = snrichPartnerRepository.findOneByPid(registrationDto.getPartnerKey());
			String tallyActivation = "";
			String emailSubject = "";
			
			if( registrationDto.getPartnerIntegrationSystem()==PartnerIntegrationSystem.TALLY ) {
				tallyActivation = "Tally activation Key (for tally activation): "+activationKey+"\n";
				emailSubject = "SalesNrich Mobile App - Company Registration Successful!";
			} else if(registrationDto.getPartnerIntegrationSystem()==PartnerIntegrationSystem.THIRD_PARTY_APP){
				if(partner.isPresent()){
					emailSubject = "SalesNrich - "+partner.get().getName()+" Mobile App - Company Registration Successful!";
				}
			} else if(registrationDto.getPartnerIntegrationSystem()==PartnerIntegrationSystem.CLIENT_APP){
				emailSubject = "SalesNrich Mobile App - Company Registration Successful!";
			}
			
			String adminUser = "Username : "+registrationDto.getUserName()+"\t Password : "+registrationDto.getPassword();
			mailService.sendEmailCompanyRegistered(
					registrationDto.getEmail(),
					emailSubject,
					"Dear Sir/Madam,\n\n\tThe Company "
					+ company.getLegalName()+" has been successfully registered with us,"
					+ " you may login using the following user credentials.\n\n"
					+ tallyActivation
					+ adminUser, false, false, true);
			
			if(partner.isPresent()){
				mailService.sendEmailCompanyRegistered(
						partner.get().getEmail(),
						emailSubject,
						"Dear Sir/Madam,\n\n\tThe Company "
						+company.getLegalName()+EMAIL_CONTENT_PARTNER, false, false, false);
			}
			createCompanyDefaultData(company);
			
			if(registrationDto.getPartnerIntegrationSystem()==PartnerIntegrationSystem.TALLY) {
				return VIEW_REGISTRATION_SUCCESS_TALLY;
			}else {
				
				return VIEW_REGISTRATION_SUCCESS;
			}
		} else {
			model.addAttribute("msg", "The OTP you entered is not valid");
			return VIEW_OTP_VALIDATE;
		}
	}
	
	@GetMapping("/downloadFile/")
	public ResponseEntity<Resource> downloadIntegrationManager(HttpServletRequest request) {
	// Load file as Resource
	Resource resource = new ClassPathResource("snrich.exe");
	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
		.body(resource);
	}
	
	@GetMapping("/download-tally-test/")
	public ResponseEntity<InputStreamResource> downloadTallyTest(HttpServletRequest request) {
		File file = new File(FILE_PATH +"test/"+ FILE_NAME);
		InputStreamResource res = null;
	    try {
	    	res = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+FILE_NAME+"\"")
			.body(res);
	}
	
	@GetMapping("/download-tally-educational/")
	public ResponseEntity<InputStreamResource> downloadTallyEducational(HttpServletRequest request) {
		File file = new File(FILE_PATH +"educational/"+ FILE_NAME);
		InputStreamResource res = null;
	    try {
	    	res = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+FILE_NAME+"\"")
			.body(res);
	}
	
	@GetMapping("/download-tally-paid/")
	public ResponseEntity<InputStreamResource> downloadTallyPaid(HttpServletRequest request) {
		File file = new File(FILE_PATH +"paid/"+ FILE_NAME);
		InputStreamResource res = null;
	    try {
	    	res = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+FILE_NAME+"\"")
			.body(res);
	}
	
	private void createCompanyFeatures(Company company) {
		FeaturesData featuresData = new FeaturesData();
		featuresData.setCompany(company);
		featuresData.setFeature(Feature.BASIC_ORDERPRO);
		featuresDataRepository.save(featuresData);
	}
	
	private boolean validRegistrationDetails(RegistrationDto registrationDto, Model model) {
		if (companyService.findByName(registrationDto.getLegalName()).isPresent()) {
			model.addAttribute("msg", "Company Name already exist");
			return Boolean.FALSE;
		}
		if (companyService.findByAlias(registrationDto.getAlias()).isPresent()) {
			model.addAttribute("msg", "Company Short name already exist");
			return Boolean.FALSE;
		}
		if (companyService.findByEmail(registrationDto.getEmail()).isPresent()) {
			model.addAttribute("msg", "Company Email already exist");
			return Boolean.FALSE;
		}
		if(userRepository.findOneByLogin(registrationDto.getUserName()).isPresent()){
			model.addAttribute("msg", "User Name already exist");
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	private void populatePartnerModel(Model model) {
		Map<String, String> partners = new LinkedHashMap<String,String>();
		for(Object[] object : snrichPartnerRepository.findAllByCompany()){
			partners.put(object[0].toString(),object[1].toString());
		}
		model.addAttribute("partners", partners);
	}
	
	private void populateProductList(Model model) {
		Map<String,String> productMap = new HashMap<String,String>();
		List<SnrichProduct> products = snrichProductRepository.findAll();
		for(SnrichProduct product : products) {
			productMap.put(product.getPid(), product.getName());
		}
		model.addAttribute("snrichProductlist",productMap);
	}
	
private void createCompanyDefaultData(Company company){
		
		
		companyTrialSetUpService.setUpDefaultCompanyData(company);
		companyTrialSetUpService.createDefaultProductGroup(company);
		companyTrialSetUpService.createDefaultProductCategory(company);
		saveDafaultFeatures(company, Feature.Default_Company_Data_SetUp);
		companyTrialSetUpService.setUpDocumentCompanyData(company);
		companyTrialSetUpService.assignDocumentProductCategory(company);
		companyTrialSetUpService.assignDocumentProductGroup(company);
		saveDafaultFeatures(company, Feature.Document_Company_Data_SetUp);
		companyTrialSetUpService.setUpOrderActivityCompanyData(company);
		saveDafaultFeatures(company, Feature.Activity_Company_Data_SetUp);
		companyTrialSetUpService.assignDocumentCompanyData(company);
		saveDafaultFeatures(company, Feature.Document_Company_Data_Assigned);
		
		//assigning company receivable payable column configuration for mobile users
		PartnerIntegrationSystem partnerIntergrationSystem = snrichProductCompanyRepository
				.findPartnerIntegrationSystemByCompanyId(company.getId());
		if(partnerIntergrationSystem.equals(PartnerIntegrationSystem.TALLY)){
			List<ReceivablePayableColumn> rpColumns = receivablePayableColumnRepository.findAll();
			List<ReceivablePayableColumnConfig> columnConfigs = new ArrayList<>();
			for(ReceivablePayableColumn receivablePayableColumn : rpColumns){
				ReceivablePayableColumnConfig rpc = new ReceivablePayableColumnConfig();
				rpc.setReceivablePayableColumn(receivablePayableColumn);
				rpc.setEnabled(true);
				rpc.setCompany(company);
				columnConfigs.add(rpc);
			}
			receivablePayableColumnConfigRepository.save(columnConfigs);
		}
	}
	
	private void saveDafaultFeatures(Company company, Feature feature){
		FeaturesData featuresData = new FeaturesData();
		featuresData.setCompany(company);
		featuresData.setFeature(feature);
		featuresDataRepository.save(featuresData);
	}
	
}