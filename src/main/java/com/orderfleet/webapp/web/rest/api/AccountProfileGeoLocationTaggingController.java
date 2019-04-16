package com.orderfleet.webapp.web.rest.api;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 *REST controller for AccountProfileGeoLocation
 *
 * @author fahad
 * @since Jul 6, 2017
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountProfileGeoLocationTaggingController {
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private AccountProfileGeoLocationTaggingService accountProfileGeoLocationTaggingService;
	
	@RequestMapping(value="/account-profile-geo-location-tagging",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileGeoLocationTaggingDTO> saveAccountProfileGeoLocation(@RequestBody AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO) {
		System.out.println(accountProfileGeoLocationTaggingDTO);
			if(accountProfileGeoLocationTaggingDTO.getLatitude()==null){
				return ResponseEntity.badRequest().headers(
						HeaderUtil.createFailureAlert("accountProfileGeoLocationTagging", "latitude is null", "latitude has no value"))
						.body(null); 
			}
			if(accountProfileGeoLocationTaggingDTO.getLongitude()==null){
				return ResponseEntity.badRequest().headers(
						HeaderUtil.createFailureAlert("accountProfileGeoLocationTagging", "longitude is null", "longitude has no value"))
						.body(null); 
			}
			
			Optional<AccountProfile> existingAccountProfile = accountProfileRepository
					.findOneByPid(accountProfileGeoLocationTaggingDTO.getAccountProfilePid());
			if (!existingAccountProfile.isPresent()) {
				return ResponseEntity.badRequest().headers(
						HeaderUtil.createFailureAlert("accountProfile", "Account Profile Not exists", "Account Profile not Present"))
						.body(null);
			}
			AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO2=accountProfileGeoLocationTaggingService.save(accountProfileGeoLocationTaggingDTO);
			
		return new ResponseEntity<AccountProfileGeoLocationTaggingDTO>(accountProfileGeoLocationTaggingDTO2, HttpStatus.OK);
		
	}
}
