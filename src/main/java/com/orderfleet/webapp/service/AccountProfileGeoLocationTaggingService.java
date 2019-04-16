package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;

public interface AccountProfileGeoLocationTaggingService {

	String PID_PREFIX = "APGLT-";
	
	AccountProfileGeoLocationTaggingDTO save(AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO);
	
	List<AccountProfileGeoLocationTaggingDTO> getAllAccountProfileGeoLocationTaggingByAccountProfile(String pid);
	
	Optional<AccountProfileGeoLocationTaggingDTO> findOneByPid(String pid);
	
}
