package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TaskCreatedType;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveTaskPlanService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;

/**
 * Service Implementation for managing ExecutiveTaskPlan.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
@Service
@Transactional
public class ExecutiveTaskPlanServiceImpl implements ExecutiveTaskPlanService {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskPlanServiceImpl.class);

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TaskRepository taskService;

	@Inject
	private TaskListRepository taskListService;

	/**
	 * Save a executiveTaskPlan.
	 * 
	 * @param executiveTaskPlanDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskPlanDTO save(ExecutiveTaskPlanDTO executiveTaskPlanDTO) {
		log.debug("Request to save ExecutiveTaskPlan : {}", executiveTaskPlanDTO);
		ExecutiveTaskPlan executiveTaskPlan = new ExecutiveTaskPlan();
		// set pid
		executiveTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
		executiveTaskPlan.setAccountProfile(
				accountProfileRepository.findOneByPid(executiveTaskPlanDTO.getAccountProfilePid()).get());
		executiveTaskPlan
				.setAccountType(accountTypeRepository.findOneByPid(executiveTaskPlanDTO.getAccountTypePid()).get());
		executiveTaskPlan.setActivity(activityRepository.findOneByPid(executiveTaskPlanDTO.getActivityPid()).get());
		executiveTaskPlan.setCreatedDate(LocalDateTime.now());
		executiveTaskPlan.setCreatedBy(SecurityUtils.getCurrentUserLogin());
		executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.PENDING);

		// <.............PlanedDate must check.............>
		executiveTaskPlan.setPlannedDate(executiveTaskPlanDTO.getPlannedDate());
		executiveTaskPlan.setRemarks(executiveTaskPlanDTO.getRemarks());
		executiveTaskPlan.setUserRemarks(executiveTaskPlanDTO.getUserRemarks());
		executiveTaskPlan.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
		// set company
		executiveTaskPlan.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		executiveTaskPlan = executiveTaskPlanRepository.save(executiveTaskPlan);
		ExecutiveTaskPlanDTO result = new ExecutiveTaskPlanDTO(executiveTaskPlan);
		return result;
	}

	@Override
	public void save(String userPid, List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs, LocalDate plannedDate) {
		List<ExecutiveTaskPlan> executiveTaskPlanList = new ArrayList<>();
		userRepository.findOneByPid(userPid).ifPresent(user -> {
			// delete and save new
			executiveTaskPlanRepository.deleteByUserPidAndTaskPlanStatusAndPlannedDateBetween(userPid,
					TaskPlanStatus.CREATED, plannedDate.atTime(0, 0), plannedDate.atTime(23, 59));
			for (ExecutiveTaskPlanDTO executiveTaskPlanDTO : executiveTaskPlanDTOs) {
				// create new executive plan
				ExecutiveTaskPlan newExecutiveTaskPlan = new ExecutiveTaskPlan();
				newExecutiveTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
				newExecutiveTaskPlan.setAccountProfile(
						accountProfileRepository.findOneByPid(executiveTaskPlanDTO.getAccountProfilePid()).get());
				newExecutiveTaskPlan.setAccountType(newExecutiveTaskPlan.getAccountProfile().getAccountType());
				newExecutiveTaskPlan
						.setActivity(activityRepository.findOneByPid(executiveTaskPlanDTO.getActivityPid()).get());
				newExecutiveTaskPlan.setCreatedDate(LocalDateTime.now());
				newExecutiveTaskPlan.setCreatedBy(SecurityUtils.getCurrentUserLogin());
				newExecutiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.CREATED);
				newExecutiveTaskPlan.setPlannedDate(executiveTaskPlanDTO.getPlannedDate());
				newExecutiveTaskPlan.setRemarks(executiveTaskPlanDTO.getRemarks());
				newExecutiveTaskPlan.setUserRemarks(executiveTaskPlanDTO.getUserRemarks());
				newExecutiveTaskPlan.setUser(user);
				newExecutiveTaskPlan.setCompany(user.getCompany());
				newExecutiveTaskPlan.setTaskCreatedType(executiveTaskPlanDTO.getTaskCreatedType());
				// set task,taskList,taskGroup
				taskService.findOneByPid(executiveTaskPlanDTO.getTaskPid())
						.ifPresent(t -> newExecutiveTaskPlan.setTask(t));
				taskListService.findOneByPid(executiveTaskPlanDTO.getTaskListPid())
						.ifPresent(tl -> newExecutiveTaskPlan.setTaskList(tl));
				executiveTaskPlanList.add(newExecutiveTaskPlan);
			}
		});
		executiveTaskPlanRepository.save(executiveTaskPlanList);
	}

	/**
	 * Update a executiveTaskPlan.
	 * 
	 * @param executiveTaskPlanDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskPlanDTO update(ExecutiveTaskPlanDTO executiveTaskPlanDTO) {
		return executiveTaskPlanDTO;
	}

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ExecutiveTaskPlan> findAll(Pageable pageable) {
		log.debug("Request to get all ExecutiveTaskPlans");
		Page<ExecutiveTaskPlan> result = executiveTaskPlanRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskPlanDTO> findAllByCompany() {
		log.debug("Request to get all ExecutiveTaskPlan");
		List<ExecutiveTaskPlan> executiveTaskPlanList = executiveTaskPlanRepository.findAllByCompanyId();
		List<ExecutiveTaskPlanDTO> result = executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskPlanDTO> findByUserIsCurrentUser() {
		log.debug("Request to get Current User ExecutiveTaskPlan");
		List<ExecutiveTaskPlan> executiveTaskPlanList = executiveTaskPlanRepository.findByUserIsCurrentUser();
		List<ExecutiveTaskPlanDTO> result = executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ExecutiveTaskPlanDTO> findByUserPidAndPlannedDate(String userPid, LocalDate plannedDate) {
		List<ExecutiveTaskPlan> executiveTaskPlanList = executiveTaskPlanRepository
				.findByUserPidAndTaskPlanStatusAndPlannedDateBetween(userPid, TaskPlanStatus.CREATED,
						plannedDate.atTime(0, 0), plannedDate.atTime(23, 59));
		List<ExecutiveTaskPlanDTO> result = executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ExecutiveTaskPlanDTO> findByUserPidAndPlannedDateBetween(String userPid, LocalDate startDate,
			LocalDate endDate) {
		List<ExecutiveTaskPlan> executiveTaskPlanList = executiveTaskPlanRepository
				.findByUserPidAndPlannedDateBetweenOrderByPlannedDateAsc(userPid, startDate.atTime(0, 0),
						endDate.atTime(23, 59));
		List<ExecutiveTaskPlanDTO> result = executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ExecutiveTaskPlanDTO> findByUserPidAndPlannedDateAndNotTaskPlanStatus(String userPid,
			LocalDate plannedDate, TaskPlanStatus taskPlanStatus) {
		List<ExecutiveTaskPlan> executiveTaskPlanList = executiveTaskPlanRepository
				.findByUserPidAndPlannedDateBetweenAndTaskPlanStatusNot(userPid, plannedDate.atTime(0, 0),
						plannedDate.atTime(23, 59), taskPlanStatus);
		List<ExecutiveTaskPlanDTO> result = executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the executiveTaskPlans.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ExecutiveTaskPlanDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ExecutiveTaskPlan");
		Page<ExecutiveTaskPlan> executiveTaskPlans = executiveTaskPlanRepository.findAllByCompanyId(pageable);
		List<ExecutiveTaskPlanDTO> executiveTaskPlanDTOs = executiveTaskPlans.getContent().stream()
				.map(ExecutiveTaskPlanDTO::new).collect(Collectors.toList());
		Page<ExecutiveTaskPlanDTO> result = new PageImpl<ExecutiveTaskPlanDTO>(executiveTaskPlanDTOs, pageable,
				executiveTaskPlans.getTotalElements());
		return result;
	}

	/**
	 * Get one executiveTaskPlan by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ExecutiveTaskPlanDTO findOne(Long id) {
		log.debug("Request to get ExecutiveTaskPlan : {}", id);
		ExecutiveTaskPlan executiveTaskPlan = executiveTaskPlanRepository.findOne(id);
		ExecutiveTaskPlanDTO executiveTaskPlanDTO = new ExecutiveTaskPlanDTO(executiveTaskPlan);
		return executiveTaskPlanDTO;
	}

	/**
	 * Get one executiveTaskPlan by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ExecutiveTaskPlanDTO> findOneByPid(String pid) {
		log.debug("Request to get ExecutiveTaskPlan by pid : {}", pid);
		return executiveTaskPlanRepository.findOneByPid(pid).map(executiveTaskPlan -> {
			ExecutiveTaskPlanDTO executiveTaskPlanDTO = new ExecutiveTaskPlanDTO(executiveTaskPlan);
			return executiveTaskPlanDTO;
		});
	}

	/**
	 * Delete the executiveTaskPlan by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ExecutiveTaskPlan : {}", pid);
		executiveTaskPlanRepository.findOneByPid(pid).ifPresent(executiveTaskPlan -> {
			executiveTaskPlanRepository.delete(executiveTaskPlan.getId());
		});
	}

	@Override
	public List<ExecutiveTaskPlanDTO> findDistictPlannedDateByUserPidAndPlannedDateGreaterThanEqualToDate(
			String userPid, LocalDateTime date) {
		List<ExecutiveTaskPlan> executiveTaskPlanList = executiveTaskPlanRepository
				.findByUserPidAndPlannedDateGreaterThanEqualOrderByPlannedDateAsc(userPid, date);
		return executiveTaskPlanList.stream().map(ExecutiveTaskPlanDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public void save(List<ExecutiveTaskPlan> newExecutiveTaskPlans) {
		executiveTaskPlanRepository.save(newExecutiveTaskPlans);
	}

	@Override
	public void saveDayPlanByRootPlanBased(TaskList taskList) {
		List<ExecutiveTaskPlan> newExecutiveTaskPlanList = new ArrayList<>();
		for (Task task : taskList.getTasks()) {
			ExecutiveTaskPlan newExecutiveTaskPlan = new ExecutiveTaskPlan();
			newExecutiveTaskPlan.setPid(ExecutiveTaskPlanService.PID_PREFIX + RandomUtil.generatePid());
			newExecutiveTaskPlan.setAccountProfile(
					accountProfileRepository.findOneByPid(task.getAccountProfile().getPid()).get());
			newExecutiveTaskPlan.setAccountType(newExecutiveTaskPlan.getAccountProfile().getAccountType());
			newExecutiveTaskPlan
					.setActivity(activityRepository.findOneByPid(task.getActivity().getPid()).get());
			newExecutiveTaskPlan.setCreatedDate(LocalDateTime.now());
			newExecutiveTaskPlan.setCreatedBy(SecurityUtils.getCurrentUserLogin());
			newExecutiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.CREATED);
			newExecutiveTaskPlan.setPlannedDate(LocalDateTime.now());
			newExecutiveTaskPlan.setRemarks("");
			newExecutiveTaskPlan.setUserRemarks("");
			User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
			newExecutiveTaskPlan.setUser(user);
			newExecutiveTaskPlan.setCompany(user.getCompany());
			newExecutiveTaskPlan.setTaskCreatedType(TaskCreatedType.TASK_LIST_SERVER);
			// set task,taskList,taskGroup
			newExecutiveTaskPlan.setTask(task);
			newExecutiveTaskPlan.setTaskList(taskList);
			newExecutiveTaskPlanList.add(newExecutiveTaskPlan);
		}
		executiveTaskPlanRepository.save(newExecutiveTaskPlanList);
	}

	@Override
	public void deleteDayPlanByUserPidAndTaskListPid(String userPid,String taskListPid) {
		LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
		LocalDateTime toDate = LocalDate.now().atTime(23, 59);
		executiveTaskPlanRepository.deleteByUserPidAndTaskListPidAndPlannedDateBetween(userPid, taskListPid,fromDate,toDate);
	}

}
