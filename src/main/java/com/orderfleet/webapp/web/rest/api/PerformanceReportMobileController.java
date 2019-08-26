package com.orderfleet.webapp.web.rest.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.MobileUINames;
import com.orderfleet.webapp.service.PerformanceReportMobileService;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;

@RestController
@RequestMapping("/api")
public class PerformanceReportMobileController {

	private final PerformanceReportMobileService performanceReportMobileService;

	public PerformanceReportMobileController(PerformanceReportMobileService performanceReportMobileService) {
		super();
		this.performanceReportMobileService = performanceReportMobileService;
	}

	@Timed
	@GetMapping("/accountwise-sales-performance-report")
	public ResponseEntity<SalesTargetReportSettingDTO> accountwiseSalesPerformanceReport(
			@RequestParam MobileUINames mobileUINames, @RequestParam String accountPids) {
		SalesTargetReportSettingDTO salesTargetReportSettingDTO = performanceReportMobileService
				.getAccountWiseSalesTargetPerformanceReport(mobileUINames, accountPids);
		return new ResponseEntity<>(salesTargetReportSettingDTO, HttpStatus.OK);
	}

	@Timed
	@GetMapping("/userwise-sales-performance-report")
	public ResponseEntity<SalesTargetReportSettingDTO> userwiseSalesPerformanceReport(
			@RequestParam MobileUINames mobileUINames, @RequestParam String login) {
		SalesTargetReportSettingDTO salesTargetReportSettingDTO = performanceReportMobileService
				.getUserWiseSalesTargetPerformanceReport(mobileUINames, login);
		return new ResponseEntity<>(salesTargetReportSettingDTO, HttpStatus.OK);
	}

}
