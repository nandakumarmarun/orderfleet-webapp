package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

				ActivityGroupUserTarget activityGroupUserTarget = activityGroupUserTargetRepository
						.findActivityGroupUserTarget(user.getId(),
								executiveTaskGroupPlan.getTaskGroup().getActivityGroup().getId(), LocalDate.now());
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
				Optional<AccountProfile> accountProfile = accountProfileRepository
						.findOneByPid(executiveTaskPlanDTO.getAccountProfilePid());
				if (accountProfile.isPresent()) {
					executiveTaskPlan.setAccountProfile(accountProfile.get());
				}
				Optional<AccountType> accountType = accountTypeRepository
						.findOneByPid(executiveTaskPlanDTO.getAccountTypePid());
				if (accountType.isPresent()) {
					executiveTaskPlan.setAccountType(accountType.get());
				}
				executiveTaskPlan
						.setActivity(activityRepository.findOneByPid(executiveTaskPlanDTO.getActivityPid()).get());
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
