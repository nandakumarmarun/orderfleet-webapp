package com.orderfleet.webapp.service;

import java.util.Optional;

import com.orderfleet.webapp.domain.enums.MobileUINames;
import com.orderfleet.webapp.web.rest.dto.PerformanceReportMobileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetReportSettingDTO;

/**
 * Service Interface for managing PerformanceReportMobile.
 * 
 * @author Shaheer
 * @since February 27, 2017
 */
public interface PerformanceReportMobileService {

	SalesTargetReportSettingDTO getAccountWiseSalesTargetPerformanceReport(MobileUINames mobileUIName,
			String accountProfilePids);

	SalesTargetReportSettingDTO getUserWiseSalesTargetPerformanceReport(MobileUINames mobileUINames, String login);

	Optional<PerformanceReportMobileDTO> findOneByMobileUINameAndalesTargetReportSetting(MobileUINames mobileUIName,
			String salesTargetReportSettingPid);

	Optional<PerformanceReportMobileDTO> findOneBySalesTargetReportSettingPid(String salesTargetReportSettingPid);
}
