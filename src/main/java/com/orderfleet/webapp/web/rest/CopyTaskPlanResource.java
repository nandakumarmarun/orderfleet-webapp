package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;

/**
 * REST controller for CopyTaskPlan.
 * 
 * @author Shaheer
 * @since January 23, 2017
 */
@Controller
@RequestMapping(value = "/web")
public class CopyTaskPlanResource {

	private final UserService userService;
	
	private final UserRepository userRepository;

	private final ExecutiveTaskPlanService executiveTaskPlanService;
	
	private final ExecutiveTaskPlanRepository executiveTaskPlanRepository;
	
	public CopyTaskPlanResource(UserService userService, UserRepository userRepository,
			ExecutiveTaskPlanService executiveTaskPlanService,
			ExecutiveTaskPlanRepository executiveTaskPlanRepository) {
		super();
		this.userService = userService;
		this.userRepository = userRepository;
		this.executiveTaskPlanService = executiveTaskPlanService;
		this.executiveTaskPlanRepository = executiveTaskPlanRepository;
	}

	/**
	 * GET /copy-task-plan : get copy-task-plan page.
	 */
	@GetMapping("/copy-task-plan")
	public String getCopyTaskPlanPage(Model model) {
		model.addAttribute("users", userService.findAllByCompany());
		return "company/copy-task-plan";
	}
	
	@PostMapping("/copy-task-plan")
	@ResponseBody
	public ResponseEntity<Void> copyTaskPlan(@RequestParam String userPid, @RequestParam LocalDate copytoDate, @RequestParam String taskPlanPids) {
		//check already assigned in the given date
		List<ExecutiveTaskPlan> executiveTaskPlans = executiveTaskPlanRepository.findByUserPidAndPidIn(userPid, Arrays.asList(taskPlanPids.split(",")));
		List<ExecutiveTaskPlan> newExecutiveTaskPlanList = new ArrayList<>();
		Optional<User> optionalUser = userRepository.findOneByPid(userPid);
		if(executiveTaskPlans != null && !executiveTaskPlans.isEmpty() && optionalUser.isPresent()){
			for (ExecutiveTaskPlan eTaskPlan : executiveTaskPlans) {
				ExecutiveTaskPlan newTaskPlan = new ExecutiveTaskPlan();
				newTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
				newTaskPlan.setCreatedDate(LocalDateTime.now());
				newTaskPlan.setCreatedBy(SecurityUtils.getCurrentUserLogin());
				newTaskPlan.setTaskPlanStatus(TaskPlanStatus.CREATED);
				newTaskPlan.setRemarks(eTaskPlan.getRemarks());
				newTaskPlan.setPlannedDate(copytoDate.atTime(LocalTime.now()));
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
		executiveTaskPlanService.save(newExecutiveTaskPlanList);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/delete-task-plan")
	@ResponseBody
	@Transactional
	public ResponseEntity<Void> deleteTaskPlan(@RequestParam String userPid, @RequestParam LocalDate plannedDate) {
		executiveTaskPlanRepository.deleteByUserPidAndTaskPlanStatusAndPlannedDateBetween(userPid,
				TaskPlanStatus.CREATED, plannedDate.atTime(0, 0), plannedDate.atTime(23, 59));
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/planned-user-tasks", params = { "userPid" })
	public ResponseEntity<List<String>> getPlannedUserTasks(@RequestParam(value = "userPid") String userPid) {
		LocalDate today = LocalDate.now();
		List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs = executiveTaskPlanService
				.findDistictPlannedDateByUserPidAndPlannedDateGreaterThanEqualToDate(userPid, today.atTime(0, 0));
		//return future planned date string : distinct by date
		return new ResponseEntity<>(executiveTaskPlanDTOs.stream().filter(distinctByKey(ExecutiveTaskPlanDTO::getPlannedDate)).map(p -> p.getPlannedDate().toLocalDate().toString()).collect(Collectors.toList()), HttpStatus.OK);
	}
	
	@GetMapping(value = "/planned-user-tasks", params = {"userPid", "startDate", "endDate"})
	public ResponseEntity<Map<LocalDateTime, List<ExecutiveTaskPlanDTO>>> getUserTasksByPlannedDateBetween(@RequestParam(value = "userPid") String userPid, @RequestParam(value = "startDate") LocalDate startDate, @RequestParam(value = "endDate") LocalDate endDate) {
		List<ExecutiveTaskPlanDTO> executiveTaskPlanDtos = executiveTaskPlanService.findByUserPidAndPlannedDateBetween(userPid, startDate,endDate);
		//group plan by date
		Map<LocalDateTime, List<ExecutiveTaskPlanDTO>> etpGroupByPlannedDate = executiveTaskPlanDtos.stream().collect(Collectors.groupingBy(ExecutiveTaskPlanDTO::getPlannedDate,LinkedHashMap::new, Collectors.toList()));
		return new ResponseEntity<>(etpGroupByPlannedDate, HttpStatus.OK);
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}
