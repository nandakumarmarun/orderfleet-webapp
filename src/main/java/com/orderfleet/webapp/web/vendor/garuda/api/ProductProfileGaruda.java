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
import com.orderfleet.webapp.web.vendor.garuda.dto.ProductProfileGarudaDTO;
import com.orderfleet.webapp.web.vendor.garuda.service.ProductProfileGarudaUploadService;

@RestController
@RequestMapping(value = "/api/garuda")
public class ProductProfileGaruda {

	private final Logger log = LoggerFactory.getLogger(ProductProfileGaruda.class);
	
	@Inject
	private ProductProfileGarudaUploadService productProfileGarudaUploadService;

	@RequestMapping(value = "/product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfiles(
			@Valid @RequestBody List<ProductProfileGarudaDTO> productProfileDTOs) {
		
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
		// save/update
		productProfileGarudaUploadService.saveUpdateProductProfiles(productProfileDTOs);
		return new ResponseEntity<>("ProductProfiles Profiles Uploaded Success fully", HttpStatus.OK);
	}

}

