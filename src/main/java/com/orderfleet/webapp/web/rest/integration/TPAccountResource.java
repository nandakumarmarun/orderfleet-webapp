package com.orderfleet.webapp.web.rest.integration;

import java.util.List;

import javax.inject.Inject;

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
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SyncOperationService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.SyncOperationDTO;
import com.orderfleet.webapp.web.rest.dto.SyncOperationTimeDTO;

@RestController
@RequestMapping(value = "/api/tp")
public class TPAccountResource {

	private final Logger log = LoggerFactory.getLogger(TPProductProfileManagementService.class);
	
	public UserRepository userRepository;
	private SyncOperationService syncOperationService;

	@Inject
	public TPAccountResource(UserRepository userRepository,SyncOperationService syncOperationService) {
		super();
		this.userRepository = userRepository;
		this.syncOperationService = syncOperationService;
	}

	/**
	 * POST /validate.json : validate the machine.
	 *
	 * @param key
	 *            the validation key
	 * @return the ResponseEntity with status 200 (OK) and the validated user in
	 *         body, or status 500 (Internal Server Error) if the user couldn't
	 *         be validated
	 */
	@RequestMapping(value = "/validate.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Boolean> validateAccountJSON(@RequestBody String key) {
		log.debug("TP_ClintApp Validating..... ");
		return userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).map(user -> {
			if (!key.equals(user.getDeviceKey())) {
				return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
			} 
			return new ResponseEntity<Boolean>(Boolean.TRUE,HttpStatus.OK);
		}).orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
	@RequestMapping(value = "/syncOperationStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<SyncOperationTimeDTO>> getSyncOprationStatus() {
		log.debug("TP_ClintApp Request For SyncOperation... ");
		return ResponseEntity.ok().body(syncOperationService.findAllByCompanyIdAndCompleted(SecurityUtils.getCurrentUsersCompanyId(),false));
		
	}
	
	@RequestMapping(value = "/assigned-syncOperations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SyncOperationDTO> getAssignedSyncOprations() {
		log.debug("tp_clintapp request for all assigned syncoperation...");
		return ResponseEntity.ok().body(syncOperationService.findAllByCompanyId(SecurityUtils.getCurrentUsersCompanyId()));
		
	}
}
