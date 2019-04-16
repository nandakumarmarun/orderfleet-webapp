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
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.web.tally.dto.LocationDTO;
import com.orderfleet.webapp.web.tally.service.AssignTallyDataService;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@Controller
@RequestMapping(value = "/api/tally")
public class LocationTallyController {

	private final Logger log = LoggerFactory.getLogger(LocationTallyController.class);
	
	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private AssignTallyDataService assignTallyDataService;
	
	@Inject
	private TallyDataUploadService tallyDataUploadService;
	
	@Inject
	private LocationRepository locationRepository;
	
	
	@PostMapping("/locations-tally")
	public ResponseEntity<String> getLocation(@RequestBody List<LocationDTO> locationDtos,
			@RequestHeader("key") String tallyKey,@RequestHeader("company") String companyName) {
		log.debug("Rest API to insert locations "+locationDtos.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			Company company = tallyConfiguration.get().getCompany();
			List<Location> exisitngLocations = locationRepository.findAllByCompanyId(company.getId());
			tallyDataUploadService.saveOrUpdateLocations(locationDtos, company);
			log.debug("Inserted all locations");
			assignTallyDataService.locationHierarchyAssociation(locationDtos,company);
			log.debug("Inserted and assigned all locations in locationsHierarchy");
			assignTallyDataService.employeeProfileLocationAssociation(exisitngLocations, locationDtos, company);
			log.debug("Assigned all locations with employees");
			return new ResponseEntity<String>("Successfully inserted locations",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
