package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.GeoLocationVarianceDTO;

@Controller
@RequestMapping("/web")
public class GeoLocationVarianceReportResource {

	private final Logger log = LoggerFactory.getLogger(GeoLocationVarianceReportResource.class);

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private UserService userService;

	@Inject
	private EmployeeProfileLocationService employeeProfileLocationService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private GeoLocationService geoLocationService;

	@RequestMapping(value = "/geo-location-variance-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getGeoLocationVariances(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of geo location variance report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/geoLocationVarianceReport";
	}

	@RequestMapping(value = "/geo-location-variance-report/get-account-profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getAllAccountProfiles(@RequestParam("userPid") String userPid) {
		log.debug("Web request to get list of Account Profiles using userPid ", userPid);

		List<String> locationPids = employeeProfileLocationService.findLocationsByUserPid(userPid).stream()
				.map(a -> a.getPid()).collect(Collectors.toList());
		List<AccountProfileDTO> accountProfiles = locationAccountProfileService
				.findAccountProfileByLocationPidIn(locationPids);

		return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
	}

	@RequestMapping(value = "/geo-location-variance-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<GeoLocationVarianceDTO>> getAllGeoLocationTaggingByAccountProfile(
			@RequestParam("userPid") String userPid, @RequestParam String accountProfilePid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to get geo location variance report by userPid " + userPid);

		List<GeoLocationVarianceDTO> geoLocationVarianceDTOs = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			geoLocationVarianceDTOs = getFilterData(userPid, accountProfilePid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			geoLocationVarianceDTOs = getFilterData(userPid, accountProfilePid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			geoLocationVarianceDTOs = getFilterData(userPid, accountProfilePid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			geoLocationVarianceDTOs = getFilterData(userPid, accountProfilePid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			geoLocationVarianceDTOs = getFilterData(userPid, accountProfilePid, fromDateTime, toFateTime);
		}

		return new ResponseEntity<>(geoLocationVarianceDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/geo-location-variance-report/calculateVariance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Boolean> calculateVariance(@RequestParam("executionPid") String executionPid) {
		log.debug("Web request to update geo location variance report by executionPid " + executionPid);

		ExecutiveTaskExecution execution = executiveTaskExecutionRepository.findByExecutionPid(executionPid);

		if (execution.getLatitude() != null && execution.getAccountProfile().getLatitude() != null
				&& execution.getLatitude().doubleValue() != 0
				&& execution.getAccountProfile().getLatitude().doubleValue() != 0) {
			String variance = "";
			double accLocLat = execution.getAccountProfile().getLatitude().doubleValue();
			double accLocLng = execution.getAccountProfile().getLongitude().doubleValue();

			double exeLocLat = execution.getLatitude().doubleValue();
			double exeLocLng = execution.getLongitude().doubleValue();

			String origin = accLocLat + "," + accLocLng;
			String destination = exeLocLat + "," + exeLocLng;

			if (!origin.equals(destination)) {
				double distance = geoLocationService.computeDistanceBetween(accLocLat, accLocLng, exeLocLat, exeLocLng);
				variance = distance + " KM";
			}else {
				variance = "0 KM";
			}
			execution.setLocationVariance(variance);
			// update
			executiveTaskExecutionRepository.save(execution);

			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {

			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	// calculateVariance

	private List<GeoLocationVarianceDTO> getFilterData(String userPid, String accountProfilePid, LocalDate fDate,
			LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();

		if (userPid.equals("no") && accountProfilePid.equals("no")) {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.isEmpty()) {
				Optional<User> user = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin());
				if (user.isPresent()) {
					executiveTaskExecutions = executiveTaskExecutionRepository
							.findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(user.get().getPid(), fromDate,
									toDate);
				}
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInAndDateBetweenOrderByDateDesc(userIds, fromDate, toDate);
			}
		} else if (!userPid.equals("no") && accountProfilePid.equals("no")) {

			List<String> locationPids = employeeProfileLocationService.findLocationsByUserPid(userPid).stream()
					.map(a -> a.getPid()).collect(Collectors.toList());
			List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
					.findAccountProfileByLocationPidIn(locationPids);
			List<String> accountProfilePids = accountProfileDTOs.stream().map(AccountProfileDTO::getPid)
					.collect(Collectors.toList());
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByDateBetweenAndUserPidAndAccountProfilePidIn(fromDate, toDate, userPid,
							accountProfilePids);
		}

		else if (!userPid.equals("no") && !accountProfilePid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByDateBetweenAndUserPidAndAccountProfilePidIn(fromDate, toDate, userPid,
							Arrays.asList(accountProfilePid));
		}

		List<GeoLocationVarianceDTO> result = executiveTaskExecutions.stream().map(GeoLocationVarianceDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
