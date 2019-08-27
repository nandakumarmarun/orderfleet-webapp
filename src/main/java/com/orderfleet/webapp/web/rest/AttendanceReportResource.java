package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import java.util.stream.IntStream;

import javax.inject.Inject;

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
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceReportDTO;

@Controller
@RequestMapping("/web")
public class AttendanceReportResource {

	@Inject
	private AttendanceService attendanceService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * GET /attendance-report : get attendance list.
	 *
	 */
	@RequestMapping(value = "/attendance-report/all", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAttendanceReport(Model model) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/attendance-report-all";
	}

	@RequestMapping(value = "/attendance-report/all/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AttendanceReportDTO>> filterAttendanceReport(
			@RequestParam("employeePid") String employeePid, @RequestParam String attStatus,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam boolean inclSubordinate) {
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		if (filterBy.equals("TODAY")) {

		} else if (filterBy.equals("YESTERDAY")) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
			tDate = LocalDate.now();
		} else if (filterBy.equals("MTD")) {
			fDate = LocalDate.now().withDayOfMonth(1);
			tDate = LocalDate.now();
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		}
		return new ResponseEntity<>(getFilterData(employeePid, attStatus, fDate, tDate, inclSubordinate),
				HttpStatus.OK);
	}

	private List<AttendanceReportDTO> getFilterData(String employeePid, String attStatus, LocalDate fDate,
			LocalDate tDate, boolean inclSubordinate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = getUserIdsUnderEmployee(employeePid, inclSubordinate);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findByUserIdInAndActivated(userIds, true);
		List<AttendanceDTO> attendanceList = attendanceService.findAllByCompanyIdUserPidInAndDateBetween(userIds,
				fromDate, toDate);
		List<LocalDate> dates = getDaysBetweenDates(fromDate.toLocalDate(), toDate.toLocalDate());
		List<AttendanceReportDTO> attendanceReportDtos = new ArrayList<>();
		for (LocalDate localDate : dates) {
			for (EmployeeProfile employee : employeeProfiles) {
				Optional<AttendanceDTO> attendanceExists = attendanceList.stream()
						.filter(a -> a.getLogin().equals(employee.getUser().getLogin())
								&& localDate.equals(a.getPlannedDate().toLocalDate()))
						.findAny();
				AttendanceReportDTO attendanceReportDTO = new AttendanceReportDTO();
				attendanceReportDTO.setAttendanceDay(localDate);
				if (attendanceExists.isPresent()) {
					AttendanceDTO attDto = attendanceExists.get();
					attendanceReportDTO.setUserPid(attDto.getUserPid());
					attendanceReportDTO.setEmployeeName(employee.getName());
					attendanceReportDTO.setAttendanceStatus(attDto.getAttendanceStatus().toString());
					attendanceReportDTO.setPlannedDate(attDto.getPlannedDate());
					attendanceReportDTO.setCreatedDate(attDto.getCreatedDate());
					attendanceReportDTO.setCompleted(attDto.getIsCompleted());
					attendanceReportDTO.setRemarks(attDto.getRemarks());
					attendanceReportDTO.setLocation(attDto.getLocation());
					attendanceReportDTO.setTowerLocation(attDto.getTowerLocation());
					attendanceReportDTO.setAttendanceSubGroupName(attDto.getAttendanceSubGroupName());
				} else {
					attendanceReportDTO.setEmployeeName(employee.getName());
					attendanceReportDTO.setAttendanceStatus("NOT MARKED");
					attendanceReportDTO.setCompleted(false);
					attendanceReportDTO.setRemarks("");
					attendanceReportDTO.setLocation("");
				}
				attendanceReportDtos.add(attendanceReportDTO);
			}
		}
		if (!"ALL".equals(attStatus)) {
			filterByAttendanceStatus(attStatus, attendanceReportDtos);
		}
		return attendanceReportDtos;
	}

	private List<Long> getUserIdsUnderEmployee(String employeePid, boolean inclSubordinate) {
		List<Long> userIds = Collections.emptyList();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
				Set<Long> uniqueIds = new HashSet<>();
				if (!dashboardUserIds.isEmpty()) {
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid.equals(sid)) {
									uniqueIds.add(sid);
								}
							}
						}
					} else {
						userIds = new ArrayList<>(dashboardUserIds);
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}
			} else {
				if (userIds.isEmpty()) {
					List<User> users = userRepository.findAllByCompanyId();
					userIds = users.stream().map(User::getId).collect(Collectors.toList());
				}
			}
		} else {
			if (inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds = Arrays.asList(opEmployee.get().getUser().getId());
				}
			}
		}
		return userIds;
	}

	private void filterByAttendanceStatus(String attStatus, List<AttendanceReportDTO> attendanceReportDtos) {
		if ("PRESENT".equals(attStatus)) {
			attendanceReportDtos.removeIf(a -> !"PRESENT".equals(a.getAttendanceStatus()));
		} else if ("LEAVE".equals(attStatus)) {
			attendanceReportDtos.removeIf(a -> !"LEAVE".equals(a.getAttendanceStatus()));
		} else if ("NOT_MARKED".equals(attStatus)) {
			attendanceReportDtos.removeIf(a -> !"NOT MARKED".equals(a.getAttendanceStatus()));
		}
	}

	private List<LocalDate> getDaysBetweenDates(LocalDate startDate, LocalDate endDate) {
		// the startDate is inclusive and endDate is exclusive in the calculation of
		// noOfDaysBetween, so add one day
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
		return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());
	}

}
