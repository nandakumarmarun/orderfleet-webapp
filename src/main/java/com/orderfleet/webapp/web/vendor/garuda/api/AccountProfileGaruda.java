package com.orderfleet.webapp.web.vendor.garuda.api;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.garuda.dto.AccountProfileGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.service.AccountProfileGarudaUploadService;

@RestController
@RequestMapping(value = "/api/garuda")
public class AccountProfileGaruda {
	private final Logger log = LoggerFactory.getLogger(AccountProfileGaruda.class);
	
	@Inject
	private AccountProfileGarudaUploadService accountProfileGarudaUploadService;
	
	@RequestMapping(value = "/account-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfiles(
			@Valid @RequestBody List<AccountProfileGarudaDTO> accountProfileDTOs) {
		log.debug("REST request to save AccountProfiles : {}", accountProfileDTOs.size());
		accountProfileGarudaUploadService.saveUpdateAccountProfiles(accountProfileDTOs);
		return new ResponseEntity<>("Account Profiles Uploaded Successfully", HttpStatus.OK);
	}

}
