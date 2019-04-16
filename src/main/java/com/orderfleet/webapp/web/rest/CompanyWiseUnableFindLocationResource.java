package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Web controller for managing Company Wise Unable Find Location.
 *
 * @author Sarath
 * @since Apr 17, 2018
 *
 */

@Controller
@RequestMapping("/web/company-wise-unable-find-location")
public class CompanyWiseUnableFindLocationResource {

	private final Logger log = LoggerFactory.getLogger(AccountingVoucherTransactionResource.class);

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private GeoLocationService geoLocationService;

	@Timed
	@RequestMapping(method = RequestMethod.GET)
	public String accountingVoucherTransaction(Model model) {
		log.debug("Web request to get acompany-wise-unable-find-location");
		return "company/companyWiseUnableFindLocation";
	}

	@RequestMapping(value = "/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<List<ExecutiveTaskExecutionDTO>> updateGetLocationOfExecution(
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterDataForLocation(LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterDataForLocation(yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterDataForLocation(weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterDataForLocation(monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterDataForLocation(fromDateTime, toFateTime);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterDataForLocation(fromDateTime, fromDateTime);
		}
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private List<ExecutiveTaskExecution> getFilterDataForLocation(LocalDate fDate, LocalDate tDate) {

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdAndDateBetweenOrderByDateDesc(fromDate, toDate);

		List<ExecutiveTaskExecution> result = executiveTaskExecutions.stream()
				.filter(execu -> execu.getLocation().equalsIgnoreCase("")
						|| execu.getLocation().equalsIgnoreCase("Unable to find location"))
				.collect(Collectors.toList());
		return result;
	}

	@RequestMapping(value = "/updateLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<List<ExecutiveTaskExecutionDTO>> updateAllLocationOfExecution(
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterDataForLocation(LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterDataForLocation(yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			executiveTaskExecutions = getFilterDataForLocation(weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			executiveTaskExecutions = getFilterDataForLocation(monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			executiveTaskExecutions = getFilterDataForLocation(fromDateTime, toFateTime);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterDataForLocation(fromDateTime, fromDateTime);
		}

		executiveTaskExecutions = updateExecutiveTaskExecution(executiveTaskExecutions);

		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
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
