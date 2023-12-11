package com.orderfleet.webapp.web.rest.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * REST controller for AccountProfileGeoLocation
 *
 * @author fahad
 * @since Jul 6, 2017
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountProfileGeoLocationTaggingController {

	private final Logger log = LoggerFactory.getLogger(MasterDataController.class);

	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileGeoLocationTaggingService accountProfileGeoLocationTaggingService;



	@RequestMapping(value = "/account-profile-geo-location-tagging", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileGeoLocationTaggingDTO> saveAccountProfileGeoLocation(
			@RequestBody AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO) {
		System.out.println(accountProfileGeoLocationTaggingDTO);
		if (accountProfileGeoLocationTaggingDTO.getLatitude() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfileGeoLocationTagging",
					"latitude is null", "latitude has no value")).body(null);
		}
		if (accountProfileGeoLocationTaggingDTO.getLongitude() == null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfileGeoLocationTagging",
					"longitude is null", "longitude has no value")).body(null);
		}
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> existingAccountProfile = accountProfileRepository
				.findOneByPid(accountProfileGeoLocationTaggingDTO.getAccountProfilePid());

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
		if (!existingAccountProfile.isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfile",
					"Account Profile Not exists", "Account Profile not Present")).body(null);
		}
		accountProfileGeoLocationTaggingDTO.setGeoTaggingType(GeoTaggingType.MOBILE_TAGGED);
		AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO2 = accountProfileGeoLocationTaggingService
				.save(accountProfileGeoLocationTaggingDTO);

		return new ResponseEntity<AccountProfileGeoLocationTaggingDTO>(accountProfileGeoLocationTaggingDTO2,
				HttpStatus.OK);

	}

	// Get Account Profile Geo Tagging
	@RequestMapping(value = "/account-profile-geo-location", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>> getAccountProfileGeoLocation(
			@RequestParam String accountProfilePid) {
		log.info("Get Account Profile Geo Tagging");
		
		List<AccountProfileGeoLocationTaggingDTO> geoLocationTaggingDTOs = new ArrayList<AccountProfileGeoLocationTaggingDTO>();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> opAccp = accountProfileRepository.findOneByPid(accountProfilePid);
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
		if (opAccp.isPresent()) {
			geoLocationTaggingDTOs = accountProfileGeoLocationTaggingService
					.getAllAccountProfileGeoLocationTaggingByAccountProfileNew(opAccp.get().getPid(),opAccp.get());

		}

		return new ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>>(geoLocationTaggingDTOs, HttpStatus.OK);

	}
}
