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
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.RootPlanApprovalRequestService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.RootPlanApprovalRequestDTO;

@Controller
@RequestMapping("/web")
public class RootPlanApprovalRequestResource{
	private final Logger log = LoggerFactory.getLogger(RootPlanApprovalRequestResource.class);
	
	@Inject
	private RootPlanApprovalRequestService rootPlanApprovalRequestService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	/**
	 * GET /root-plan-approval-request : get all the root Plan Approval Request.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of root Plans in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/root-plan-approval-request", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllRootPlanApprovalRequest(Model model) throws URISyntaxException {
		log.debug("Web request to get page of root plan Approval Request");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("users", userService.findAllByCompany());
		} else {
			model.addAttribute("users", userService.findByUserIdIn(userIds));
		}
		return "company/rootPlanApprovalRequest";
	}
	
	@RequestMapping(value = "/root-plan-approval-request/loadapprovalrequest" , method = RequestMethod.GET)
	public @ResponseBody List<RootPlanApprovalRequestDTO> getRootPlanApprovalRequestsByUser(@RequestParam(value = "userPid") String userPid) {
		List<RootPlanApprovalRequestDTO>rootPlanApprovalRequestDTOs=rootPlanApprovalRequestService.findAllByUsers(userPid);
		return rootPlanApprovalRequestDTOs;
	}
	
	@RequestMapping(value = "/root-plan-approval-request/approve", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Boolean> approveRequest(@RequestParam String message, @RequestParam Long rootPlanApprovalRequestId) {
		log.debug("REST request to approve root plan : {}", rootPlanApprovalRequestId);
	boolean cond=	rootPlanApprovalRequestService.approveRootPlanRequest(rootPlanApprovalRequestId, message);
		return new ResponseEntity<>(cond,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/root-plan-approval-request/reject", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> rejectRequest(@RequestParam String message, @RequestParam Long rootPlanApprovalRequestId) {
		log.debug("REST request to approve root plan : {}", rootPlanApprovalRequestId);
		rootPlanApprovalRequestService.rejectRootPlanRequest(rootPlanApprovalRequestId, message);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@RequestMapping(value = "/root-plan-approval-request/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<RootPlanApprovalRequestDTO>> filterRootPlanApprovalRequests( @RequestParam("filterBy") String filterBy,
		@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter Root Plan Approval Requests");
		List<RootPlanApprovalRequestDTO> rootPlanApprovalRequestDTOs=new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			rootPlanApprovalRequestDTOs=rootPlanApprovalRequestService.findAllByRequestedDateBetween(LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			rootPlanApprovalRequestDTOs=rootPlanApprovalRequestService.findAllByRequestedDateBetween(yesterday, yesterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			rootPlanApprovalRequestDTOs=rootPlanApprovalRequestService.findAllByRequestedDateBetween(weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			rootPlanApprovalRequestDTOs=rootPlanApprovalRequestService.findAllByRequestedDateBetween(monthStartDate, LocalDate.now());
		} 
		else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			rootPlanApprovalRequestDTOs=rootPlanApprovalRequestService.findAllByRequestedDateBetween(fromDateTime, toDateTime);
		}
		return new ResponseEntity<List<RootPlanApprovalRequestDTO>>(rootPlanApprovalRequestDTOs, HttpStatus.OK);
		
	}
	
	
}
