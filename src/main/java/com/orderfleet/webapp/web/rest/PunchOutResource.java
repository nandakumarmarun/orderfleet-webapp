package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.PunchOutService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PunchOutDTO;

/**
 * Web controller for managing PunchOut.
 * 
 * @author Athul
 * @since March 27,2018
 */
@Controller
@RequestMapping("/web")
public class PunchOutResource {

	private final Logger log = LoggerFactory.getLogger(PunchOutResource.class);

	@Inject
	private PunchOutService punchOutService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	/**
	 * GET /punchOut-report : get punchOut list.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/punchOut-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getPunchOutReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of punchOut Report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/punchOutReport";
	}

	@RequestMapping(value = "/punchOut-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PunchOutDTO>> filterPunchOutReport(@RequestParam("employeePid") String employeePid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter executive task executions");

		List<PunchOutDTO> punchOutList = new ArrayList<PunchOutDTO>();
		if (filterBy.equals("TODAY")) {
			punchOutList = getFilterData(employeePid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			punchOutList = getFilterData(employeePid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			punchOutList = getFilterData(employeePid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			punchOutList = getFilterData(employeePid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			punchOutList = getFilterData(employeePid, fromDateTime, toDateTime);
		}

		return new ResponseEntity<>(punchOutList, HttpStatus.OK);
	}

	private List<PunchOutDTO> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate) {

		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<PunchOutDTO> punchOutList = new ArrayList<PunchOutDTO>();
		if (userPid.equals("no")) {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.isEmpty()) {
				punchOutList = punchOutService.findAllByCompanyIdAndDateBetween(fromDate, toDate);
			} else {
				punchOutList = punchOutService.findAllByCompanyIdUserPidInAndDateBetween(userIds, fromDate, toDate);
			}
		} else if (!userPid.equals("no")) {
			punchOutList = punchOutService.findAllByCompanyIdUserPidAndDateBetween(userPid, fromDate, toDate);
		}
		punchOutList.forEach(punchOutDTO -> {
			EmployeeProfileDTO employeeProfileDTO2 = employeeProfileService
					.findEmployeeProfileByUserLogin(punchOutDTO.getLogin());
			punchOutDTO.setEmployeeName(employeeProfileDTO2.getName());
		});
		return punchOutList;
	}
}
