package com.orderfleet.webapp.service.impl;

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
			task.setActivity(activityRepository.findOneByPid(taskDTO.getActivityPid()).get());
			task.setAccountType(accountTypeRepository.findOneByPid(taskDTO.getAccountTypePid()).get());
			task.setAccountProfile(accountProfileRepository.findOneByPid(taskDTO.getAccountProfilePid()).get());
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
