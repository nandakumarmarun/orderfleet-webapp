package com.orderfleet.webapp.web.rest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CountryC;
import com.orderfleet.webapp.domain.DistrictC;
import com.orderfleet.webapp.domain.StateC;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CounrtyCRepository;
import com.orderfleet.webapp.repository.DistrictCRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.StateCRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.impl.LocationAccountProfileManagementServiceImpl;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.CountryCDTO;
import com.orderfleet.webapp.web.rest.dto.DistrictCDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.StateCDTO;

@Controller
@RequestMapping("/web")
public class LocationAccountProfileManagementResource {

	private final Logger log = LoggerFactory.getLogger(LocationAccountProfileManagementResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	
	@Inject
	private LocationAccountProfileManagementServiceImpl locationAccountProfileServiceImpl;
	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private LocationService locationService;

	@RequestMapping(value = "/locationAccountProfileManagement", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) {

		model.addAttribute("AccountProfiles", locationAccountProfileServiceImpl.findAllByCompanyAndActivated(true));
		model.addAttribute("locations", locationService.findAllByCompanyAndLocationActivated(true));

		return "company/locationAccountProfileManagement";

	}

	@RequestMapping(value = "/locationAccountProfileManagement/assign-locations", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedLocations(@RequestParam String locationPid,
			@RequestParam String assignedAccountProfiles) {
		log.debug("REST request to save assigned Account Profiles : {}", locationPid);
		
//		String[] locationPids = locationPid.split(",");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(assignedAccountProfiles).get();
		log.debug("AccountProfile===" + accountProfile);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
		logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		locationAccountProfileRepository.deleteByAccountProfilePid(SecurityUtils.getCurrentUsersCompanyId(),
				accountProfile.getId());
		
		locationAccountProfileService.save(locationPid, assignedAccountProfiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}