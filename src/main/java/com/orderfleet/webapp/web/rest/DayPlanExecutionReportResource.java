package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;

/**
 * Web controller for managing DayPlanExecutionConfig.
 * 
 * @author Shaheer
 * @since May 05, 2017
 */
@Controller
@RequestMapping("/web")
public class DayPlanExecutionReportResource {

	private final Logger log = LoggerFactory.getLogger(DayPlanExecutionReportResource.class);

	private final ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	private final ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	private final EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserService userService;

	public DayPlanExecutionReportResource(ExecutiveTaskPlanRepository executiveTaskPlanRepository,
			ExecutiveTaskExecutionRepository executiveTaskExecutionRepository,
			EmployeeHierarchyService employeeHierarchyService) {
		super();
		this.executiveTaskPlanRepository = executiveTaskPlanRepository;
		this.executiveTaskExecutionRepository = executiveTaskExecutionRepository;
		this.employeeHierarchyService = employeeHierarchyService;
	}

	/**
	 * GET /day-plan-execution-report : get all the DayPlanExecutionReport.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/day-plan-execution-report", method = RequestMethod.GET)
	public String getAllDayPlanExecutionReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Day Plan Execution Report");
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/dayPlanExecutionReport";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/day-plan-execution-report/load", method = RequestMethod.GET)
	public ResponseEntity<List<ExecutiveTaskPlanDTO>> getExecutiveTaskPlan(
			@RequestParam("employeePid") String employeePid, @RequestParam String date) {
		log.debug("Web request to get Executive Task Plan");
//		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
//		if (!employeePid.equals("no")) {
//			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
//		}
//		String userPid = "no";
//		if (employeeProfileDTO.getPid() != null) {
//			userPid = employeeProfileDTO.getUserPid();
//		}

		List<String> userPids = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
			if (employeeProfileDTO.getPid() != null) {
				userPids.add(employeeProfileDTO.getUserPid());
			}
		} else {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.size() > 0) {
				List<UserDTO> users = userService.findByUserIdIn(userIds);
				for (UserDTO user : users) {
					userPids.add(user.getPid());
				}
			}
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate dateTime = LocalDate.parse(date, formatter);
//		List<ExecutiveTaskPlan> actualEcutiveTaskPlans = executiveTaskPlanRepository
//				.findByUserPidAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPid, dateTime.atTime(0, 0),
//						dateTime.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
		List<ExecutiveTaskPlan> actualEcutiveTaskPlans = executiveTaskPlanRepository
				.findByUserPidInAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPids, dateTime.atTime(0, 0),
						dateTime.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
		List<ExecutiveTaskPlanDTO> result = new ArrayList<>();
		if (!actualEcutiveTaskPlans.isEmpty()) {
			List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
					.findExecutiveTaskPlanByExecutiveTaskPlanIn(actualEcutiveTaskPlans);

			List<ExecutiveTaskPlan> convertedTaskPlans = actualEcutiveTaskPlans.stream().map(etp -> {
				int index = savedExecutedPlan.indexOf(etp);
				if (index != -1) {
					etp.setSortOrder(index + 1);
				}
				return etp;
			}).collect(Collectors.toList());
			result = convertedTaskPlans.stream().map(ExecutiveTaskPlanDTO::new).collect(Collectors.toList());
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
