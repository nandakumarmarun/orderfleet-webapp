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
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
@Controller
@RequestMapping("/web")
public class AccountWithoutLocationResource {

	private final Logger log = LoggerFactory.getLogger(AccountWithoutLocationResource.class);

	@Inject
	private LocationService locationService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@RequestMapping(value = "/account-without-location", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Pageable pageable, Model model) {
        log.info("get all accountprofiles");
		model.addAttribute("locations", locationService.findAllByCompanyAndLocationActivated(true));

		return "company/accountWithoutLocation";
	}

	@RequestMapping(value = "/account-without-location/load", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> loadAccounts() {
		

		List<AccountProfile> accProfile =accountProfileRepository.findAllAccountProfilesWithoutLocation();
		
		List<AccountProfileDTO> accDTO = new ArrayList<>();
		for(AccountProfile accp:accProfile)
		{
			AccountProfileDTO accpDTO = new AccountProfileDTO(); ;
		     accpDTO.setName(accp.getName());
		     accpDTO.setPid(accp.getPid());
		     accDTO.add(accpDTO);
		}
		return new ResponseEntity<>(accDTO, HttpStatus.OK);

	}
	@RequestMapping(value = "/account-without-location/locations", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<LocationDTO>> locationAccountProfiles(@RequestParam String accountProfilePid) {
		log.debug("REST request to location Account Profiles : {}", accountProfilePid);
		List<LocationDTO> locationDTOs = locationAccountProfileService
				.findAllLocationByAccountProfilePid(accountProfilePid);
		return new ResponseEntity<>(locationDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/account-without-location/assign-locations", method = RequestMethod.POST)
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
