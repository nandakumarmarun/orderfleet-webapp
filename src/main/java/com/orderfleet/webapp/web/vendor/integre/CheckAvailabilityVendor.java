package com.orderfleet.webapp.web.vendor.integre;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderfleet.webapp.security.AuthoritiesConstants;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class CheckAvailabilityVendor {
	
	
	@GetMapping("/ping")
	public ResponseEntity<String> ping(@RequestHeader("X-COMPANY") String companyId) {
		return new ResponseEntity<String>("Pong => " + companyId, HttpStatus.OK);		
	}
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return new ResponseEntity<String>("test", HttpStatus.OK);		
	}

}
