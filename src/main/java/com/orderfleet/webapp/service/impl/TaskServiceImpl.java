package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.mapper.TaskMapper;

/**
 * Service Implementation for managing Task.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

	private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private TaskRepository taskRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TaskMapper taskMapper;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private AccountProfileMapper accountProfileMapper;

	/**
	 * Save a task.
	 * 
	 * @param taskDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public List<TaskDTO> save(TaskDTO taskDTO) {
		log.debug("Request to save Task : {}", taskDTO);
		List<Task> tasks = new ArrayList<>();
		for (String accountPid : taskDTO.getAccountPids()) {
			Task task = new Task();
			// set pid
			List<Task> tasks2=taskRepository.findTaskByActivityPidAndAccountPid(taskDTO.getActivityPid(), accountPid);
			if(tasks2.size()== 0){
			taskDTO.setPid(TaskService.PID_PREFIX + RandomUtil.generatePid());
			taskDTO.setAccountProfilePid(accountPid);
			task = taskMapper.taskDTOToTask(taskDTO);
			// set company
			task.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
			tasks.add(task);
			}
		}
		List<Task> savedTasks = taskRepository.save(tasks);
		return taskMapper.tasksToTaskDTOs(savedTasks);
	}

	/**
	 * Update a task.
	 * 
	 * @param taskDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public TaskDTO update(TaskDTO taskDTO) {
		log.debug("Request to Update Task : {}", taskDTO);

		return taskRepository.findOneByPid(taskDTO.getPid()).map(task -> {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			task.setActivity(activityRepository.findOneByPid(taskDTO.getActivityPid()).get());
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
			task.setAccountType(accountTypeRepository.findOneByPid(taskDTO.getAccountTypePid()).get());
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
			String id11 = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 ="get one by pid";
			LocalDateTime startLCTime11 = LocalDateTime.now();
			String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
			String startDate11 = startLCTime11.format(DATE_FORMAT11);
			logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			task.setAccountProfile(accountProfileRepository.findOneByPid(taskDTO.getAccountProfilePid()).get());
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

			task.setRemarks(taskDTO.getRemarks());
			task = taskRepository.save(task);
			TaskDTO result = taskMapper.taskToTaskDTO(task);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the tasks.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Task> findAll(Pageable pageable) {
		log.debug("Request to get all Tasks");
		Page<Task> result = taskRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the tasks.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findAllByCompany() {
		log.debug("Request to get all Tasks");
		List<Task> tasks = taskRepository.findAllByCompanyId();
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(tasks);
		return result;
	}

	/**
	 * Get all the tasks.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Tasks");
		Page<Task> tasks = taskRepository.findAllByCompanyId(pageable);
		Page<TaskDTO> result = new PageImpl<TaskDTO>(taskMapper.tasksToTaskDTOs(tasks.getContent()), pageable,
				tasks.getTotalElements());
		return result;
	}

	/**
	 * Get one task by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public TaskDTO findOne(Long id) {
		log.debug("Request to get Task : {}", id);
		Task task = taskRepository.findOne(id);
		TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);
		return taskDTO;
	}

	/**
	 * Get one task by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskDTO> findOneByPid(String pid) {
		log.debug("Request to get Task by pid : {}", pid);
		return taskRepository.findOneByPid(pid).map(task -> {
			TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);
			return taskDTO;
		});
	}

	/**
	 * Delete the task by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Task : {}", pid);
		taskRepository.findOneByPid(pid).ifPresent(task -> {
			taskRepository.delete(task.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findByActivityPids(List<String> activgityPids) {
		List<Task> tasks = taskRepository.findTaskByActivityPidIn(activgityPids);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(tasks);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findByAccountProfilePids(List<String> accProfilePids) {
		List<Task> tasks = taskRepository.findTaskByAccountProfilePidIn(accProfilePids);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(tasks);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findByActivityPidsAndAccountProfilePids(List<String> activgityPids,
			List<String> accProfilePids) {
		List<Task> tasks = taskRepository.findTaskByActivityPidInAndAccountProfilePidIn(activgityPids,accProfilePids);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(tasks);
		return result;
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * Get all the AccountProfiles of a company By AccountType and Activity.
	 * 
	 * @param activityPid
	 *            the activityPid of the entity
	 * @param accountTypePid
	 *            the accountTypePid of the entity          
	 *            
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountProfileDTO> findAccountProfileByAccountTypePidAndActivityPid(String activityPid,String accountTypePid) {
		List<AccountProfile> accountProfiles=taskRepository.findAllAccountProfileByCompanyAndAccountTypePid(accountTypePid, activityPid);
		List<AccountProfileDTO> result = accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfiles);
		return result;
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * Update task By Pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity          
	 *            
	 * @return the list of entities
	 */
	@Override
	public TaskDTO updateTaskStatus(String pid, boolean activate) {
		log.debug("Request to update task status");
		return taskRepository.findOneByPid(pid).map(task -> {
			task.setActivated(activate);
			task = taskRepository.save(task);
			TaskDTO result = taskMapper.taskToTaskDTO(task);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * find all the Tasks by company activated.
	 * 
	 * @param active
	 *            the active of the entity
	 *            
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findAllByCompanyAndActivated(boolean active) {
		log.debug("Request to get Activated Task");
		List<Task> tasks = taskRepository.findAllByCompanyIdAndActivated(active);
		List<TaskDTO> taskDTOs = taskMapper.tasksToTaskDTOs(tasks);
		return taskDTOs;
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * find all the Tasks by ActivityPids And Activated.
	 * 
	 * @param activityPids
	 *            the activityPids of the entity
	 *            
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findByActivityPidsAndActivated(List<String> activityPids) {
		log.debug("Request to get Task By ActivityPids And Activated");
		List<Task> taskList = taskRepository.findByActivityPidInAndActivated(activityPids, true);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(taskList);
		return result;
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 * find all the Tasks by AccountTypePids And Activated.
	 * 
	 * @param accountTypePids
	 *            the accountTypePids of the entity
	 *            
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findByAccountTypePidsAndActivated(List<String> accountTypePids) {
		log.debug("Request to get Task By AccountTypePids And Activated");
		List<Task> taskList = taskRepository.findByAccountTypePidInAndActivated(accountTypePids, true);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(taskList);
		return result;
	}

	/**
	 * @author Fahad
	 * @since Mar 28, 2017
	 * 
	 *        find all the Tasks by AccountTypePids And ActivityPids.
	 * 
	 * @param accountTypePids
	 *            the accountTypePids of the entity
	 * 
	 * @param activityPids
	 *            the activityPids of the entity
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskDTO> findByActivityPidsAndAccountTypePidsAndActivated(List<String> activityPids,
			List<String> accountTypePids) {
		log.debug("Request to get Task By AccountTypePids , ActivityPids And Activated");
		List<Task> taskList = taskRepository
				.findByActivityPidInAndAccountTypePidInAndActivated(activityPids, accountTypePids, true);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(taskList);
		return result;
	}
}
