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
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@Controller
@RequestMapping(value = "/api/tally")
public class OpeningStockTallyController {

	private final Logger log = LoggerFactory.getLogger(OpeningStockTallyController.class);

	@Inject
	TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	TallyDataUploadService tallyDataUploadService;
	
	
	@PostMapping("/opening-stocks-tally")
	@ResponseBody
	public ResponseEntity<String> uploadOpeningStocks(@RequestBody List<OpeningStockDTO> openingStockDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
		log.debug("Rest API to insert opening stocks "+openingStockDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			tallyDataUploadService.saveOrUpdateOpeningStock(openingStockDtos, company); 
			log.debug("Inserted all opening stock");
			return new ResponseEntity<String>("Successfully inserted opening stock",HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
}
