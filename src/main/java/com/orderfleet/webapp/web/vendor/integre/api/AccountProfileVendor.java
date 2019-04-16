package com.orderfleet.webapp.web.vendor.integre.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.rest.dto.ReceiptVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.DataDownloadController;
import com.orderfleet.webapp.web.vendor.integre.dto.AccountProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.AssignUploadDataService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataDownloadService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class AccountProfileVendor {
	
	private final Logger log = LoggerFactory.getLogger(AccountProfileVendor.class);
	
	@Inject
	private MasterDataUploadService masterDataUploadService;
	
	@Inject
	private MasterDataDownloadService masterDataDownloadService;
	
	@Inject
	private AssignUploadDataService assignUploadDataService;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	

	@PostMapping("/account-profile.json")
	public ResponseEntity<String> uploadAccountProfiles(@RequestBody List<AccountProfileVendorDTO> accountProfileDtos,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if(snrichPartnerCompany==null) {
			return new ResponseEntity<String>("Company doesnot exit",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			List<AccountProfile> existingAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
			masterDataUploadService.saveOrUpdateAccountProfile(accountProfileDtos,company);
			for(AccountProfile accountProfile : existingAccountProfiles) {
				accountProfileDtos.removeIf(aProfile -> aProfile.getCode().equals(accountProfile.getAlias()));
			}
			List<String> newAccountProfileAlias = accountProfileDtos.stream()
					.map(aProfile -> aProfile.getCode()).collect(Collectors.toList());
			assignUploadDataService.locationAccountProfileAssociation(newAccountProfileAlias,company);
			return new ResponseEntity<String>("Success",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@RequestMapping(value = "/get-new-account-profiles.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<AccountProfileVendorDTO> getAccountProfilesJson(@RequestHeader("X-COMPANY") String companyId) throws URISyntaxException {
		List<AccountProfileVendorDTO> aountProfileVendorDTOs = new ArrayList<>();
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if(snrichPartnerCompany == null) {
			return aountProfileVendorDTOs;
		}
		Company company = snrichPartnerCompany.getCompany();
		log.debug("REST request to download new Account Profiles : {}");
		aountProfileVendorDTOs = masterDataDownloadService.getAllNewAccountProfiles(company.getId(),DataSourceType.MOBILE);
		
		return aountProfileVendorDTOs;
		
	}
	
	@PostMapping("/update-new-customers.json")
	public ResponseEntity<String> updateNewAccountProfiles(@RequestBody List<AccountProfileVendorDTO> accountProfileDtos,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		if(snrichPartnerCompany == null) {
			return new ResponseEntity<String>("Company not present",HttpStatus.EXPECTATION_FAILED);
		}
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			List<AccountProfile> existingAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
			masterDataUploadService.saveOrUpdateAccountProfileByPid(accountProfileDtos,company);
//			for(AccountProfile accountProfile : existingAccountProfiles) {
//				accountProfileDtos.removeIf(aProfile -> aProfile.getCode().equals(accountProfile.getAlias()));
//			}
//			List<String> newAccountProfileAlias = accountProfileDtos.stream()
//					.map(aProfile -> aProfile.getCode()).collect(Collectors.toList());
//			assignUploadDataService.locationAccountProfileAssociation(newAccountProfileAlias,company);
			return new ResponseEntity<String>("Success",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
