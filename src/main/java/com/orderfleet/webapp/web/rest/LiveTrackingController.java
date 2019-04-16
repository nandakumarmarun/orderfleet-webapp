package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
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


	@RequestMapping("/live-tracking")
	public String executives(Model model) {
		log.info("liveTracking.........................");
		model.addAttribute("companyId", SecurityUtils.getCurrentUsersCompanyId());
		return "company/liveTracking";
	}

	@RequestMapping(value = "/getUsersDetails", method = RequestMethod.GET)
	public @ResponseBody List<LiveTrackingDTO> getUsersDetails(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		//get user under current users
		List<EmployeeProfileDTO> dashboardEmployees;
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		log.info("No: subordinate users :"+userIds);
		if(userIds.isEmpty()) {
			dashboardEmployees = employeeProfileService.findAllByCompany();
		} else {
			dashboardEmployees = employeeProfileService.findAllEmployeeByUserIdsIn(userIds);
		}
		List<LiveTrackingDTO> liveTrackingDTOs = new ArrayList<>();
		dashboardEmployees.forEach(employee -> {
			LiveTrackingDTO liveTrackingDTO = new LiveTrackingDTO();
			liveTrackingDTO.setEmployeeName(employee.getName());
			liveTrackingDTO.setUserPid(employee.getUserPid());
			liveTrackingDTO.setUserName(employee.getUserFirstName());
			
			List<ExecutiveTaskExecutionDTO> trackingPoints = new ArrayList<>();
			Optional<AttendanceDTO> attendance = attendanceService
					.findTop1(SecurityUtils.getCurrentUsersCompanyId(),employee.getUserPid());
			if(attendance.isPresent() && attendance.get().getCreatedDate().toLocalDate().isEqual(LocalDate.now())) {
				ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO();
				if(attendance.get().getTowerLatitude() != null){
					executiveTaskExecutionDTO.setTowerLatitude(attendance.get().getTowerLatitude());
					executiveTaskExecutionDTO.setTowerLongitude(attendance.get().getTowerLongitude());
				}
				executiveTaskExecutionDTO.setLatitude(attendance.get().getLatitude());
				executiveTaskExecutionDTO.setLongitude(attendance.get().getLongitude());
				executiveTaskExecutionDTO.setLocation(attendance.get().getLocation());
				executiveTaskExecutionDTO.setTowerLocation(attendance.get().getTowerLocation());
				executiveTaskExecutionDTO.setLocationType(attendance.get().getLocationType());
				executiveTaskExecutionDTO.setDate(attendance.get().getCreatedDate());
				executiveTaskExecutionDTO.setAccountProfileName("Attendance");
				trackingPoints.add(executiveTaskExecutionDTO);
			}
			trackingPoints.addAll(executiveTaskExecutionService
				.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(employee.getUserPid(), date.atTime(0, 0),
					date.atTime(23, 59)));
			
			liveTrackingDTO.setTrackingPoints(trackingPoints);
			liveTrackingDTOs.add(liveTrackingDTO);
		});
		return liveTrackingDTOs;
	}

}
