package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceReportDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;

@Controller
@RequestMapping("/web")
public class AttendanceStatusSubgroupReportResource {
	
	private final Logger log = LoggerFactory.getLogger(AttendanceStatusSubgroupReportResource.class);
	@Inject
	private AttendanceService attendanceService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private AttendanceStatusSubgroupService attendanceStatusSubgroupService;
	
	@Inject
	private AttendanceStatusSubgroupRepository attendanceStatusSubgroupRepository;
	

	@RequestMapping(value = "/attendance-status-subgroup-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAttendanceStatusSubgroupReport(Model model) {
		model.addAttribute("subgroups", attendanceStatusSubgroupRepository.findAllByCompanyId());
		return "company/attendance-status-subgroup-report";
	}
	
	@RequestMapping(value = "/attendance-status-subgroup-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AttendanceReportDTO>> filterAttendanceReport(
			@RequestParam String attStatus,@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, 
			@RequestParam String toDate) {
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
		return new ResponseEntity<>(getFilterData(attStatus, fDate, tDate),
				HttpStatus.OK);
	}
	
	private List<AttendanceReportDTO> getFilterData(String attStatus, LocalDate fDate,
			LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		
		List<AttendanceDTO> attendanceList = attendanceService.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		List<AttendanceReportDTO> attendanceReportDtos = new ArrayList<>();
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyId(true);
		
		List<Long> userIdRequired = new ArrayList<>();
		userIdRequired.add(362120l);
		userIdRequired.add(362122l);
		userIdRequired.add(362121l);
		userIdRequired.add(330182l);
					log.info("Size :"+employeeProfiles.size());
					employeeProfiles.removeIf(emp -> !userIdRequired.contains(emp.getUser().getId()));
					log.info("Size After :"+employeeProfiles.size());
					
					for(AttendanceDTO attDto : attendanceList) {
						if(!attStatus.equals("all") ) {
							if(attDto.getAttendanceSubGroupId() != null) {
								if(!attDto.getAttendanceSubGroupId().toString().equals(attStatus)) {
									continue;
								}
							}
							else {
								continue;
							}
						}
						AttendanceReportDTO attendanceReportDTO = new AttendanceReportDTO();
					  List<EmployeeProfile> OPemployees = employeeProfiles.stream().filter(e -> e.getUser().getLogin().equals(attDto.getLogin())).collect(Collectors.toList());
						if(OPemployees.size() == 0) {
							continue;
						}
					  
					 	
						attendanceReportDTO.setEmployeeName(OPemployees.get(0).getName());
						attendanceReportDTO.setAttendanceStatus(attDto.getAttendanceStatus().toString());
						attendanceReportDTO.setPlannedDate(attDto.getPlannedDate());
						attendanceReportDTO.setCreatedDate(attDto.getCreatedDate());
						attendanceReportDTO.setCompleted(attDto.getIsCompleted());
						attendanceReportDTO.setRemarks(attDto.getRemarks());
						attendanceReportDTO.setLocation(attDto.getAttendanceSubGroupName());
						attendanceReportDTO.setAttendanceDay(attDto.getCreatedDate().toLocalDate());
						
						boolean status = true;
						for(AttendanceReportDTO attRepoDto :attendanceReportDtos) {
							if(attendanceReportDTO.getEmployeeName().equals(attRepoDto.getEmployeeName())) {
								if(attendanceReportDTO.getCreatedDate().toLocalDate().isEqual(attRepoDto.getCreatedDate().toLocalDate())) {
									if(attRepoDto.getLocation() == null) {
										attRepoDto.setLocation(attendanceReportDTO.getLocation());
									}
									status = false;
									break;
								}
							}
						}
						
						if(status) {
							attendanceReportDtos.add(attendanceReportDTO);
						}
					}

		return attendanceReportDtos;
	}
	
	private List<LocalDate> getDaysBetweenDates(LocalDate startDate, LocalDate endDate) {
		//the startDate is inclusive and endDate is exclusive in the calculation of noOfDaysBetween, so add one day
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
		return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());
	}
}
