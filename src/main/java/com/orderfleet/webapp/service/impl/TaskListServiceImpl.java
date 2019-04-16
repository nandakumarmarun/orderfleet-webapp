package com.orderfleet.webapp.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;
import com.orderfleet.webapp.web.rest.mapper.TaskListMapper;
import com.orderfleet.webapp.web.rest.mapper.TaskMapper;

/**
 * Service Implementation for managing TaskList.
 * 
 * @author Sarath
 * @since July 13, 2016
 */
@Service
@Transactional
public class TaskListServiceImpl implements TaskListService {

	private final Logger log = LoggerFactory.getLogger(TaskListServiceImpl.class);

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TaskListMapper taskListMapper;

	@Inject
	private TaskRepository taskRepository;
	
	@Inject
	private TaskMapper taskMapper;

	/**
	 * Save a taskList.
	 * 
	 * @param taskListDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public TaskListDTO save(TaskListDTO taskListDTO) {
		log.debug("Request to save TaskList : {}", taskListDTO);

		// set pid
		taskListDTO.setPid(TaskListService.PID_PREFIX + RandomUtil.generatePid());
		TaskList taskList = taskListMapper.taskListDTOToTaskList(taskListDTO);
		// set company
		taskList.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taskList = taskListRepository.save(taskList);
		TaskListDTO result = taskListMapper.taskListToTaskListDTO(taskList);
		return result;
	}

	@Override
	public TaskListDTO saveAssignedTasks(String pid, String assignedTasks) {
		TaskList oneTaskList = taskListRepository.findOneByPid(pid).get();
		String[] tasks = assignedTasks.split(",");
		Set<Task> taskList = new HashSet<Task>();
		for (String taskPid : tasks) {
			Task task = taskRepository.findOneByPid(taskPid).get();
			taskList.add(task);
		}
		oneTaskList.setTasks(taskList);
		taskListRepository.save(oneTaskList);
		Optional<TaskList> optinalTaskList = taskListRepository.findOneByPid(pid);
		if(optinalTaskList.isPresent()) {
			return taskListMapper.taskListToTaskListDTO(optinalTaskList.get());
		} else {
			return null;
		}
	}

	/**
	 * Update a taskList.
	 * 
	 * @param taskListDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public TaskListDTO update(TaskListDTO taskListDTO) {
		log.debug("Request to Update TaskList : {}", taskListDTO);

		return taskListRepository.findOneByPid(taskListDTO.getPid()).map(taskList -> {
			taskList.setName(taskListDTO.getName());
			taskList.setAlias(taskListDTO.getAlias());
			taskList.setDescription(taskListDTO.getDescription());
			taskList = taskListRepository.save(taskList);
			TaskListDTO result = taskListMapper.taskListToTaskListDTO(taskList);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the taskLists.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskList> findAll(Pageable pageable) {
		log.debug("Request to get all TaskLists");
		Page<TaskList> result = taskListRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the taskLists.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskListDTO> findAllByCompany() {
		log.debug("Request to get all TaskLists");
		List<TaskList> taskListList = taskListRepository.findAllByCompanyId();
		List<TaskListDTO> result = taskListMapper.taskListsToTaskListDTOs(taskListList);
		return result;
	}

	/**
	 * Get all the taskLists.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskListDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all TaskLists");
		Page<TaskList> taskLists = taskListRepository.findAllByCompanyId(pageable);
		Page<TaskListDTO> result = new PageImpl<TaskListDTO>(
				taskListMapper.taskListsToTaskListDTOs(taskLists.getContent()), pageable, taskLists.getTotalElements());
		return result;
	}

	/**
	 * Get one taskList by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public TaskListDTO findOne(Long id) {
		log.debug("Request to get TaskList : {}", id);
		TaskList taskList = taskListRepository.findOne(id);
		TaskListDTO taskListDTO = taskListMapper.taskListToTaskListDTO(taskList);
		return taskListDTO;
	}

	/**
	 * Get one taskList by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskListDTO> findOneByPid(String pid) {
		log.debug("Request to get TaskList by pid : {}", pid);
		return taskListRepository.findOneByPid(pid).map(taskList -> {
			TaskListDTO taskListDTO = taskListMapper.taskListToTaskListDTO(taskList);
			return taskListDTO;
		});
	}

	/**
	 * Get one taskList by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskListDTO> findByName(String name) {
		log.debug("Request to get TaskList by name : {}", name);
		return taskListRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(taskList -> {
					TaskListDTO taskListDTO = taskListMapper.taskListToTaskListDTO(taskList);
					return taskListDTO;
				});
	}

	/**
	 * Delete the taskList by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete TaskList : {}", pid);
		taskListRepository.findOneByPid(pid).ifPresent(taskList -> {
			taskListRepository.delete(taskList.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TaskListDTO> findByNameAndPid(String taskListName, String pid) {
		log.debug("Request to get TaskList by name And Pid : {}", taskListName + "  " + pid);
		return taskListRepository.findByNameIgnoreCaseAndPid(taskListName, pid).map(taskList -> {
			TaskListDTO taskListDTO = taskListMapper.taskListToTaskListDTO(taskList);
			return taskListDTO;
		});
	}

	@Override
	public List<TaskDTO> findByTaskListPid(String pid) {
		List<Task>tasks=taskListRepository.findAllByTaskListPid(pid);
		List<TaskDTO> result = taskMapper.tasksToTaskDTOs(tasks);
		return result;
	}

}
