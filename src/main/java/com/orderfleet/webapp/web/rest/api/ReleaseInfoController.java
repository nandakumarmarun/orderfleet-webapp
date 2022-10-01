package com.orderfleet.webapp.web.rest.api;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.itextpdf.text.pdf.hyphenation.TernaryTree.Iterator;
import com.orderfleet.webapp.service.ReleaseInfoService;
import com.orderfleet.webapp.web.rest.api.dto.ReleaseInfo;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RestController
@RequestMapping("/api/v1")
public class ReleaseInfoController {
	private final Logger log = LoggerFactory.getLogger(ReleaseInfoController.class);
	@Inject
	ReleaseInfoService releaseInfoService;
	
	@GetMapping("/Release-info")
	@Timed
	public ResponseEntity<ReleaseInfo> getReleaseInfo(){
		log.debug("REST request to get ReleaseInfo");
		ReleaseInfo result = releaseInfoService.getReleaseInfoDetails();
		return new ResponseEntity<>(result, HttpStatus.OK);
		
	}
}
