package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.UserTaskAssignmentRepository;
import com.orderfleet.webapp.repository.UserTaskGroupAssignmentRepository;
import com.orderfleet.webapp.repository.UserTaskListAssignmentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DayPlanDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * Web controller for managing DayPlan.
 * 
 * @author Muhammed Riyas T
 * @since November 12, 2016
 */
@Controller
@RequestMapping("/web")
public class DayPlanResource {

	private final Logger log = LoggerFactory.getLogger(DayPlanResource.class);

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private UserTaskAssignmentRepository userTaskAssignmentRepository;

	@Inject
	private UserTaskGroupAssignmentRepository userTaskGroupAssignmentRepository;

	@Inject
	private UserTaskListAssignmentRepository userTaskListAssignmentRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserService userService;

	/**
	 * GET /day-plans
	 *
	 * @param pageable the pagination information
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/day-plans", method = RequestMethod.GET)
	public String getDayPlans(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of day plans");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/dayPlans";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/day-plans/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DayPlanDTO>> filterDayPlans(@RequestParam("employeePid") String employeePid,
			@RequestParam String date) {
		log.debug("Web request to filter day plans");
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
		return new ResponseEntity<>(getPlans(userPids, dateTime), HttpStatus.OK);
	}

	private List<DayPlanDTO> getPlans(List<String> userPids, LocalDate date) {

//		List<ExecutiveTaskPlan> executiveTaskPlans = executiveTaskPlanRepository
//				.findByUserPidAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPid, date.atTime(0, 0),
//						date.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());

		List<ExecutiveTaskPlan> executiveTaskPlans = executiveTaskPlanRepository
				.findByUserPidInAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPids, date.atTime(0, 0),
						date.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());

		List<DayPlanDTO> dayPlans = new ArrayList<>();
		for (ExecutiveTaskPlan executiveTaskPlan : executiveTaskPlans) {
			String name = executiveTaskPlan.getAccountProfile().getName() + " - "
					+ executiveTaskPlan.getActivity().getName();
			String status = executiveTaskPlan.getTaskPlanStatus().name();
			String taskType = "";
			if (executiveTaskPlan.getTaskCreatedType().equals(TaskCreatedType.TASK_CLIENT)) {
				taskType = "Custom";
			} else if (executiveTaskPlan.getTaskCreatedType().equals(TaskCreatedType.TASK_SERVER)) {
				taskType = "Task Server";
			} else if (executiveTaskPlan.getTaskCreatedType().equals(TaskCreatedType.TASK_GROUP_SERVER)) {
				taskType = "Task Group Server";
			} else if (executiveTaskPlan.getTaskCreatedType().equals(TaskCreatedType.TASK_LIST_SERVER)) {
				taskType = "Task List Server";
			}
			dayPlans.add(new DayPlanDTO(name, taskType, status));
		}

		// find not in user task assignment
		// List<Task> userTaskAssignmentTasks =
		// userTaskAssignmentRepository.findTasksByUserPidAndStartDate(userPid, date);

		List<Task> userTaskAssignmentTasks = userTaskAssignmentRepository.findTasksByUserPidInAndStartDate(userPids,
				date);
		dayPlans = findNotInTaskFromUserTaskAssignments(executiveTaskPlans, userTaskAssignmentTasks, dayPlans);

		// find not in user task group assignment
//		List<TaskGroup> userTaskGroups = userTaskGroupAssignmentRepository.findTaskGroupsByUserPidAndStartDate(userPid,
//				date);
		List<TaskGroup> userTaskGroups = userTaskGroupAssignmentRepository
				.findTaskGroupsByUserPidInAndStartDate(userPids, date);
		dayPlans = findNotInTaskFromUserTaskGroupAssignments(executiveTaskPlans, userTaskGroups, dayPlans);

		// find not in user task list assignment
//		List<TaskList> userTaskLists = userTaskListAssignmentRepository.findTaskListsByUserPidAndStartDate(userPid,
//				date);
		List<TaskList> userTaskLists = userTaskListAssignmentRepository.findTaskListsByUserPidInAndStartDate(userPids,
				date);
		dayPlans = findNotInTaskFromUserTaskListAssignments(executiveTaskPlans, userTaskLists, dayPlans);

		// Sorting
		Collections.sort(dayPlans, new Comparator<DayPlanDTO>() {
			@Override
			public int compare(DayPlanDTO dayPlan1, DayPlanDTO dayPlan2) {
				return dayPlan1.getTaskType().compareTo(dayPlan2.getTaskType());
			}
		});

		return dayPlans;
	}

	private List<DayPlanDTO> findNotInTaskFromUserTaskAssignments(List<ExecutiveTaskPlan> executiveTaskPlans,
			List<Task> userTaskAssignmentTasks, List<DayPlanDTO> dayPlans) {
		for (Task task : userTaskAssignmentTasks) {
			boolean isNotIn = true;
			for (ExecutiveTaskPlan executiveTaskPlan : executiveTaskPlans) {
				if (executiveTaskPlan.getTask() != null) {
					if (task.getPid().equals(executiveTaskPlan.getTask().getPid())) {
						isNotIn = false;
						break;
					}
				}
			}
			if (isNotIn) {
				String name = task.getAccountProfile().getName() + " - " + task.getActivity().getName();
				String status = "Not In Plan";
				dayPlans.add(new DayPlanDTO(name, "Task Server", status));
			}
		}
		return dayPlans;
	}

	private List<DayPlanDTO> findNotInTaskFromUserTaskGroupAssignments(List<ExecutiveTaskPlan> executiveTaskPlans,
			List<TaskGroup> taskGroups, List<DayPlanDTO> dayPlans) {
		for (TaskGroup taskGroup : taskGroups) {
			for (Task task : taskGroup.getTasks()) {
				boolean isNotIn = true;
				for (ExecutiveTaskPlan executiveTaskPlan : executiveTaskPlans) {
					if (executiveTaskPlan.getTask() != null) {
						if (task.getPid().equals(executiveTaskPlan.getTask().getPid())) {
							isNotIn = false;
							break;
						}
					}
				}
				if (isNotIn) {
					String name = task.getAccountProfile().getName() + " - " + task.getActivity().getName();
					String status = "Not In Plan";
					dayPlans.add(new DayPlanDTO(name, taskGroup.getName() + " (Task Group Server)", status));
				}
			}
		}
		return dayPlans;
	}

	private List<DayPlanDTO> findNotInTaskFromUserTaskListAssignments(List<ExecutiveTaskPlan> executiveTaskPlans,
			List<TaskList> taskLists, List<DayPlanDTO> dayPlans) {
		for (TaskList taskList : taskLists) {
			for (Task task : taskList.getTasks()) {
				boolean isNotIn = true;
				for (ExecutiveTaskPlan executiveTaskPlan : executiveTaskPlans) {
					if (executiveTaskPlan.getTask() != null) {
						if (task.getPid().equals(executiveTaskPlan.getTask().getPid())) {
							isNotIn = false;
							break;
						}
					}
				}
				if (isNotIn) {
					String name = task.getAccountProfile().getName() + " - " + task.getActivity().getName();
					String status = "Not In Plan";
					dayPlans.add(new DayPlanDTO(name, taskList.getName() + " (Task List Server)", status));
				}
			}
		}
		return dayPlans;
	}

}
