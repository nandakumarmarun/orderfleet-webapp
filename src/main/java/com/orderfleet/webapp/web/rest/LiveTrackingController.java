package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileHistory;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.EmployeeHierarchyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LiveTrackingDTO;

/**
 * Web controller for managing Live tracking view.
 * 
 * @author Muhammed Riyas T
 * @since Sep 16, 2016
 */
@Controller
@RequestMapping("/web")
public class LiveTrackingController {

	private final Logger log = LoggerFactory.getLogger(LiveTrackingController.class);

	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private AttendanceService attendanceService;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private GeoLocationService geoLocationService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileRepository employeeRepository;

	@Inject
	private EmployeeHierarchyRepository employeeHierarchyRepository;

	@RequestMapping("/live-tracking")
	public String executives(Model model) {
		log.info("liveTracking.........................");
		model.addAttribute("companyId", SecurityUtils.getCurrentUsersCompanyId());
		return "company/liveTracking";
	}

	@RequestMapping(value = "/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();

			if (execution.getLatitude() != BigDecimal.ZERO) {
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);
				execution.setLocation(location);

			} else {
				System.out.println("-------No Location");
				execution.setLocation("No Location");
			}
			execution = executiveTaskExecutionRepository.save(execution);
			executionView = new InvoiceWiseReportView(execution);
		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}

	@RequestMapping(value = "/getUsersDetails", method = RequestMethod.GET)
	public @ResponseBody List<LiveTrackingDTO> getUsersDetails(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		// get user under current users

		List<EmployeeProfileDTO> employees;
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		employees = employeeProfileService.findAllEmployeeByUserIdsIn(userIds);

		List<LiveTrackingDTO> liveTrackingDTOs = new ArrayList<>();
		employees.forEach(employee -> {
			LiveTrackingDTO liveTrackingDTO = new LiveTrackingDTO();
			liveTrackingDTO.setEmployeeName(employee.getName());
			liveTrackingDTO.setUserPid(employee.getUserPid());
			liveTrackingDTO.setUserName(employee.getUserFirstName());

			List<ExecutiveTaskExecutionDTO> trackingPoints = new ArrayList<>();

			/*
			 * Optional<AttendanceDTO> attendance = attendanceService
			 * .findTop1(SecurityUtils.getCurrentUsersCompanyId(),employee.getUserPid());
			 * if(attendance.isPresent() &&
			 * attendance.get().getCreatedDate().toLocalDate().isEqual(LocalDate.now())) {
			 * ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new
			 * ExecutiveTaskExecutionDTO(); if(attendance.get().getTowerLatitude() != null){
			 * executiveTaskExecutionDTO.setTowerLatitude(attendance.get().getTowerLatitude(
			 * )); executiveTaskExecutionDTO.setTowerLongitude(attendance.get().
			 * getTowerLongitude()); }
			 * executiveTaskExecutionDTO.setLatitude(attendance.get().getLatitude());
			 * executiveTaskExecutionDTO.setLongitude(attendance.get().getLongitude());
			 * executiveTaskExecutionDTO.setLocation(attendance.get().getLocation());
			 * executiveTaskExecutionDTO.setTowerLocation(attendance.get().getTowerLocation(
			 * ));
			 * executiveTaskExecutionDTO.setLocationType(attendance.get().getLocationType())
			 * ; executiveTaskExecutionDTO.setDate(attendance.get().getCreatedDate());
			 * executiveTaskExecutionDTO.setAccountProfileName("Attendance");
			 * trackingPoints.add(executiveTaskExecutionDTO); }
			 */

			List<AttendanceDTO> attendanceList = attendanceService.findAllByCompanyIdUserPidAndDateBetween(
					employee.getUserPid(), date.atTime(0, 0), date.atTime(23, 59));
			if (attendanceList.size() > 0 && attendanceList.get(0).getCreatedDate().toLocalDate().isEqual(date)) {
				ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO();
				if (attendanceList.get(0).getTowerLatitude() != null) {
					executiveTaskExecutionDTO.setTowerLatitude(attendanceList.get(0).getTowerLatitude());
					executiveTaskExecutionDTO.setTowerLongitude(attendanceList.get(0).getTowerLongitude());
				}
				executiveTaskExecutionDTO.setLatitude(attendanceList.get(0).getLatitude());
				executiveTaskExecutionDTO.setLongitude(attendanceList.get(0).getLongitude());
				executiveTaskExecutionDTO.setLocation(attendanceList.get(0).getLocation());
				executiveTaskExecutionDTO.setTowerLocation(attendanceList.get(0).getTowerLocation());
				executiveTaskExecutionDTO.setLocationType(attendanceList.get(0).getLocationType());
				executiveTaskExecutionDTO.setDate(attendanceList.get(0).getCreatedDate());
				executiveTaskExecutionDTO.setAccountProfileName("Attendance");
				trackingPoints.add(executiveTaskExecutionDTO);
			}

			trackingPoints.addAll(executiveTaskExecutionService.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(
					employee.getUserPid(), date.atTime(0, 0), date.atTime(23, 59)));

			liveTrackingDTO.setTrackingPoints(trackingPoints);
			liveTrackingDTOs.add(liveTrackingDTO);
		});
		return liveTrackingDTOs;
	}

}
