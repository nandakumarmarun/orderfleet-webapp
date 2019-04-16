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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@Controller
@RequestMapping(value = "/api/tally")
public class PostDatedVoucherTallyController {

	private final Logger log = LoggerFactory.getLogger(PostDatedVoucherTallyController.class);
	
	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private TallyDataUploadService tallyDataUploadService;
	
	
	@PostMapping("/postdated-voucher-tally")
	public ResponseEntity<String> getPostDatedVouchers(@RequestBody List<PostDatedVoucherDTO> postDatedVouchers,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		log.debug("Rest API to insert postdated vouchers "+postDatedVouchers.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			tallyDataUploadService.saveOrUpdatePostDatedVouchers(postDatedVouchers,company);
			log.debug("Inserted all postdated voucher");
			return new ResponseEntity<String>("Successfully inserted postdated vouchers",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
