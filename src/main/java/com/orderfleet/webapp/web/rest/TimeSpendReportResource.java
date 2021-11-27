package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.PunchOut;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.PunchOutRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.TimeSpendReportView;

/**
 * Web controller for managing ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since July 12, 2016
 */
@Controller
@RequestMapping("/web")
public class TimeSpendReportResource {

	private final Logger log = LoggerFactory.getLogger(TimeSpendReportResource.class);

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PunchOutRepository punchOutRepository;

	@RequestMapping(value = "/time-spend-reports", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllExecutiveTaskExecutions(Pageable pageable, Model model) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/timeSpendReports";
	}

	@RequestMapping(value = "/time-spend-reports/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<TimeSpendReportView>> filterExecutiveTaskExecutions(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {

		List<TimeSpendReportView> executiveTaskExecutions = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			executiveTaskExecutions = getFilterData(employeePid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			executiveTaskExecutions = getFilterData(employeePid, yesterday, yesterday);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			executiveTaskExecutions = getFilterData(employeePid, fromDateTime, fromDateTime);
		}
		return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
	}

	private List<TimeSpendReportView> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate) {

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByPid(employeePid);

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		List<TimeSpendReportView> timeSpendReportViews = new ArrayList<>();

		if (employeeProfile != null) {
			String id="ATT_QUERY_121";
			String description="get the top 1 by companyPid ,userPid and planned date between and order by create date in desc ";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			Optional<Attendance> opAttendance = attendanceRepository
					.findTop1ByCompanyPidAndUserPidAndPlannedDateBetweenOrderByCreatedDateDesc(company.getPid(),
							employeeProfile.getUser().getPid(), fromDate, toDate);

			if (opAttendance.isPresent()) {

				TimeSpendReportView timeSpendReportView = new TimeSpendReportView();

				int sortOrder = 3;

				long hours = 00;
				long minutes = 00;
				long seconds = 00;

				List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(employeeProfile.getUser().getPid(),
								fromDate, toDate);

				for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
					timeSpendReportView = new TimeSpendReportView();

					timeSpendReportView.setSortOrder(sortOrder);
					timeSpendReportView.setCustomerName(executiveTaskExecution.getAccountProfile().getName());

					if (executiveTaskExecution.getWithCustomer()) {

						timeSpendReportView.setVisitType("Counter Visit");
					} else {
						timeSpendReportView.setVisitType("Remote Visit");
					}

					timeSpendReportView.setEmployeeName(employeeProfile.getName());
					if (executiveTaskExecution.getPunchInDate() != null) {
						timeSpendReportView
								.setPunchInTime(convertLocalDateTimetoString(executiveTaskExecution.getPunchInDate()));
					} else {
						timeSpendReportView.setPunchInTime("-");
					}

					timeSpendReportView.setEmployeeName(employeeProfile.getName());

					if (executiveTaskExecution.getSendDate() != null) {
						timeSpendReportView
								.setPunchOutTime(convertLocalDateTimetoString(executiveTaskExecution.getSendDate()));
					} else {
						timeSpendReportView.setPunchOutTime("-");
					}

					String timeSpend = "00 : 00 : 00";
					if (executiveTaskExecution.getPunchInDate() != null
							&& executiveTaskExecution.getSendDate() != null) {
						timeSpend = findTimeSpend(executiveTaskExecution.getPunchInDate(),
								executiveTaskExecution.getSendDate());
					}

					timeSpendReportView
							.setServerTime(convertLocalDateTimetoString(executiveTaskExecution.getCreatedDate()));

					timeSpendReportView.setTimeSpend(timeSpend);

					String[] timeSpendArray = timeSpend.split(" : ");

					hours += Long.parseLong(timeSpendArray[0]);

					minutes += Long.parseLong(timeSpendArray[1]);

					seconds += Long.parseLong(timeSpendArray[2]);

					timeSpendReportViews.add(timeSpendReportView);

					sortOrder++;

				}

				timeSpendReportView = new TimeSpendReportView();

				long hoursToSec = hours * 3600;
				long minToSec = minutes * 60;

				long totalSeconds = hoursToSec + minToSec + seconds;

				long conSeconds = totalSeconds % 60;
				long conHours = totalSeconds / 60;
				long conMinutes = conHours % 60;
				conHours = conHours / 60;

				timeSpendReportView.setSortOrder(1);
				timeSpendReportView.setCustomerName("-");
				timeSpendReportView.setVisitType("Attendance");
				timeSpendReportView.setEmployeeName(employeeProfile.getName());
				timeSpendReportView.setPunchInTime(convertLocalDateTimetoString(opAttendance.get().getPlannedDate()));
				timeSpendReportView.setPunchOutTime("-");
				timeSpendReportView.setServerTime(convertLocalDateTimetoString(opAttendance.get().getCreatedDate()));
				timeSpendReportView.setTimeSpend("<b>Customer Faceing Time :</b> <br><br> " + conHours + " : "
						+ conMinutes + " : " + conSeconds);

				timeSpendReportViews.add(timeSpendReportView);

				Optional<PunchOut> opPunchOut = punchOutRepository.findIsAttendancePresent(opAttendance.get().getPid());
				// findAllByCompanyIdUserPidAndPunchDateBetween(employeeProfile.getUser().getPid(),
				// fromDate, toDate);

				String timeSpend = "00 : 00 : 00";

				if (opPunchOut.isPresent()) {

					timeSpendReportView = new TimeSpendReportView();

					timeSpendReportView.setSortOrder(2);
					timeSpendReportView.setCustomerName("-");
					timeSpendReportView.setVisitType("Punch Out");
					timeSpendReportView.setEmployeeName(employeeProfile.getName());
					timeSpendReportView.setPunchInTime("-");
					timeSpendReportView
							.setPunchOutTime(convertLocalDateTimetoString(opPunchOut.get().getPunchOutDate()));
					timeSpendReportView.setServerTime(convertLocalDateTimetoString(opPunchOut.get().getCreatedDate()));

					timeSpend = findTimeSpend(opAttendance.get().getPlannedDate(), opPunchOut.get().getPunchOutDate());

					timeSpendReportView.setTimeSpend("<b>Total Login Hours :</b> <br><br>" + timeSpend);

					timeSpendReportViews.add(timeSpendReportView);
				}

			}

		}

		timeSpendReportViews.sort((

				TimeSpendReportView s1, TimeSpendReportView s2) -> s1.getSortOrder() - s2.getSortOrder());

		log.info("Time Spend Report Size :", timeSpendReportViews.size());
		return timeSpendReportViews;

	}

	private String convertLocalDateTimetoString(LocalDateTime localdatetime) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd YYYY, h:mm:ss a");

		String formatDateTime = localdatetime.format(formatter);

		return formatDateTime;
	}

	public String findTimeSpend(LocalDateTime startTime, LocalDateTime endTime) {
		long hours = 00;
		long minutes = 00;
		long seconds = 00;
		if (startTime != null && endTime != null) {
			long years = startTime.until(endTime, ChronoUnit.YEARS);
			startTime = startTime.plusYears(years);

			long months = startTime.until(endTime, ChronoUnit.MONTHS);
			startTime = startTime.plusMonths(months);

			long days = startTime.until(endTime, ChronoUnit.DAYS);
			startTime = startTime.plusDays(days);
			hours = startTime.until(endTime, ChronoUnit.HOURS);
			startTime = startTime.plusHours(hours);

			minutes = startTime.until(endTime, ChronoUnit.MINUTES);
			startTime = startTime.plusMinutes(minutes);

			seconds = startTime.until(endTime, ChronoUnit.SECONDS);
		}
		return hours + " : " + minutes + " : " + seconds;

	}

}
