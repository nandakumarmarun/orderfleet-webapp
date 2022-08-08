package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FirebaseLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FirebaseLocationRepository;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LiveRoutingResponse;
import com.orderfleet.webapp.web.rest.dto.LocationData;

@Service
public class LiveRoutingService {

	

	final Logger log = LoggerFactory.getLogger(LiveRoutingService.class);

	
	private final FirebaseLocationRepository firebaseLocationRepository;
	
	private final CompanyRepository companyRepository;
	
	
	
	static String PID_PREFIX = "FBLOC-";

	public LiveRoutingService(FirebaseLocationRepository firebaseLocationRepository,CompanyRepository companyRepository) {

		super();
		
		this.firebaseLocationRepository = firebaseLocationRepository;
		this.companyRepository = companyRepository;
		}

	

	@Transactional
	public void saveLocationDetails(List<LiveRoutingResponse> routing, Long companyId) {

		// TODO Auto-generated method stub

		firebaseLocationRepository.deleteByCompanyId(companyId);
		
		log.info("Saving Firebase URL data.........");

		long start = System.nanoTime();

		Set<FirebaseLocation> saveUpdateLocation = new HashSet<>();
		for(LiveRoutingResponse locations :routing)
		{
			for(LocationData locationData:locations.getLocations())
			{
			
			FirebaseLocation fblocation = new FirebaseLocation();
			fblocation.setPid(LiveRoutingService.PID_PREFIX + RandomUtil.generatePid());
		     fblocation.setKey(locationData.getKey());
		     fblocation.setCompanyName(locationData.getCompanyName());
		     Optional<Company> company =companyRepository.findByLegalName(locationData.getCompanyName());
		     fblocation.setCompanyId(company.get().getId());
		     String created_date =locationData.getDate();
		     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				LocalDateTime createdDate = LocalDateTime.parse(created_date, formatter);
		     fblocation.setDate(createdDate);
		fblocation.setLatitude(locationData.getLatitude());
		fblocation.setLongitude(locationData.getLongitude());
		saveUpdateLocation.add(fblocation);
		}}
		firebaseLocationRepository.save(saveUpdateLocation);
		
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
		
	}



	

}
