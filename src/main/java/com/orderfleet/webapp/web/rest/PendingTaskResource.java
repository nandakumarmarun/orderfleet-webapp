package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PendingTaskView;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TaskPlan;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class PendingTaskResource {

	private final Logger log = LoggerFactory.getLogger(PendingTaskResource.class);

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private ExecutiveTaskPlanService executiveTaskPlanService;

	@RequestMapping(value = "/pending-taskList", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllExecutiveTaskPlans(Model model) {

		return "company/pendingTask";
	}

	@RequestMapping(value = "/pending-taskList/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<PendingTaskView>> checkUnattendedTask()

	{
		log.info("web request to get the List pending Task");
		List<ExecutiveTaskPlan> taskPlans = null;

		Long companyid = SecurityUtils.getCurrentUsersCompanyId();
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
		taskPlans = executiveTaskPlanRepository.findAllByCompanyIdAndTaskPlanStatusAndActivatedAndPlannedDateBetween(companyid,
				TaskPlanStatus.PENDING, true,weekStartDate.atTime(0,0),LocalDateTime.now());

		System.out.println("Size of TaskPlan :" + taskPlans.size());

		Map<User, List<ExecutiveTaskPlan>> plansGroupedByUser = null;

		List<PendingTaskView> pendingTaskView = new ArrayList<PendingTaskView>();
		if (taskPlans.size() > 0) {

			plansGroupedByUser = taskPlans.stream().collect(Collectors.groupingBy(etp -> etp.getUser()));

			for (Entry<User, List<ExecutiveTaskPlan>> entry : plansGroupedByUser.entrySet()) {
				
				PendingTaskView pendingTask = new PendingTaskView();

				List<TaskPlan> taskList = new ArrayList<>();

				User user = entry.getKey();
				pendingTask.setUserLogin(user.getLogin());
				List<ExecutiveTaskPlan> tasks = entry.getValue();
				tasks.forEach(data -> System.out.println(data.getTask().getAccountProfile().getName()));
				tasks.forEach(data -> {
					TaskPlan task = new TaskPlan();
					task.setAccountProfile(data.getTask().getAccountProfile().getName());
					task.setAccountType(data.getTask().getAccountType().getName());
					task.setActivity(data.getTask().getActivity().getName());
					task.setDate(data.getPlannedDate());
					task.setTaskPid(data.getTask().getPid());
					task.setUserPid(data.getUser().getPid());
					task.setPid(data.getPid());
					task.setActivated(true);
					taskList.add(task);
					pendingTask.setPid(data.getPid());
				});

				pendingTask.setUnattendedTask(entry.getValue().size());
				EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin());
				pendingTask.setEmpName(employee.getName());
				List<Location> location = employeeProfileLocationRepository.findLocationsByUserPid(user.getPid());
				for (Location loc : location) {
					pendingTask.setTerritory(loc.getName());
				}

				pendingTask.setTaskList(taskList);
				pendingTaskView.add(pendingTask);
			}
		}
		
		return new ResponseEntity<>(pendingTaskView, HttpStatus.OK);

	}

	@RequestMapping(value = "/pending-taskList/forward-task-plan", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> forwardTaskPlan(@RequestParam String userPid, @RequestParam LocalDate date,
			@RequestParam String taskPid) {
		// check already assigned in the given date
		List<ExecutiveTaskPlan> executiveTaskPlans = executiveTaskPlanRepository.findByUserPidAndTaskPid(userPid,
				taskPid);
		System.out.println("size :" + executiveTaskPlans.size());
		List<ExecutiveTaskPlan> newExecutiveTaskPlanList = new ArrayList<>();
		Optional<User> optionalUser = userRepository.findOneByPid(userPid);
		if (executiveTaskPlans != null && !executiveTaskPlans.isEmpty() && optionalUser.isPresent()) {
			for (ExecutiveTaskPlan eTaskPlan : executiveTaskPlans) {
				if(eTaskPlan.getAccountType().getName()=="Lead")
				{
				ExecutiveTaskPlan newTaskPlan = new ExecutiveTaskPlan();
				newTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
				newTaskPlan.setCreatedDate(LocalDateTime.now());
				newTaskPlan.setCreatedBy(SecurityUtils.getCurrentUserLogin());
				newTaskPlan.setTaskPlanStatus(TaskPlanStatus.CREATED);
				newTaskPlan.setRemarks(eTaskPlan.getRemarks());
				newTaskPlan.setPlannedDate(date.atTime(LocalTime.now()));
				newTaskPlan.setCompany(eTaskPlan.getCompany());
				newTaskPlan.setUser(optionalUser.get());
				newTaskPlan.setActivity(eTaskPlan.getActivity());
				newTaskPlan.setAccountType(eTaskPlan.getAccountType());
				newTaskPlan.setAccountProfile(eTaskPlan.getAccountProfile());
				newTaskPlan.setTaskCreatedType(TaskCreatedType.TASK_SERVER);
				newTaskPlan.setTask(eTaskPlan.getTask());
				newTaskPlan.setTaskGroup(eTaskPlan.getTaskGroup());
				newTaskPlan.setTaskList(eTaskPlan.getTaskList());
				newTaskPlan.setSortOrder(eTaskPlan.getSortOrder());

				newExecutiveTaskPlanList.add(newTaskPlan);
			}
				}
		}
		executiveTaskPlanService.save(newExecutiveTaskPlanList);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/pending-taskList/{pid}/{activated}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deactivateTaskPlan(@PathVariable String pid, @PathVariable boolean activated) {
		log.debug("REST request to deactivate TaskPlan : {}", pid, activated);
		ExecutiveTaskPlan taskplan = executiveTaskPlanRepository.findOneByPid(pid).get();

		taskplan.setActivated(activated);
		taskplan = executiveTaskPlanRepository.save(taskplan);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("taskplan", pid.toString())).build();

	}
}