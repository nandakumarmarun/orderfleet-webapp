package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * get employees.
 * 
 * used to load employees in report pages(selectBox).
 *
 * @author Sarath
 * @since Dec 22, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class DashboardUserEmployeeHierarchyResource {

	private final Logger log = LoggerFactory.getLogger(DashboardUserEmployeeHierarchyResource.class);

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@GetMapping(value = "/get-hierarchical-employees", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<String, List<EmployeeProfileDTO>>> getHierarchicalEmployees() {
		log.debug("Web request to get hierarchical employees");
		Map<String, List<EmployeeProfileDTO>> mapEmployyes = new HashMap<>();
		List<EmployeeProfileDTO> otherEmployees = new ArrayList<>();
		List<EmployeeProfileDTO> dashBoardEmployees = new ArrayList<>();

		// get userUnderUserIds(manager Under UserIds)
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		// get employees from dashboard users
		List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
		List<Long> dashboardUsersIds = dashboardUsers.stream().map(a -> a.getId()).collect(Collectors.toList());

		if (userIds.isEmpty()) {
			otherEmployees.addAll(employeeProfileService.findAllByCompanyIdNotInUserIds(true, dashboardUsersIds));
			dashBoardEmployees = employeeProfileService.findAllEmployeeByUserIdsIn(dashboardUsersIds);
		} else {
			Set<Long> dashboardLastUsersIds = new HashSet<>();

			for (Long dashId : dashboardUsersIds) {
				for (Long uid : userIds) {
					if (dashId.equals(uid)) {
						dashboardLastUsersIds.add(dashId);
					}
				}
			}

			userIds.removeAll(dashboardUsersIds);
			otherEmployees.addAll(employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			dashBoardEmployees = employeeProfileService.findAllEmployeeByUserIdsIn(new ArrayList<>(dashboardLastUsersIds));
		}
		mapEmployyes.put("DASHBOARD", dashBoardEmployees);
		mapEmployyes.put("OTHERS", otherEmployees);
		return new ResponseEntity<>(mapEmployyes, HttpStatus.OK);
	}

}
