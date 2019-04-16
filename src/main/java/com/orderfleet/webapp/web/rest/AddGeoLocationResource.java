package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;

/**
 *Controller For Add Geo Location
 *
 * @author fahad
 * @since Jul 5, 2017
 */
@Controller
@RequestMapping("/web")
public class AddGeoLocationResource {

	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private AccountTypeService accountTypeService;
	
	@Inject
	private AccountProfileGeoLocationTaggingService accountProfileGeoLocationTaggingService;
	

	@RequestMapping(value = "/add-geo-location", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAccountProfiles(Model model) throws URISyntaxException {
		
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		model.addAttribute("deactivatedAccountProfiles",
				accountProfileService.findAllByCompanyAndActivated(false));
		return "company/addGeoLocation";
	}
	
	@RequestMapping(value = "/add-geo-location/filterByAccountType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> filterAccountProfilesByAccountTypes(
			@RequestParam String accountTypePids,@RequestParam String importedStatus) throws URISyntaxException {
		List<AccountProfileDTO> accountProfileDTOs = new ArrayList<>();
		boolean imports;
		
			//Not selected
		if (accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		
		//Both selected
		if (!accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			if(importedStatus.equals("true")){
				imports=true;
				accountProfileDTOs.addAll(accountProfileService
						.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(Arrays.asList(accountTypePids.split(",")), imports));
			}else if (importedStatus.equals("false"))  {
				imports=false;
				accountProfileDTOs.addAll(accountProfileService
						.findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(Arrays.asList(accountTypePids.split(",")), imports));
			}else{
				accountProfileDTOs.addAll(accountProfileService
						.findAccountProfileByAccountTypePidInAndActivated(Arrays.asList(accountTypePids.split(","))));
			}
			
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		
		//ImportStatus Selected
		if (accountTypePids.isEmpty() && !importedStatus.isEmpty()) {
			if(importedStatus.equals("true")){
				imports=true;
				accountProfileDTOs.addAll(accountProfileService
						.findAllByCompanyAndAccountImportStatusAndActivated(imports));
			}else if (importedStatus.equals("false")) {
				imports=false; 
				accountProfileDTOs.addAll(accountProfileService
						.findAllByCompanyAndAccountImportStatusAndActivated(imports));
			}else{
				accountProfileDTOs.addAll(accountProfileService.findAllByCompanyAndActivated(true));
			}
			
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		
		//	AccountType Selected	
		if (!accountTypePids.isEmpty() && importedStatus.isEmpty()) {
			accountProfileDTOs.addAll(accountProfileService
					.findAccountProfileByAccountTypePidInAndActivated(Arrays.asList(accountTypePids.split(","))));
			return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
		}
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/add-geo-location/getAccountProfileGeoLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>> getAllGeoLocationTaggingByAccountProfile(@RequestParam String accountProfilePid) {
		List<AccountProfileGeoLocationTaggingDTO>accountProfileGeoLocationTaggingDTOs=accountProfileGeoLocationTaggingService.getAllAccountProfileGeoLocationTaggingByAccountProfile(accountProfilePid);
		return new ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>>(accountProfileGeoLocationTaggingDTOs, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/add-geo-location/saveGeoLocation" ,method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> attachAccountProfile(@RequestParam("geoLocationPid") String geoLocationPid,@RequestParam("accountProfilePid") String accountProfilePid) {
		AccountProfileDTO accountProfileDTO=accountProfileService.findOneByPid(accountProfilePid).get();
		AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO=accountProfileGeoLocationTaggingService.findOneByPid(geoLocationPid).get();
		accountProfileDTO.setLatitude(accountProfileGeoLocationTaggingDTO.getLatitude());
		accountProfileDTO.setLongitude(accountProfileGeoLocationTaggingDTO.getLongitude());
		accountProfileDTO.setLocation(accountProfileGeoLocationTaggingDTO.getLocation());
		accountProfileDTO=accountProfileService.update(accountProfileDTO);
		return new ResponseEntity<AccountProfileDTO>(accountProfileDTO, HttpStatus.OK);
		
	}
}
