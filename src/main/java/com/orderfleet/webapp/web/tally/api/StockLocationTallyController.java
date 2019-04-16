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
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.StockLocationDTO;
import com.orderfleet.webapp.web.tally.service.AssignTallyDataService;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@Controller
@RequestMapping(value = "/api/tally")
public class StockLocationTallyController {
	
	private final Logger log = LoggerFactory.getLogger(StockLocationTallyController.class);

	@Inject
	TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	TallyDataUploadService tallyDataUploadService;
	
	@Inject
	private AssignTallyDataService assignTallyDataService;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	
	@PostMapping("/stock-locations-tally")
	@ResponseBody
	public ResponseEntity<String> uploadStockLocations(@RequestBody List<StockLocationDTO> stockLocationDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		
		log.debug("Rest API to insert stock locations "+stockLocationDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			List<StockLocation> existingStockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
			tallyDataUploadService.saveOrUpdateStockLocation(stockLocationDtos, company);
			assignTallyDataService.documentStockLocationAssociation(existingStockLocations, stockLocationDtos, company);
			assignTallyDataService.userStockLocationAssociation(existingStockLocations, stockLocationDtos, company);
			log.debug("Inserted all Stock Locations");
			return new ResponseEntity<String>("Successfully inserted stock locations",HttpStatus.OK);
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
}
