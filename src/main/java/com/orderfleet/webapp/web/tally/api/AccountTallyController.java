package com.orderfleet.webapp.web.tally.api;


import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;
import com.orderfleet.webapp.web.tally.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.tally.service.AssignTallyDataService;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@Controller
@RequestMapping(value = "/api/tally")
public class AccountTallyController {

	private final Logger log = LoggerFactory.getLogger(AccountTallyController.class);
	
	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private AssignTallyDataService assignTallyDataService;
	
	@Inject
	private TallyDataUploadService tallyDataUploadService;
	
	
	@PostMapping("/account-profiles-tally")
	public ResponseEntity<String> getAccountProfiles(@RequestBody List<AccountProfileDTO> accountProfileDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		log.debug("Rest API to insert account profiles "+accountProfileDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			List<AccountProfile> updatedAccountProfiles = tallyDataUploadService.saveOrUpdateAccountProfile(accountProfileDtos,company);
			assignTallyDataService.locationAccountProfileAssociation(updatedAccountProfiles,accountProfileDtos,company);
			log.debug("Inserted all account profiles");
			return new ResponseEntity<String>("Successfully inserted account profiles",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/receivable-payable-tally")
	public ResponseEntity<String> getReceivablesPayables(@RequestBody List<ReceivablePayableDTO> receivablePayableDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
			log.debug("Rest API to insert receivable payables "+receivablePayableDtos.size());
			Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
			
			if(tallyConfiguration.isPresent()){
				Company company = tallyConfiguration.get().getCompany();
				tallyDataUploadService.saveOrReceivablePayable(receivablePayableDtos, company);
				return new ResponseEntity<String>("Successfully inserted receivable payables",HttpStatus.OK);
			}else{
				return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
	
	
	@PostMapping("/gst-ledger-tally")
	public ResponseEntity<String> getGstLedgers(@RequestBody List<GstLedgerDTO> gstLedgers,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		log.debug("Rest API to insert gst ledgers "+gstLedgers.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			tallyDataUploadService.saveOrUpdateGstLedgers(gstLedgers, company);
			log.debug("Inserted all gst ledgers");
			return new ResponseEntity<String>("Successfully inserted gst ledgers",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
