package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.time.Duration;
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

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.web.rest.dto.*;
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

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.Files;
import com.orderfleet.webapp.domain.enums.AttendanceStatus;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.PunchOutService;

@Controller
@RequestMapping("/web")
public class LoginLogoutReportResource {

	private final Logger log = LoggerFactory.getLogger(AttendanceResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AttendanceService attendanceService;
	@Inject
	private PunchOutService punchoutService;

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

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private FileManagerService fileManagerService;
	
	@Inject 
    private	PunchOutRepository punchOutRepository;

    @Inject
    private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	/**
	 * GET /attendance-report : get attendance list.
	 *
	 */
	@RequestMapping(value = "/loginlogout-report/all", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getLoginLogOutReport(Model model) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/loginlogout-report-all";
	}

	@RequestMapping(value = "/loginlogout-report/all/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LoginLogoutDTO>> filterLoginLogOutReport(@RequestParam("employeePid") String employeePid,
			@RequestParam String attStatus, @RequestParam("filterBy") String filterBy, @RequestParam String fromDate,
			@RequestParam String toDate, @RequestParam boolean inclSubordinate) {
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
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		}
		return new ResponseEntity<>(getFilterData(employeePid, attStatus, fDate, tDate, inclSubordinate),
				HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/loginlogout-report/all/images/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFileDTO>> getAttendanceImages(@PathVariable String pid) {
		log.debug("Web request to get Attendance images by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_122" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<Attendance> optionalAttendanceDTO = attendanceRepository.findOneByPid(pid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		Attendance attendance = new Attendance();

		List<FormFileDTO> formFileDTOs = new ArrayList<>();
		if (optionalAttendanceDTO.isPresent()) {
			attendance = optionalAttendanceDTO.get();
		}
		if (attendance.getFiles().size() > 0) {
			FormFileDTO formFileDTO = new FormFileDTO();
			String formName = attendance.getUser().getFirstName();
			Optional<EmployeeProfile> opEmployee = employeeProfileRepository
					.findByUserPid(attendance.getUser().getPid());
			if (opEmployee.isPresent()) {
				formName = formName + " " + opEmployee.get().getName();
			}
			formFileDTO.setFormName(formName);
			formFileDTO.setFiles(new ArrayList<>());
			Set<File> files = attendance.getFiles();
			for (File file : files) {
				FileDTO fileDTO = new FileDTO();
				fileDTO.setFileName(file.getFileName());
				fileDTO.setMimeType(file.getMimeType());
				java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
				if (physicalFile.exists()) {
					try {
						fileDTO.setContent(Files.toByteArray(physicalFile));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				formFileDTO.getFiles().add(fileDTO);
			}
			formFileDTOs.add(formFileDTO);
		}
		return new ResponseEntity<>(formFileDTOs, HttpStatus.OK);

	}
	
	@Timed
	@RequestMapping(value = "/loginlogout-report/all/PunchOutImages/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFileDTO>> getPunchOutImages(@PathVariable String pid) {
		log.debug("Web request to get Attendance images by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_122" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<PunchOut> optPunchOut = punchOutRepository.findOneByPid(pid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		PunchOut punchOut = new PunchOut();

		List<FormFileDTO> formFileDTOs = new ArrayList<>();
		if (optPunchOut.isPresent()) {
			punchOut = optPunchOut.get();
		}
		if (punchOut.getFiles().size() > 0) {
			FormFileDTO formFileDTO = new FormFileDTO();
			String formName = punchOut.getUser().getFirstName();
			Optional<EmployeeProfile> opEmployee = employeeProfileRepository
					.findByUserPid(punchOut.getUser().getPid());
			if (opEmployee.isPresent()) {
				formName = formName + " " + opEmployee.get().getName();
			}
			formFileDTO.setFormName(formName);
			formFileDTO.setFiles(new ArrayList<>());
			Set<File> files = punchOut.getFiles();
			for (File file : files) {
				FileDTO fileDTO = new FileDTO();
				fileDTO.setFileName(file.getFileName());
				fileDTO.setMimeType(file.getMimeType());
				java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
				if (physicalFile.exists()) {
					try {
						fileDTO.setContent(Files.toByteArray(physicalFile));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				formFileDTO.getFiles().add(fileDTO);
			}
			formFileDTOs.add(formFileDTO);
		}
		return new ResponseEntity<>(formFileDTOs, HttpStatus.OK);

	}

	private List<LoginLogoutDTO> getFilterData(String employeePid, String attStatus, LocalDate fDate, LocalDate tDate,
			boolean inclSubordinate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = getUserIdsUnderEmployee(employeePid, inclSubordinate);

		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findByUserIdInAndActivated(userIds, true);
		List<AttendanceDTO> attendanceList = attendanceService.findAllByCompanyIdUserPidInAndDateBetween(userIds,
				fromDate, toDate);
		List<PunchOutDTO> punchoutList = punchoutService.findAllByCompanyIdUserPidInAndPunchDateBetween(userIds,
				fromDate, toDate);
        List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository.findAllByCompanyIdUserIdInAndDateBetween(userIds,
				fromDate, toDate);

		List<LocalDate> dates = getDaysBetweenDates(fromDate.toLocalDate(), toDate.toLocalDate());
		List<LoginLogoutDTO> loginlogoutDtos = new ArrayList<>();
		for (LocalDate localDate : dates) {
			for (EmployeeProfile employee : employeeProfiles) {
				Optional<AttendanceDTO> attendanceExists = attendanceList.stream()
						.filter(a -> a.getLogin().equals(employee.getUser().getLogin())
								&& localDate.equals(a.getPlannedDate().toLocalDate()))
						.findAny();
				Optional<PunchOutDTO> punchExists = punchoutList.stream()
						.filter(a -> a.getLogin().equals(employee.getUser().getLogin())
								&& localDate.equals(a.getPunchOutDate().toLocalDate()))
						.findAny();
				List<ExecutiveTaskExecution> filteredExecutions = executiveTaskExecutions
						.stream()
						.filter(execution -> execution.getUser().getId().equals(employee.getUser().getId()))
						.collect(Collectors.toList())
						.stream()
						.filter(execution ->localDate.equals(execution.getCreatedDate().toLocalDate()))
						.collect(Collectors.toList());


				LoginLogoutDTO loginlogoutDTO = new LoginLogoutDTO();
				loginlogoutDTO.setAttendanceDay(localDate);

				if (attendanceExists.isPresent()) {

					AttendanceDTO attDto = attendanceExists.get();
					loginlogoutDTO.setAttendancePid(attDto.getPid());
					loginlogoutDTO.setEmployeeName(employee.getName());
					loginlogoutDTO.setAttendanceStatus(attDto.getAttendanceStatus().toString());
					loginlogoutDTO.setPlannedDate(attDto.getPlannedDate());
					loginlogoutDTO.setCreatedDate(attDto.getCreatedDate());
					loginlogoutDTO.setAttendaceOdooMeter(attDto.getOodoMeter());
					loginlogoutDTO.setVehicleType(attDto.getVehicleType());					
					loginlogoutDTO.setRemarks(attDto.getRemarks());
					log.debug(" imagereffrencenumber ATT : " + attDto.getImageRefNo());
					if(attDto.getImageRefNo() != null && !attDto.getImageRefNo().isEmpty()){
						loginlogoutDTO.setImageButtonVisibleAtt(true);
					}
				} else {
					loginlogoutDTO.setEmployeeName(employee.getName());
					loginlogoutDTO.setAttendanceStatus("NOT MARKED");
					loginlogoutDTO.setRemarks("");

				}

				if (punchExists.isPresent()) {
					PunchOutDTO pnchDto = punchExists.get();
					loginlogoutDTO.setPunchoutPid(pnchDto.getPid());
					loginlogoutDTO.setPunchoutClientDate(pnchDto.getCreatedDate());
					loginlogoutDTO.setPunchoutServerDate(pnchDto.getPunchOutDate());
					loginlogoutDTO.setPunchoutRemarks(pnchDto.getPunchOutRemarks());
					loginlogoutDTO.setPunchoutOdoMeter(pnchDto.getOodoMeter());
					if(attendanceExists.isPresent()) {
					loginlogoutDTO.setTotalOdoMeter(pnchDto.getOodoMeter() - attendanceExists.get().getOodoMeter());
					}
					loginlogoutDTO.setPunchoutStatus("MARKED");
					log.debug(" imagereffrencenumber Pun : " + pnchDto.getImageRefNo());
					if(pnchDto.getImageRefNo() != null && !pnchDto.getImageRefNo().isEmpty() ){
						loginlogoutDTO.setImageButtonVisiblePun(true);
					}
				} else {
					loginlogoutDTO.setPunchoutStatus("NOT MARKED");
					loginlogoutDTO.setPunchoutRemarks("");
				}
				if(attendanceExists.isPresent()) {
					loginlogoutDTO.setMockLocationStatus(attendanceExists.get().getMockLocationStatus());
				}
				loginlogoutDTO.setNoOfVisits(filteredExecutions.size());
				loginlogoutDtos.add(loginlogoutDTO);
			}
		}
		if (!"ALL".equals(attStatus)) {
			filterByAttendanceStatus(attStatus, loginlogoutDtos);
		}
		return loginlogoutDtos;
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

	private void filterByAttendanceStatus(String attStatus, List<LoginLogoutDTO> loginlogoutDtos) {
		if ("PRESENT".equals(attStatus)) {
			loginlogoutDtos.removeIf(a -> !"PRESENT".equals(a.getAttendanceStatus()));
		} else if ("LEAVE".equals(attStatus)) {
			loginlogoutDtos.removeIf(a -> !"LEAVE".equals(a.getAttendanceStatus()));
		} else if ("NOT_MARKED".equals(attStatus)) {
			loginlogoutDtos.removeIf(a -> !"NOT MARKED".equals(a.getAttendanceStatus()));
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
