package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.geolocation.api.GeoLocationServiceException;
import com.orderfleet.webapp.geolocation.model.TowerLocation;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Service Implementation for managing UnableToFindLocations.
 *
 * @author Athul
 * @since Apr 16, 2018
 *
 */

@Controller
@RequestMapping("/web")
public class UnableToFindLocations {

	private final Logger log = LoggerFactory.getLogger(UnableToFindLocations.class);

	@Inject
	private CompanyService companyService;

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private GeoLocationService geoLocationService;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@RequestMapping(value = "/unable-to-find-locations", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getCompanies(Model model) throws URISyntaxException {
		log.debug("Web request to get a list of Companies");
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("locationTypes", LocationType.values());
		return "site_admin/unable-to-find-locations";
	}

	@RequestMapping(value = "/unable-to-find-locations/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public Map<String, CompanyViewDTO> getUnableToFindLocationsCount(@RequestParam String companyPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam String locationType) throws URISyntaxException {
		log.debug("Web request to Get Count of unable to find locations using company Pid: {}", companyPid);
		Map<String, CompanyViewDTO> locationMap = new HashMap<>();

		if (filterBy.equals("TODAY")) {
			locationMap = getFilterData(companyPid, LocalDate.now(), LocalDate.now(), locationType);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			locationMap = getFilterData(companyPid, yesterday, yesterday, locationType);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			locationMap = getFilterData(companyPid, weekStartDate, LocalDate.now(), locationType);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			locationMap = getFilterData(companyPid, monthStartDate, LocalDate.now(), locationType);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			locationMap = getFilterData(companyPid, fromDateTime, toDateTime, locationType);
		}
		return locationMap;
	}

	private Map<String, CompanyViewDTO> getFilterData(String companyPid, LocalDate fDate, LocalDate tDate,
			String locationType) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		Map<String, CompanyViewDTO> flocationMap = new HashMap<>();
		List<String> locations = Arrays.asList("Unable to find location", "No Location");
		List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs = new ArrayList<ExecutiveTaskExecutionDTO>();
		LocationType lctiontype = null;

		if (!locationType.equalsIgnoreCase("no")) {
			lctiontype = LocationType.valueOf(locationType);
			if (companyPid.equalsIgnoreCase("no")) {
				List<CompanyViewDTO> companies = companyService.findAllCompaniesByActivatedTrue();
				for (CompanyViewDTO company : companies) {
					String selectCompanyPid = company.getPid();
					executiveTaskExecutionDTOs = executiveTaskExecutionService
							.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, selectCompanyPid,
									fromDate, toDate, lctiontype);

					Long noLocationCount = executiveTaskExecutionDTOs.stream()
							.filter(exec -> exec.getLocation().equalsIgnoreCase("No Location")).count();
					Long unableLocationCount = executiveTaskExecutionDTOs.stream()
							.filter(exec -> exec.getLocation().equalsIgnoreCase("Unable to find location")).count();

					company.setPincode(String.valueOf(unableLocationCount));
					company.setAddress2(String.valueOf(noLocationCount));
					flocationMap.put(company.getLegalName(), company);
				}
			} else {
				Optional<CompanyViewDTO> rCompany = companyService.findOneByPid(companyPid);
				CompanyViewDTO company = (rCompany.get());
				executiveTaskExecutionDTOs = executiveTaskExecutionService
						.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, companyPid, fromDate,
								toDate, lctiontype);

				Long noLocationCount = executiveTaskExecutionDTOs.stream()
						.filter(exec -> exec.getLocation().equalsIgnoreCase("No Location")).count();
				Long unableLocationCount = executiveTaskExecutionDTOs.stream()
						.filter(exec -> exec.getLocation().equalsIgnoreCase("Unable to find location")).count();
				company.setPincode(String.valueOf(unableLocationCount));
				company.setAddress2(String.valueOf(noLocationCount));
				flocationMap.put(company.getLegalName(), company);
			}
		} else {
			if (companyPid.equalsIgnoreCase("no")) {
				List<CompanyViewDTO> companies = companyService.findAllCompaniesByActivatedTrue();
				for (CompanyViewDTO company : companies) {
					String selectCompanyPid = company.getPid();
					executiveTaskExecutionDTOs = executiveTaskExecutionService
							.getByCompanyPidAndLocationInAndDateBetween(locations, selectCompanyPid,
									fromDate, toDate);

					Long noLocationCount = executiveTaskExecutionDTOs.stream()
							.filter(exec -> exec.getLocation().equalsIgnoreCase("No Location")).count();
					Long unableLocationCount = executiveTaskExecutionDTOs.stream()
							.filter(exec -> exec.getLocation().equalsIgnoreCase("Unable to find location")).count();

					company.setPincode(String.valueOf(unableLocationCount));
					company.setAddress2(String.valueOf(noLocationCount));
					flocationMap.put(company.getLegalName(), company);
				}
			} else {
				Optional<CompanyViewDTO> rCompany = companyService.findOneByPid(companyPid);
				CompanyViewDTO company = (rCompany.get());
				executiveTaskExecutionDTOs = executiveTaskExecutionService
						.getByCompanyPidAndLocationInAndDateBetween(locations, companyPid,
								fromDate, toDate);

				Long noLocationCount = executiveTaskExecutionDTOs.stream()
						.filter(exec -> exec.getLocation().equalsIgnoreCase("No Location")).count();
				Long unableLocationCount = executiveTaskExecutionDTOs.stream()
						.filter(exec -> exec.getLocation().equalsIgnoreCase("Unable to find location")).count();
				company.setPincode(String.valueOf(unableLocationCount));
				company.setAddress2(String.valueOf(noLocationCount));
				flocationMap.put(company.getLegalName(), company);
			}

		}

		return flocationMap;
	}

	@RequestMapping(value = "/unable-to-find-locations/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<ExecutiveTaskExecutionDTO> getUnableToFindLocationn(@RequestParam String companyPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam String locationType) throws URISyntaxException {
		log.debug("Web request to Get Count of unable to find locations using company Pid: {}", companyPid);
		List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs = new ArrayList<ExecutiveTaskExecutionDTO>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutionDTOs = getFilteredData(companyPid, LocalDate.now(), LocalDate.now(), locationType);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			executiveTaskExecutionDTOs = getFilteredData(companyPid, yesterday, yesterday, locationType);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutionDTOs = getFilteredData(companyPid, weekStartDate, LocalDate.now(), locationType);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutionDTOs = getFilteredData(companyPid, monthStartDate, LocalDate.now(), locationType);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutionDTOs = getFilteredData(companyPid, fromDateTime, toDateTime, locationType);
		}

		return executiveTaskExecutionDTOs;
	}

	private List<ExecutiveTaskExecutionDTO> getFilteredData(String companyPid, LocalDate fDate, LocalDate tDate,
			String locationType) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs = new ArrayList<ExecutiveTaskExecutionDTO>();

		List<String> locations = Arrays.asList("Unable to find location", "No Location");

		if (locationType.equalsIgnoreCase("no")) {
			executiveTaskExecutionDTOs = executiveTaskExecutionService
					.getByCompanyPidAndLocationInAndDateBetween(locations, companyPid, fromDate, toDate);
		} else {
			if (locationType.equalsIgnoreCase("GpsLocation")) {
				executiveTaskExecutionDTOs = executiveTaskExecutionService
						.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, companyPid, fromDate,
								toDate, LocationType.GpsLocation);
			} else if (locationType.equalsIgnoreCase("FlightMode")) {
				executiveTaskExecutionDTOs = executiveTaskExecutionService
						.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, companyPid, fromDate,
								toDate, LocationType.FlightMode);
			}

			else if (locationType.equalsIgnoreCase("NoLocation")) {
				executiveTaskExecutionDTOs = executiveTaskExecutionService
						.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, companyPid, fromDate,
								toDate, LocationType.NoLocation);
			}

			else if (locationType.equalsIgnoreCase("TowerLocation")) {
				executiveTaskExecutionDTOs = executiveTaskExecutionService
						.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, companyPid, fromDate,
								toDate, LocationType.TowerLocation);
			}

		}
		return executiveTaskExecutionDTOs;

	}

	@RequestMapping(value = "/unable-to-find-locations/update", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<ExecutiveTaskExecutionDTO> updateLocation(@RequestParam String exeTaskPids) throws URISyntaxException {
		log.debug("Web request to Get Count of unable to find locations using executive Task Execution Pids: {}",
				exeTaskPids);

		String[] exePids = exeTaskPids.split(",");
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByExecutiveTaskExecutionPidIn(Arrays.asList(exePids));

		// update here
		executiveTaskExecutions = updateExecutiveTaskExecution(executiveTaskExecutions);

		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());

		return result;
	}

	private List<ExecutiveTaskExecution> updateExecutiveTaskExecution(
			List<ExecutiveTaskExecution> executiveTaskExecutions) {

		List<ExecutiveTaskExecution> saveExecutiveTaskExecutions = new ArrayList<>();
		if (!executiveTaskExecutions.isEmpty()) {
			for (ExecutiveTaskExecution execution : executiveTaskExecutions) {
				try {
					if (execution.getLocationType() == LocationType.GpsLocation) {
						String location = geoLocationService
								.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
						execution.setLocation(location);
					} else if (execution.getLocationType() == LocationType.TowerLocation) {
						TowerLocation location = geoLocationService.findAddressFromCellTower(execution.getMcc(),
								execution.getMnc(), execution.getCellId(), execution.getLac());
						execution.setLatitude(location.getLat());
						execution.setLongitude(location.getLan());
						execution.setLocation(location.getLocation());
					}
					if (!execution.getLocation().equalsIgnoreCase("Unable to find location")) {
						saveExecutiveTaskExecutions.add(execution);
					}
				} catch (GeoLocationServiceException ex) {
					log.error(ex.getMessage());
				}
			}
			if (!saveExecutiveTaskExecutions.isEmpty()) {
				executiveTaskExecutionRepository.save(saveExecutiveTaskExecutions);
			}
		}
		executiveTaskExecutions.removeAll(saveExecutiveTaskExecutions);
		return executiveTaskExecutions;
	}
}