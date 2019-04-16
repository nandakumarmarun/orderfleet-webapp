package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.service.AttendanceSubgroupApprovalRequestService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.AttendanceSubgroupApprovalRequestDTO;

@Controller
@RequestMapping("/web")
public class AttendanceSubgroupApprovalRequestResource {

	private final Logger log = LoggerFactory.getLogger(AttendanceSubgroupApprovalRequestResource.class);

	@Inject
	private UserService userService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private AttendanceSubgroupApprovalRequestService attendanceSubgroupApprovalRequestService;

	/**
	 * GET /attendance-subgroup-approval-request : get all the attendance
	 * Subgroup Approval Request.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         attendances in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/attendance-subgroup-approval-request", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAttendanceSubgroupApprovalRequest(Model model) throws URISyntaxException {
		log.debug("Web request to get page of attendance Subgroup Approval Request");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		return "company/attendanceSubgroupApproveRequest";
	}

	@RequestMapping(value = "/attendance-subgroup-approval-request/loadapprovalrequest", method = RequestMethod.GET)
	public @ResponseBody List<AttendanceSubgroupApprovalRequestDTO> getAttendanceSubgroupApprovalRequestsByUser(
			@RequestParam(value = "userPid") String userPid) {
		List<AttendanceSubgroupApprovalRequestDTO> attendanceSubgroupApprovalRequestDTOs = attendanceSubgroupApprovalRequestService
				.findAllByUsers(userPid);
		return attendanceSubgroupApprovalRequestDTOs;
	}

	@RequestMapping(value = "/attendance-subgroup-approval-request/approve", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> approveRequest(@RequestParam String message,
			@RequestParam Long attendanceSubgroupApprovalRequestId) {
		log.debug("REST request to approve root plan : {}", attendanceSubgroupApprovalRequestId);
		AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest = attendanceSubgroupApprovalRequestService
				.approveAttendanceRequest(attendanceSubgroupApprovalRequestId, message);
		attendanceSubgroupApprovalRequestService.sendNotification(attendanceSubgroupApprovalRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/attendance-subgroup-approval-request/reject", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> rejectRequest(@RequestParam String message,
			@RequestParam Long attendanceSubgroupApprovalRequestId) {
		log.debug("REST request to approve root plan : {}", attendanceSubgroupApprovalRequestId);
		attendanceSubgroupApprovalRequestService.rejectAttendanceRequest(attendanceSubgroupApprovalRequestId, message);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/attendance-subgroup-approval-request/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AttendanceSubgroupApprovalRequestDTO>> filterAttendanceSubgroupApprovalRequests(
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter Attendance Subgroup Approval Requests");
		List<AttendanceSubgroupApprovalRequestDTO> attendanceSubgroupApprovalRequestDTOs = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			attendanceSubgroupApprovalRequestDTOs = attendanceSubgroupApprovalRequestService
					.findAllByRequestedDateBetween(LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			attendanceSubgroupApprovalRequestDTOs = attendanceSubgroupApprovalRequestService
					.findAllByRequestedDateBetween(yesterday, yesterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			attendanceSubgroupApprovalRequestDTOs = attendanceSubgroupApprovalRequestService
					.findAllByRequestedDateBetween(weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			attendanceSubgroupApprovalRequestDTOs = attendanceSubgroupApprovalRequestService
					.findAllByRequestedDateBetween(monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			attendanceSubgroupApprovalRequestDTOs = attendanceSubgroupApprovalRequestService
					.findAllByRequestedDateBetween(fromDateTime, toDateTime);
		}
		return new ResponseEntity<List<AttendanceSubgroupApprovalRequestDTO>>(attendanceSubgroupApprovalRequestDTOs, HttpStatus.OK);
	}


}
