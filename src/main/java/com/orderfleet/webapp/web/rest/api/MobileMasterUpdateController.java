package com.orderfleet.webapp.web.rest.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.MobileMasterDetail;
import com.orderfleet.webapp.domain.MobileMasterUpdate;
import com.orderfleet.webapp.repository.MobileMasterDetailRepository;
import com.orderfleet.webapp.repository.MobileMasterUpdateRepository;
import com.orderfleet.webapp.service.MobileMasterDetailService;
import com.orderfleet.webapp.service.MobileMasterUpdateService;
import com.orderfleet.webapp.web.rest.dto.MobileMasterUpdateDTO;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MobileMasterUpdateController {

	private final Logger log = LoggerFactory.getLogger(MobileMasterUpdateController.class);

	@Autowired
	MobileMasterUpdateService mobileMasterUpdateService;
	@Autowired
	MobileMasterDetailService mobileMasterDetailService;

	@Autowired
	MobileMasterUpdateRepository mobileMasterUpdateRepository;

	@Autowired
	MobileMasterDetailRepository mobileMasterDetailRepository;

	@RequestMapping(value = "/mobile-master-update-status", method = RequestMethod.POST, 
						produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Boolean> mobileMasterUpdateStatus(
									@RequestBody MobileMasterUpdateDTO mobileMasterUpdateDTO) {
		log.info("Save mobile master update details");
		MobileMasterUpdate mobileMasterUpdate = null;
		if(mobileMasterUpdateDTO != null && mobileMasterUpdateDTO.getUserPid() != null) {
			
			mobileMasterUpdate = mobileMasterUpdateService.convertMobileMasterUpdate(mobileMasterUpdateDTO);
		}
		
		System.out.println("MobileMasterUpdateId :"+mobileMasterUpdate.getId());
		List<MobileMasterDetail> mmdList = mobileMasterDetailService
				.convertMobileMasterDetails(mobileMasterUpdateDTO.getMobileMasterDetailDtos(), mobileMasterUpdate);
		if (mmdList != null && mmdList.size() > 0) {
			mobileMasterUpdate.setMobileMasterDetails(mmdList);
			mobileMasterUpdate = mobileMasterUpdateService.saveMobileMasterUpdate(mobileMasterUpdate);
			return new ResponseEntity<>(true, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(false, HttpStatus.CONFLICT);
		}L̥
		
	}
}
