package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.mapper.LocationMapper;

/**
 * Service Implementation for managing EmployeeProfileLocation.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
@Service
@Transactional
public class EmployeeProfileLocationServiceImpl implements EmployeeProfileLocationService {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileLocationServiceImpl.class);

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationMapper locationMapper;

	/**
	 * Save a EmployeeProfileLocation.
	 * 
	 * @param employeeProfilePid
	 * @param assignedLocations
	 */
	@Override
	public void save(String employeeProfilePid, String assignedLocations) {
		log.debug("Request to save EmployeeProfile Location");

		EmployeeProfile employeeProfile = employeeProfileRepository.findOneByPid(employeeProfilePid).get();
		String[] locations = assignedLocations.split(",");

		List<EmployeeProfileLocation> employeeProfileLocations = new ArrayList<>();

		for (String locationPid : locations) {
			Location location = locationRepository.findOne(Long.valueOf(locationPid));
			employeeProfileLocations.add(new EmployeeProfileLocation(employeeProfile, location));
		}
		employeeProfileLocationRepository.deleteByEmployeeProfilePid(employeeProfilePid);
		employeeProfileLocationRepository.save(employeeProfileLocations);
	}

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findLocationsByEmployeeProfileIsCurrentUser() {
		List<Location> locationList = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locationList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findLocationsByEmployeeProfilePid(String employeeProfilePid) {
		log.debug("Request to get all Locations");
		List<Location> locationList = employeeProfileLocationRepository
				.findLocationsByEmployeeProfilePid(employeeProfilePid);
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locationList);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findLocationsByEmployeeProfileIsCurrentUserAndlastModifiedDate(LocalDateTime lastModifiedDate) {
		List<Location> locationList = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUserAndlastModifiedDate(lastModifiedDate);
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locationList);
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findLocationsByUserPid(String userPid) {
		List<Location> locationList = employeeProfileLocationRepository.findLocationsByUserPid(userPid);
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locationList);
		return result;
	}

	@Override
	public List<LocationDTO> findLocationsByUserPidIn(List<String> UserPids) {
		List<Location> locationList = employeeProfileLocationRepository.findLocationsByUserPidIn(UserPids);
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locationList);
		return result;
	}
}
