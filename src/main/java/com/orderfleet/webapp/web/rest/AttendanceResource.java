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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.RootPlanDetailRepository;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * Web controller for managing Attendance.
 * 
 * @author Muhammed Riyas T
 * @since August 23, 2016
 */
@Controller
@RequestMapping("/web")
public class AttendanceResource {

	private final Logger log = LoggerFactory.getLogger(AttendanceResource.class);

	@Inject
	private AttendanceService attendanceService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private RootPlanDetailRepository rootPlanDetailRepository;
	
	@Inject
	private EmployeeProfileService employeeProfileService;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	/**
	 * GET /attendance-report : get attendance list.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/attendance-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAttendanceReport(Model model) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/attendance-report";
	}

	@RequestMapping(value = "/attendance-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AttendanceDTO>> filterAttendanceReport(@RequestParam("employeePid") String employeePid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate, @RequestParam boolean inclSubordinate) {
		log.debug("Web request to filter executive task executions");

		List<AttendanceDTO> attendanceList = new ArrayList<>();
		List<AttendanceDTO> filteredAttendanceDTOs = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			attendanceList = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			attendanceList = getFilterData(employeePid, yeasterday, yeasterday, inclSubordinate);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			attendanceList = getFilterData(employeePid, weekStartDate, LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			attendanceList = getFilterData(employeePid, monthStartDate, LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			attendanceList = getFilterData(employeePid, fromDateTime, toDateTime, inclSubordinate);
		}
		filteredAttendanceDTOs = removeDuplicate(attendanceList);
		// set task list alias from root plan
		for (AttendanceDTO attendanceDTO : filteredAttendanceDTOs) {
			List<RootPlanDetail> rootplanDetails = rootPlanDetailRepository
					.findAllByUserLoginAndApprovalStatusNotEqualOrderByRootOrder(attendanceDTO.getLogin(),
							ApprovalStatus.PENDING);
			
			rootplanDetails.stream().filter(rpd -> {
				if (rpd.getDownloadDate() != null && rpd.getDownloadDate().toLocalDate().equals(attendanceDTO.getCreatedDate().toLocalDate())) {
					return true;
				} else {
					return false;
				}
			}).findFirst().ifPresent(rpd -> attendanceDTO.setTaskListAlias(rpd.getTaskList().getAlias()));
		}

		return new ResponseEntity<>(filteredAttendanceDTOs, HttpStatus.OK);
	}

	private List<AttendanceDTO> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		
		List<Long> userIds = Collections.emptyList();
		if(employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		} else {
			if(inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds = Arrays.asList(opEmployee.get().getUser().getId());
				}
			}
		}
		List<AttendanceDTO> attendanceList;
		if(userIds.isEmpty()) {
			attendanceList = attendanceService.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		} else {
			attendanceList = attendanceService.findAllByCompanyIdUserPidInAndDateBetween(userIds, fromDate, toDate);
		}
		for (AttendanceDTO attendanceDTO : attendanceList) {
			EmployeeProfileDTO employeeProfileDTO2=employeeProfileService.findEmployeeProfileByUserLogin(attendanceDTO.getLogin());
			attendanceDTO.setEmployeeName(employeeProfileDTO2.getName());
		}
		return attendanceList;
	}

	/**
	 * removeing sameday multiple attendance of same user.
	 * 
	 * @param attendanceList
	 * @return
	 */
	private List<AttendanceDTO> removeDuplicate(List<AttendanceDTO> attendanceList) {
		List<AttendanceDTO> filteredAttendanceDTO = new ArrayList<>();
		Map<String, List<AttendanceDTO>> attendanceUserMap = attendanceList.parallelStream()
				.collect(Collectors.groupingBy(AttendanceDTO::getLogin));
		for (Entry<String, List<AttendanceDTO>> entry : attendanceUserMap.entrySet()) {
			Map<String, List<AttendanceDTO>> individulDayWiseMap = entry.getValue().parallelStream().collect(Collectors.groupingBy(a -> a.getPlannedDate().toLocalDate().toString()));
			individulDayWiseMap.forEach((k, v) -> filteredAttendanceDTO.add(v.stream().max(Comparator.comparing(AttendanceDTO::getPlannedDate)).get()));
		}
		Collections.sort(filteredAttendanceDTO, (p1, p2) -> p1.getPlannedDate().compareTo(p2.getPlannedDate()));
		return filteredAttendanceDTO;
	}
}
