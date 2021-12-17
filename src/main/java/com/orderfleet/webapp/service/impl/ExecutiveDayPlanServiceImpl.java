package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ExecutiveTaskGroupPlan;
import com.orderfleet.webapp.domain.ExecutiveTaskListPlan;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityGroupUserTargetRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskGroupPlanRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskListPlanRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.TaskGroupRepository;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveDayPlanService;
import com.orderfleet.webapp.service.ExecutiveTaskGroupPlanService;
import com.orderfleet.webapp.service.ExecutiveTaskListPlanService;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ExecutiveDayPlanDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskGroupPlanDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskListPlanDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;
import com.orderfleet.webapp.web.rest.api.dto.DayPlanResponse;
import com.orderfleet.webapp.web.rest.api.dto.TaskGroupResponse;

/**
 * Service Implementation for managing ExecutiveDayPlan.
 * 
 * @author Sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class ExecutiveDayPlanServiceImpl implements ExecutiveDayPlanService {

	private final Logger log = LoggerFactory.getLogger(ExecutiveDayPlanServiceImpl.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private TaskGroupRepository taskGroupRepository;

	@Inject
	private ExecutiveTaskGroupPlanRepository executiveTaskGroupPlanRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private ExecutiveTaskListPlanRepository executiveTaskListPlanRepository;

	@Inject
	private ActivityGroupUserTargetRepository activityGroupUserTargetRepository;

	@Override
	public DayPlanResponse saveExecutiveDayPlan(ExecutiveDayPlanDTO executiveDayPlan) {
		log.debug("Rest request to save ExecutiveDayPlan : {}", executiveDayPlan);

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		saveExecutiveTaskPlan(executiveDayPlan.getExecutiveTaskPlanDTOs(), user, company);
		List<TaskGroupResponse> taskGroupResponses = saveExecutiveTaskGroupPlan(
				executiveDayPlan.getExecutiveTaskGroupPlanDTOs(), user, company);
		saveExecutiveTaskListPlan(executiveDayPlan.getExecutiveTaskListPlanDTOs(), user, company);

		DayPlanResponse dayPlanResponse = new DayPlanResponse();
		dayPlanResponse.setTaskGroupResponses(taskGroupResponses);
		dayPlanResponse.setUserPid(user.getPid());
		return dayPlanResponse;
	}

	private void saveExecutiveTaskListPlan(List<ExecutiveTaskListPlanDTO> executiveTaskListPlanDTOs, User user,
			Company company) {
		log.debug("Request to save ExecutiveTaskListPlan : {}", executiveTaskListPlanDTOs);
		if (executiveTaskListPlanDTOs != null) {
			for (ExecutiveTaskListPlanDTO executiveTaskListPlanDTO : executiveTaskListPlanDTOs) {
				ExecutiveTaskListPlan executiveTaskListPlan = new ExecutiveTaskListPlan();
				executiveTaskListPlan.setPid(ExecutiveTaskListPlanService.PID_PREFIX + RandomUtil.generatePid());
				executiveTaskListPlan.setCompany(company);
				executiveTaskListPlan.setUser(user);
				executiveTaskListPlan.setPlannedDate(executiveTaskListPlanDTO.getPlannedDate());
				executiveTaskListPlan.setRemarks(executiveTaskListPlanDTO.getRemarks());
				executiveTaskListPlan.setCreatedDate(LocalDateTime.now());
				Optional<TaskList> taskList = taskListRepository.findByNameIgnoreCaseAndPid(
						executiveTaskListPlanDTO.getTaskListName(), executiveTaskListPlanDTO.getTaskListPid());
				if (taskList.isPresent()) {
					executiveTaskListPlan.setTaskList(taskList.get());
				}
				executiveTaskListPlan = executiveTaskListPlanRepository.save(executiveTaskListPlan);
			}
		}
	}

	private List<TaskGroupResponse> saveExecutiveTaskGroupPlan(
			List<ExecutiveTaskGroupPlanDTO> executiveTaskGroupPlanDTOs, User user, Company company) {
		log.debug("Request to save List<ExecutiveTaskGroupPlan> : {}", executiveTaskGroupPlanDTOs);
		List<TaskGroupResponse> taskGroupResponseList = new ArrayList<>();
		if (executiveTaskGroupPlanDTOs != null) {
			List<ExecutiveTaskGroupPlan> executiveTaskGroupPlans = new ArrayList<>();
			executiveTaskGroupPlanDTOs.stream().forEach(taskGroupPlanDTO -> {
				ExecutiveTaskGroupPlan executiveTaskGroupPlan = new ExecutiveTaskGroupPlan();
				executiveTaskGroupPlan.setPid(ExecutiveTaskGroupPlanService.PID_PREFIX + RandomUtil.generatePid());
				executiveTaskGroupPlan.setPlannedDate(taskGroupPlanDTO.getPlannedDate());
				executiveTaskGroupPlan.setCreatedDate(LocalDateTime.now());
				executiveTaskGroupPlan.setRemarks(taskGroupPlanDTO.getRemarks());
				Optional<TaskGroup> taskGroup = taskGroupRepository.findByNameIgnoreCaseAndPid(
						taskGroupPlanDTO.getTaskGroupName(), taskGroupPlanDTO.getTaskGroupPid());
				if (taskGroup.isPresent()) {
					executiveTaskGroupPlan.setTaskGroup(taskGroup.get());
				}
				executiveTaskGroupPlan.setUser(user);
				executiveTaskGroupPlan.setCompany(company);
				executiveTaskGroupPlans.add(executiveTaskGroupPlan);
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "AGUT_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get activity group user target";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				ActivityGroupUserTarget activityGroupUserTarget = activityGroupUserTargetRepository
						.findActivityGroupUserTarget(user.getId(),
								executiveTaskGroupPlan.getTaskGroup().getActivityGroup().getId(), LocalDate.now());
				String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

				if (activityGroupUserTarget != null) {
					TaskGroupResponse taskGroupResponse = new TaskGroupResponse();
					taskGroupResponse
							.setActivityGroupPid(executiveTaskGroupPlan.getTaskGroup().getActivityGroup().getPid());
					taskGroupResponse
							.setActivityGroupName(executiveTaskGroupPlan.getTaskGroup().getActivityGroup().getName());
					taskGroupResponse.setTarget(activityGroupUserTarget.getTargetNumber());
					taskGroupResponse.setStartDate(activityGroupUserTarget.getStartDate());
					taskGroupResponse.setEndDate(activityGroupUserTarget.getEndDate());
					taskGroupResponseList.add(taskGroupResponse);
				}
			});
			executiveTaskGroupPlanRepository.save(executiveTaskGroupPlans);
			return taskGroupResponseList;
		}
		return taskGroupResponseList;
	}

	private void saveExecutiveTaskPlan(List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs, User user, Company company) {
		log.debug("Request to save  List<ExecutiveTaskPlan> : {}", executiveTaskPlanDTOs);
		if (executiveTaskPlanDTOs != null) {
			for (ExecutiveTaskPlanDTO executiveTaskPlanDTO : executiveTaskPlanDTOs) {
				ExecutiveTaskPlan executiveTaskPlan = new ExecutiveTaskPlan();
				executiveTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
				  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get one by pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				Optional<AccountProfile> accountProfile = accountProfileRepository
						.findOneByPid(executiveTaskPlanDTO.getAccountProfilePid());
				String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);

				if (accountProfile.isPresent()) {
					executiveTaskPlan.setAccountProfile(accountProfile.get());
				}
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get one by pid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				Optional<AccountType> accountType = accountTypeRepository
						.findOneByPid(executiveTaskPlanDTO.getAccountTypePid());
				 String flag1 = "Normal";
					LocalDateTime endLCTime1 = LocalDateTime.now();
					String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
					String endDate1 = startLCTime1.format(DATE_FORMAT1);
					Duration duration1 = Duration.between(startLCTime1, endLCTime1);
					long minutes1 = duration1.toMinutes();
					if (minutes1 <= 1 && minutes1 >= 0) {
						flag1 = "Fast";
					}
					if (minutes1 > 1 && minutes1 <= 2) {
						flag1 = "Normal";
					}
					if (minutes1 > 2 && minutes1 <= 10) {
						flag1 = "Slow";
					}
					if (minutes1 > 10) {
						flag1 = "Dead Slow";
					}
			                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
							+ description1);

				if (accountType.isPresent()) {
					executiveTaskPlan.setAccountType(accountType.get());
				}
				 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id11 = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description11 ="get one by pid";
					LocalDateTime startLCTime11 = LocalDateTime.now();
					String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
					String startDate11 = startLCTime11.format(DATE_FORMAT11);
					logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
				executiveTaskPlan
						.setActivity(activityRepository.findOneByPid(executiveTaskPlanDTO.getActivityPid()).get());
				
				  String flag11 = "Normal";
					LocalDateTime endLCTime11 = LocalDateTime.now();
					String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
					String endDate11 = startLCTime11.format(DATE_FORMAT11);
					Duration duration11 = Duration.between(startLCTime11, endLCTime11);
					long minutes11 = duration11.toMinutes();
					if (minutes11 <= 1 && minutes11 >= 0) {
						flag11 = "Fast";
					}
					if (minutes11 > 1 && minutes11 <= 2) {
						flag11 = "Normal";
					}
					if (minutes11 > 2 && minutes11 <= 10) {
						flag11 = "Slow";
					}
					if (minutes11 > 10) {
						flag11 = "Dead Slow";
					}
			                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
							+ description11);
executiveTaskPlan.setCreatedBy(SecurityUtils.getCurrentUserLogin());
				executiveTaskPlan.setCreatedDate(LocalDateTime.now());
				executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.PENDING);
				executiveTaskPlan.setPlannedDate(executiveTaskPlanDTO.getPlannedDate());
				executiveTaskPlan.setRemarks(executiveTaskPlanDTO.getRemarks());
				executiveTaskPlan.setUserRemarks(executiveTaskPlanDTO.getUserRemarks());
				executiveTaskPlan.setUser(user);
				executiveTaskPlan.setCompany(company);

				// Task Created Type
				executiveTaskPlan.setTaskCreatedType(executiveTaskPlanDTO.getTaskCreatedType());
				if (!executiveTaskPlanDTO.getTaskCreatedType().equals(TaskCreatedType.TASK_CLIENT)) {
					executiveTaskPlan.setTask(taskRepository.findOneByPid(executiveTaskPlanDTO.getTaskPid()).get());
					if (executiveTaskPlanDTO.getTaskCreatedType().equals(TaskCreatedType.TASK_GROUP_SERVER)) {
						executiveTaskPlan.setTaskGroup(
								taskGroupRepository.findOneByPid(executiveTaskPlanDTO.getTaskGroupPid()).get());
					} else if (executiveTaskPlanDTO.getTaskCreatedType().equals(TaskCreatedType.TASK_LIST_SERVER)) {
						executiveTaskPlan.setTaskList(
								taskListRepository.findOneByPid(executiveTaskPlanDTO.getTaskListPid()).get());
					}
				}
				executiveTaskPlan = executiveTaskPlanRepository.save(executiveTaskPlan);
			}
		}
	}

}
