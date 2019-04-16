package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.orderfleet.webapp.service.UserDistanceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.UserDistanceDTO;

/**
 * Web controller for managing UserDistance.
 *
 * @author Sarath
 * @since May 26, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class UserDistanceResource {

	private final Logger log = LoggerFactory.getLogger(UserDistanceResource.class);

	@Inject
	private UserDistanceService userDistanceService;

	@Inject
	private UserService userService;

	@RequestMapping(value = "/user-distance", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getUserDistances(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of User Distance");
		model.addAttribute("users", userService.findAllByCompany());
		return "company/userDistance";
	}

	@RequestMapping(value = "/user-distance/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserDistanceDTO>> filterExecutiveTaskExecutions(@RequestParam("userPid") String userPid,
			@RequestParam("filterBy") String filterBy,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.debug("Web request to filter user distances");

		List<UserDistanceDTO> userDistanceDTO = new ArrayList<UserDistanceDTO>();

		if (userPid.equalsIgnoreCase("no")) {
			List<String> userPids = userService.findAllByCompany().stream().map(a -> a.getPid())
					.collect(Collectors.toList());

			if (filterBy.equals("TODAY")) {
				userDistanceDTO = getFilterDataTodayYesterDayListUsers(userPids, LocalDate.now());
			} else if (filterBy.equals("YESTERDAY")) {
				LocalDate yeasterday = LocalDate.now().minusDays(1);
				userDistanceDTO = getFilterDataTodayYesterDayListUsers(userPids, yeasterday);
			} else if (filterBy.equals("WTD")) {
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
				userDistanceDTO = getFilterDataListUsers(userPids, weekStartDate, LocalDate.now());
			} else if (filterBy.equals("MTD")) {
				LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
				userDistanceDTO = getFilterDataListUsers(userPids, monthStartDate, LocalDate.now());
			} else if (filterBy.equals("CUSTOM")) {
				userDistanceDTO = getFilterDataListUsers(userPids, fromDate, toDate);
			}

		} else {

			if (filterBy.equals("TODAY")) {
				userDistanceDTO = getFilterDataTodayYesterDay(userPid, LocalDate.now());
			} else if (filterBy.equals("YESTERDAY")) {
				LocalDate yeasterday = LocalDate.now().minusDays(1);
				userDistanceDTO = getFilterDataTodayYesterDay(userPid, yeasterday);
			} else if (filterBy.equals("WTD")) {
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
				userDistanceDTO = getFilterData(userPid, weekStartDate, LocalDate.now());
			} else if (filterBy.equals("MTD")) {
				LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
				userDistanceDTO = getFilterData(userPid, monthStartDate, LocalDate.now());
			} else if (filterBy.equals("CUSTOM")) {
				userDistanceDTO = getFilterData(userPid, fromDate, toDate);
			}
		}
		return new ResponseEntity<>(userDistanceDTO, HttpStatus.OK);
	}

	private List<UserDistanceDTO> getFilterData(String userPid, LocalDate fromDate, LocalDate toDate) {
		log.debug("Web request to get Filterd Data user distances");
		List<UserDistanceDTO> result = userDistanceService
				.findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
		return result;
	}

	private List<UserDistanceDTO> getFilterDataTodayYesterDay(String userPid, LocalDate fromDate) {
		log.debug("Web request to get Filterd Data user distances");
		List<UserDistanceDTO> result = userDistanceService
				.findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(userPid, fromDate);
		return result;
	}

	private List<UserDistanceDTO> getFilterDataListUsers(List<String> userPids, LocalDate fromDate, LocalDate toDate) {
		log.debug("Web request to get Filterd Data user distances");
		List<UserDistanceDTO> result = userDistanceService
				.findAllByCompanyIdAndUserPidInAndDateBetweenOrderByCreatedDateDesc(userPids, fromDate, toDate);
		return result;
	}

	private List<UserDistanceDTO> getFilterDataTodayYesterDayListUsers(List<String> userPids, LocalDate fromDate) {
		log.debug("Web request to get Filterd Data user distances");
		List<UserDistanceDTO> result = userDistanceService
				.findAllByCompanyIdAndUserPidInAndDateOrderByCreatedDateDesc(userPids, fromDate);
		return result;
	}
}
