package com.orderfleet.webapp.web.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.web.rest.dto.MobileMasterUpdateDTO;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MobileMasterUpdateController {

	private final Logger log = LoggerFactory.getLogger(MobileMasterUpdateController.class);
	
	@RequestMapping(value = "/mobile-master-update-status", method = RequestMethod.POST, 
						produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<MobileMasterUpdateDTO> mobileMasterUpdateStatus(
									@RequestBody MobileMasterUpdateDTO mobileMasterUpdateDTO) {
		log.info("Save mobile master update details");
		return null;
	}
}
