package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FirebaseLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.FirebaseLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LiveRoutingResponse;
import com.orderfleet.webapp.web.rest.dto.LocationData;

@Service
public class LiveRoutingService {

	

	final Logger log = LoggerFactory.getLogger(LiveRoutingService.class);

	private final UserRepository userRepository;
	
	private final CompanyRepository companyRepository;
	
	private final AttendanceService attendanceService;
	
	private final AttendanceRepository attendanceRepository;
	
	private final FirebaseLocationRepository firebaseLocationRepository;
	
	static String PID_PREFIX = "FBLOC-";

	public LiveRoutingService(UserRepository userRepository, CompanyRepository companyRepository,AttendanceService attendanceService,
			AttendanceRepository attendanceRepository,BulkOperationRepositoryCustom bulkOperationRepositoryCustom,FirebaseLocationRepository firebaseLocationRepository) {

		super();
		
		this.firebaseLocationRepository = firebaseLocationRepository;
		this.attendanceService = attendanceService;
		this.attendanceRepository = attendanceRepository;
		this.userRepository = userRepository;
		this.companyRepository = companyRepository;
	}

	

	@Transactional
	public void saveLocationDetails(List<LiveRoutingResponse> routing) {

		// TODO Auto-generated method stub

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
