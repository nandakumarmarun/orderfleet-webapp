package com.orderfleet.webapp.web.rest;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.ReleaseInfoService;
import com.orderfleet.webapp.web.rest.api.dto.ReleaseInfo;

@Controller
@RequestMapping("/web")
public class RealeaseInfoResource {
	
		private final Logger log = LoggerFactory.getLogger(RealeaseInfoResource.class);
		
		@Inject
		ReleaseInfoService releaseInfoService;
		
		
	
		@RequestMapping("/Release-info")
		public String releaseInfo(Model model) {
			log.info("Releasing info.........................");
			
			return "company/releaseInfo";
		}
		
		
		@RequestMapping(value ="/Release-info/Release-data" ,method=RequestMethod.GET ,produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<ReleaseInfo> getReleaseInfo(){
			log.debug("REST request to get ReleaseInfo");
			ReleaseInfo result = releaseInfoService.getReleaseInfoDetails();
		
			return new ResponseEntity<>(result, HttpStatus.OK);
			
		}


}
