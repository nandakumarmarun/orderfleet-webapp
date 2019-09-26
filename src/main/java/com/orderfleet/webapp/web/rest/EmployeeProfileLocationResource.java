package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportDetailView;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;

/**
 * Web controller for managing EmployeeProfileLocation.
 * 
 * @author Muhammed Riyas T
 * @since August 31, 2016
 */
@Controller
@RequestMapping("/web")
public class EmployeeProfileLocationResource {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileLocationResource.class);

	@Inject
	private EmployeeProfileLocationService employeeProfileLocationService;

	@Inject
	private LocationService locationService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	/**
	 * GET /employeeProfile-locations : get all the employeeProfile locations.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         employeeProfiles and locations in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/employeeProfile-locations", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getEmployeeProfileLocations(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of EmployeeProfile Locations");
		model.addAttribute("employeeProfileLocations",
				employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));
		model.addAttribute("locations", locationService.findAllByCompanyAndLocationActivated(true));
		return "company/employeeProfileLocations";
	}

	@RequestMapping(value = "/employeeProfile-locations/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<EmployeeProfileDTO>> filterEmployeeProfileLocations(
			@RequestParam("employeePid") String employeePid) {
		log.info("filter Employee Profile Locations-----");
		List<EmployeeProfileDTO> employeeProfiles = new ArrayList<>();

		List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
		List<Long> dashboardUsersIds = dashboardUsers.stream().map(a -> a.getId()).collect(Collectors.toList());

		if (employeePid.equalsIgnoreCase("Dashboard Employee")) {
			employeeProfiles = employeeProfileService.findAllEmployeeByUserIdsIn(dashboardUsersIds);
		} else if (employeePid.equalsIgnoreCase("no")) {
			employeeProfiles = employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true);
		} else if (employeePid != null && !employeePid.equalsIgnoreCase("")) {
			List<Long> userIds = Collections.emptyList();
			Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
			if (opEmployee.isPresent()) {
				userIds = Arrays.asList(opEmployee.get().getUser().getId());
			}
			employeeProfiles = employeeProfileService.findAllEmployeeByUserIdsIn(userIds);
		}

		employeeProfiles
				.sort((EmployeeProfileDTO e1, EmployeeProfileDTO e2) -> e1.getName().compareToIgnoreCase(e2.getName()));

		return new ResponseEntity<>(employeeProfiles, HttpStatus.OK);
	}

	/**
	 * * GET /employeeProfile-locations/save : save employeeProfile locations.
	 * 
	 * @param pid
	 * @param assignedLocations
	 * @return
	 */
	@RequestMapping(value = "/employeeProfile-locations/save", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> save(@RequestParam String employeeProfilePid, @RequestParam String assignedLocations) {
		log.debug("REST request to save assigned assigned locations", employeeProfilePid);
		employeeProfileLocationService.save(employeeProfilePid, assignedLocations);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/employeeProfile-locations/{employeeProfilePid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationDTO>> getEmployeeProfileLocations(@PathVariable String employeeProfilePid) {
		log.debug("Web request to get get Locations by employeeProfile pid : {}", employeeProfilePid);
		return new ResponseEntity<>(
				employeeProfileLocationService.findLocationsByEmployeeProfilePid(employeeProfilePid), HttpStatus.OK);
	}

}
