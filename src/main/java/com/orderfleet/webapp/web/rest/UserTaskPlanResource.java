package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

/**
 * REST controller for managing ExecutiveTaskPlan.
 *
 * @author Shaheer
 * @since January 02, 2017
 */
@Controller
@RequestMapping(value = "/web")
public class UserTaskPlanResource {

	private final UserService userService;

	private final ExecutiveTaskPlanService executiveTaskPlanService;

	private final TaskRepository taskRepository;

	private final TaskListService taskListService;

	private final ActivityService activityService;

	private final LocationService locationService;

	private final EmployeeProfileLocationRepository employeeProfileLocationRepository;

	private final LocationAccountProfileRepository locationAccountProfileRepository;
	
	private final UserActivityRepository userActivityRepository;
	
	public UserTaskPlanResource(UserService userService, ExecutiveTaskPlanService executiveTaskPlanService,
			TaskRepository taskRepository, TaskListService taskListService, ActivityService activityService,
			LocationService locationService, EmployeeProfileLocationRepository employeeProfileLocationRepository,
			LocationAccountProfileRepository locationAccountProfileRepository,UserActivityRepository userActivityRepository) {
		super();
		this.userService = userService;
		this.executiveTaskPlanService = executiveTaskPlanService;
		this.taskRepository = taskRepository;
		this.taskListService = taskListService;
		this.activityService = activityService;
		this.locationService = locationService;
		this.employeeProfileLocationRepository = employeeProfileLocationRepository;
		this.locationAccountProfileRepository = locationAccountProfileRepository;
		this.userActivityRepository = userActivityRepository;
	}

	/**
	 * GET /user-task-plan : get user-task-plan page.
	 */
	@GetMapping("/user-task-plan")
	public String getUserTaskPlanPage(Model model) {
		model.addAttribute("users", userService.findAllByCompany());
		model.addAttribute("taskList", taskListService.findAllByCompany());
		model.addAttribute("activities", activityService.findAllByCompany());
		model.addAttribute("locations", locationService.findAllByCompany());
		List<Object[]> tasks = taskRepository.findTaskPropertyByCompanyId();
		List<TaskDTO> taskDtos = new ArrayList<>();
		for (Object[] taskObj : tasks) {
			TaskDTO taskDto = new TaskDTO();
			taskDto.setPid(taskObj[0].toString());
			taskDto.setActivityPid(taskObj[1].toString());
			taskDto.setActivityName(taskObj[2].toString());
			taskDto.setAccountProfilePid(taskObj[3].toString());
			taskDto.setAccountProfileName(taskObj[4].toString());
			taskDto.setRemarks(taskObj[5].toString());
			
			taskDtos.add(taskDto);
		}
		model.addAttribute("tasks", taskDtos);
		return "company/user-task-plan";
	}

	@GetMapping(value = "/user-tasks", params = { "userPid", "plannedDate" })
	public ResponseEntity<List<ExecutiveTaskPlanDTO>> getUserTasks(@RequestParam(value = "userPid") String userPid,
			@RequestParam(value = "plannedDate") LocalDate plannedDate) {
		return new ResponseEntity<>(executiveTaskPlanService.findByUserPidAndPlannedDate(userPid, plannedDate),
				HttpStatus.OK);
	}

	@GetMapping(value = "/downloaded-user-tasks", params = { "userPid", "plannedDate" })
	public ResponseEntity<List<ExecutiveTaskPlanDTO>> getDownloadedUserTasks(
			@RequestParam(value = "userPid") String userPid,
			@RequestParam(value = "plannedDate") LocalDate plannedDate) {
		return new ResponseEntity<>(executiveTaskPlanService.findByUserPidAndPlannedDateAndNotTaskPlanStatus(userPid,
				plannedDate, TaskPlanStatus.CREATED), HttpStatus.OK);
	}

	/**
	 * POST /user-task-plan : Create a new executiveTaskPlan.
	 *
	 * @param executiveTaskPlan
	 *            the executiveTaskPlanDTO to create
	 * @return the ResponseEntity with status 200
	 */
	@Timed
	@RequestMapping(value = "/user-task-plan/{userPid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> createUserTaskPlan(@PathVariable String userPid,
			@RequestBody List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs) {
		List<String> notAssignedAccountsOrActivities = new ArrayList<>();
		if (userPid.isEmpty() || "-1".equals(userPid)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		if (!executiveTaskPlanDTOs.isEmpty()) {
			List<ExecutiveTaskPlanDTO> userHaveAxecutiveTaskPlanDTOs = new ArrayList<>();
			List<Location> userLocations = employeeProfileLocationRepository.findLocationsByUserPid(userPid);
			if (userLocations != null) {
				//user activities
				List<UserActivity> userActivities = userActivityRepository.findByUserPid(userPid);
				//user account profile
				List<AccountProfile> accountProfiles = locationAccountProfileRepository
						.findAccountProfilesByUserLocationsAndAccountProfileActivated(userLocations);
				for (ExecutiveTaskPlanDTO etpDto : executiveTaskPlanDTOs) {
					Optional<AccountProfile> optionalAP = accountProfiles.stream()
							.filter(pl -> etpDto.getAccountProfilePid().equals(pl.getPid())).findAny();
					Optional<UserActivity> optionalUserActivities = userActivities.stream()
							.filter(ua -> etpDto.getActivityPid().equals(ua.getActivity().getPid())).findAny();
					if(!optionalUserActivities.isPresent()) {
						notAssignedAccountsOrActivities.add(etpDto.getActivityName());
					}
					if (optionalAP.isPresent()) {
						userHaveAxecutiveTaskPlanDTOs.add(etpDto);
					} else {
						notAssignedAccountsOrActivities.add(etpDto.getAccountProfileName());
					}
				}
			}
			if (notAssignedAccountsOrActivities.isEmpty()) {
				executiveTaskPlanService.save(userPid, userHaveAxecutiveTaskPlanDTOs,
						executiveTaskPlanDTOs.get(0).getPlannedDate().toLocalDate());
			}
		}
		return new ResponseEntity<>(notAssignedAccountsOrActivities, HttpStatus.OK);
	}

}
