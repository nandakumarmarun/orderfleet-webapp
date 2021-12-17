package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		executiveTaskPlan.setAccountProfile(
				accountProfileRepository.findOneByPid(executiveTaskPlanDTO.getAccountProfilePid()).get());
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
	                DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id1 = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description1 ="get one by pid";
	        		LocalDateTime startLCTime1 = LocalDateTime.now();
	        		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
	        		String startDate1 = startLCTime1.format(DATE_FORMAT1);
	        		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		executiveTaskPlan
				.setAccountType(accountTypeRepository.findOneByPid(executiveTaskPlanDTO.getAccountTypePid()).get());
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
	                DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id11 = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description11 ="get one by pid";
	        		LocalDateTime startLCTime11 = LocalDateTime.now();
	        		String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
	        		String startDate11 = startLCTime11.format(DATE_FORMAT11);
	        		logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
		executiveTaskPlan.setActivity(activityRepository.findOneByPid(executiveTaskPlanDTO.getActivityPid()).get());
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
				DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				newExecutiveTaskPlan.setAccountProfile(
							accountProfileRepository.findOneByPid(executiveTaskPlanDTO.getAccountProfilePid()).get());
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
				newExecutiveTaskPlan.setAccountType(newExecutiveTaskPlan.getAccountProfile().getAccountType());
				DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get one by pid";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
				newExecutiveTaskPlan
						.setActivity(activityRepository.findOneByPid(executiveTaskPlanDTO.getActivityPid()).get());
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
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			newExecutiveTaskPlan
					.setActivity(activityRepository.findOneByPid(task.getActivity().getPid()).get());
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
