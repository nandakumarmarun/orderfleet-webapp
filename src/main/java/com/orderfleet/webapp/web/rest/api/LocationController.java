package com.orderfleet.webapp.web.rest.api;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;

@RestController
@RequestMapping("/api")
public class LocationController {

	private final Logger log = LoggerFactory.getLogger(LocationController.class);

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@RequestMapping(value = "/create-location", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Timed
	public ResponseEntity<String> saveLocation(@RequestParam String name, @RequestParam String parentPid) {
		log.debug("Save location and assign location to hierarchy {} ", name);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		if (locationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), name).isPresent()) {
			return ResponseEntity.badRequest().body("Location already in use");
		}
		Optional<Location> optionalParentLoc = locationRepository.findOneByPid(parentPid);
		if (!optionalParentLoc.isPresent()) {
			return ResponseEntity.badRequest().body("Parent Location not found.");
		}
		Location location = new Location();
		location.setActivated(true);
		location.setCompany(company);
		location.setName(name);
		location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
		location = locationRepository.save(location);
		
		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUserLogin());
		if(employee == null){
			return ResponseEntity.badRequest().body("No Employee found.");
		}
		employeeProfileLocationRepository.save(new EmployeeProfileLocation(employee, location));

		Long version = locationHierarchyRepository.findMaxVersionByCompanyId(company.getId());
		locationHierarchyRepository.insertLocationHierarchyWithParent(version, location.getId(),
				optionalParentLoc.get().getId());
		 return ResponseEntity.ok(location.getPid());
	}
}
