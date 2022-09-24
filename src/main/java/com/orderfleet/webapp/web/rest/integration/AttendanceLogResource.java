package com.orderfleet.webapp.web.rest.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.AttendanceLogService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.integration.dto.AttendanceLogDTO;

@RestController
@RequestMapping(value = "/api/tp/v1")
public class AttendanceLogResource {
	
	private final Logger log = LoggerFactory.getLogger(AttendanceLogResource.class);
	
	@Inject
	AttendanceLogService attendanceLogService;
	
	@Inject
	private AttendanceRepository attendanceRepository;
	
	@RequestMapping(value = "/attendance-logs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AttendanceLogDTO>> getAttendanceLogs(@RequestParam String fromDate) {
		log.debug("REST request to Get Attendance Logs : {}", fromDate);

       List<AttendanceLogDTO> result = attendanceLogService.getattendaceLog(fromDate);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/attendance-logs-Change-Status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> getAttendanceLogsChangeStatus(@RequestBody List<String> AttendancePids) {
		log.debug("REST request Update Attendance Logs status : {}", AttendancePids.size());

       if(!AttendancePids.isEmpty()) {
    	   attendanceRepository.updateAttendaceLogIsUploadedUsingPid(AttendancePids);
       }
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
