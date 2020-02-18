package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.LiveTrackingDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

/**
 * Web controller for managing Live tracking view.
 * 
 * @author Muhammed Riyas T
 * @since Sep 16, 2016
 */
@Controller
@RequestMapping("/web")
public class HeatMapController {

	private final Logger log = LoggerFactory.getLogger(HeatMapController.class);

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private AttendanceService attendanceService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileMapper accountProfileMapper;

	@RequestMapping("/heat-map")
	public String executives(Model model) {
		log.info("heatmap.........................");
		model.addAttribute("companyId", SecurityUtils.getCurrentUsersCompanyId());
		return "company/heatMap";
	}

	@RequestMapping(value = "/getCustomerDetails", method = RequestMethod.GET)
	public @ResponseBody List<AccountProfileDTO> getCustomerDetails() {
		// get user under current users

		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyIdAndGeoTaggingTypeNotEqual(
				SecurityUtils.getCurrentUsersCompanyId(), GeoTaggingType.NOT_TAGGED);

		List<AccountProfileDTO> accountProfileDtos = accountProfileMapper
				.accountProfilesToAccountProfileDTOs(accountProfiles);
		System.out.println(accountProfiles.size()+"--------------------");
		
		System.out.println(accountProfiles);

		return accountProfileDtos;
	}

}
