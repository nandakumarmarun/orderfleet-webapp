package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;


@Controller
@RequestMapping("/web")
public class TerritoryWiseAccount {

	private final Logger log = LoggerFactory.getLogger(TerritoryWiseAccount.class);
	@Inject
	private LocationService locationService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private LocationAccountProfileService locationAccountProfileService;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@RequestMapping("/territory-wise-account")
	@Timed
	public String getAllTerritoryWiseAccount(Model model) throws URISyntaxException{
		model.addAttribute("locations", locationService.findAllByCompanyAndLocationActivated(true));
		return "company/territoryWiseAccount";
	}
	
	@RequestMapping(value="/territory-wise-account/filter",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfileByLocation(@RequestParam("territoryPid") String territoryPid,@RequestParam("activated") String activated) {
		List<AccountProfileDTO> accountProfileDTOs=new ArrayList<>();
		if(territoryPid.equals("no") && activated.equals("true")){
			List<LocationDTO> locationDTOs=locationService.findAllByCompany();
			List<String>locationPids=new ArrayList<>();
			if(locationDTOs!=null){
				for(LocationDTO locationDTO:locationDTOs)
				locationPids.add(locationDTO.getPid());
			}
			accountProfileDTOs=locationAccountProfileService.findAllAccountProfileByLocationPidInAndActivated(locationPids, true);
		}else if (territoryPid.equals("no") && activated.equals("false")) {
			List<LocationDTO> locationDTOs=locationService.findAllByCompany();
			List<String>locationPids=new ArrayList<>();
			if(locationDTOs!=null){
				for(LocationDTO locationDTO:locationDTOs)
				locationPids.add(locationDTO.getPid());
			}
			accountProfileDTOs=locationAccountProfileService.findAllAccountProfileByLocationPidInAndActivated(locationPids, false);
		}else if (!territoryPid.equals("no") && activated.equals("false")) {
			accountProfileDTOs=locationAccountProfileService.findAccountProfileByLocationPidAndActivated(territoryPid, false);
		}else if (!territoryPid.equals("no") && activated.equals("true")) {
			accountProfileDTOs=locationAccountProfileService.findAccountProfileByLocationPidAndActivated(territoryPid, true);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}
	
	@Timed
	@RequestMapping(value = "/territory-wise-account/activateAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> accountProfileDeactivated(@Valid @RequestParam String accountprofiles,@Valid @RequestParam  boolean status) {
		String[] accountProfiles = accountprofiles.split(",");
		for (String accountprofile : accountProfiles) {
			accountProfileService.updateAccountProfileStatus(accountprofile, status);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@RequestMapping(value = "/territory-wise-account/locations", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<LocationDTO>> locationAccountProfiles(@RequestParam String accountProfilePid) {
		System.out.println(("REST request to location Account Profiles : {}  ,"+ accountProfilePid));
		List<LocationDTO> locationDTOs = locationAccountProfileService
				.findAllLocationByAccountProfilePid(accountProfilePid);
		return new ResponseEntity<>(locationDTOs, HttpStatus.OK);
	}
	@RequestMapping(value = "/territory-wise-account/assign-locations", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedLocations(@RequestParam String locationPid,
			@RequestParam String assignedAccountProfiles) {
		log.debug("REST request to save assigned Account Profiles : {}", locationPid);
		String[] locationPids = locationPid.split(",");
		
		AccountProfile accountProfile = accountProfileRepository.findOneByPid(assignedAccountProfiles).get();
		

		locationAccountProfileRepository.deleteByAccountProfilePid(SecurityUtils.getCurrentUsersCompanyId(),
				accountProfile.getId());
		locationAccountProfileService.save(locationPids[0], assignedAccountProfiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
