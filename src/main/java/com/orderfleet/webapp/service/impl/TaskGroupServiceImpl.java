package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.repository.TaskGroupRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.TaskGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.TaskGroupMapper;

/**
 * Service Implementation for managing TaskGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class TaskGroupServiceImpl implements TaskGroupService {

	private final Logger log = LoggerFactory.getLogger(TaskGroupServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private TaskGroupRepository taskGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TaskGroupMapper taskGroupMapper;

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Inject
	private TaskRepository taskRepository;

	/**
	 * Save a taskGroup.
	 * 
	 * @param taskGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public TaskGroupDTO save(TaskGroupDTO taskGroupDTO) {
		log.debug("Request to save TaskGroup : {}", taskGroupDTO);

		// set pid
		taskGroupDTO.setPid(TaskGroupService.PID_PREFIX + RandomUtil.generatePid());
		TaskGroup taskGroup = taskGroupMapper.taskGroupDTOToTaskGroup(taskGroupDTO);
		// set company
		taskGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taskGroup = taskGroupRepository.save(taskGroup);
		TaskGroupDTO result = taskGroupMapper.taskGroupToTaskGroupDTO(taskGroup);
		return result;
	}

	@Override
	public void saveAssignedTasks(String pid, String assignedTasks) {
		TaskGroup taskGroup = taskGroupRepository.findOneByPid(pid).get();
		String[] tasks = assignedTasks.split(",");
		Set<Task> taskList = new HashSet<Task>();
		for (String taskPid : tasks) {
			Task task = taskRepository.findOneByPid(taskPid).get();
			taskList.add(task);
		}
		taskGroup.setTasks(taskList);
		taskGroupRepository.save(taskGroup);
	}

	/**
	 * Update a taskGroup.
	 * 
	 * @param taskGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public TaskGroupDTO update(TaskGroupDTO taskGroupDTO) {
		log.debug("Request to Update TaskGroup : {}", taskGroupDTO);

		return taskGroupRepository.findOneByPid(taskGroupDTO.getPid()).map(taskGroup -> {taskGroup.setName(taskGroupDTO.getName());
			taskGroup.setAlias(taskGroupDTO.getAlias());
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AG_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get one by pid";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			taskGroup.setActivityGroup(activityGroupRepository.findOneByPid(taskGroupDTO.getActivityGroupPid()).get());
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

			taskGroup.setDescription(taskGroupDTO.getDescription());
			taskGroup = taskGroupRepository.save(taskGroup);
			TaskGroupDTO result = taskGroupMapper.taskGroupToTaskGroupDTO(taskGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the taskGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskGroup> findAll(Pageable pageable) {
		log.debug("Request to get all TaskGroups");
		Page<TaskGroup> result = taskGroupRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the taskGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<TaskGroupDTO> findAllByCompany() {
		log.debug("Request to get all TaskGroups");
		List<TaskGroup> taskGroupList = taskGroupRepository.findAllByCompanyId();
		List<TaskGroupDTO> result = taskGroupMapper.taskGroupsToTaskGroupDTOs(taskGroupList);
		return result;
	}

	/**
	 * Get all the taskGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all TaskGroups");
		Page<TaskGroup> taskGroups = taskGroupRepository.findAllByCompanyId(pageable);
		Page<TaskGroupDTO> result = new PageImpl<TaskGroupDTO>(taskGroupMapper.taskGroupsToTaskGroupDTOs(taskGroups
				.getContent()), pageable, taskGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one taskGroup by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public TaskGroupDTO findOne(Long id) {
		log.debug("Request to get TaskGroup : {}", id);
		TaskGroup taskGroup = taskGroupRepository.findOne(id);
		TaskGroupDTO taskGroupDTO = taskGroupMapper.taskGroupToTaskGroupDTO(taskGroup);
		return taskGroupDTO;
	}

	/**
	 * Get one taskGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get TaskGroup by pid : {}", pid);
		return taskGroupRepository.findOneByPid(pid).map(taskGroup -> {
			TaskGroupDTO taskGroupDTO = taskGroupMapper.taskGroupToTaskGroupDTO(taskGroup);
			return taskGroupDTO;
		});
	}

	/**
	 * Get one taskGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskGroupDTO> findByName(String name) {
		log.debug("Request to get TaskGroup by name : {}", name);
		return taskGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(taskGroup -> {
					TaskGroupDTO taskGroupDTO = taskGroupMapper.taskGroupToTaskGroupDTO(taskGroup);
					return taskGroupDTO;
				});
	}

	/**
	 * Delete the taskGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete TaskGroup : {}", pid);
		taskGroupRepository.findOneByPid(pid).ifPresent(taskGroup -> {
			taskGroupRepository.delete(taskGroup.getId());
		});
	}

	/**
	 * Get one taskGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskGroupDTO> findByNameAndPid(String taskGroupName, String pid) {
		log.debug("Request to get TaskGroup by name And Pid: {}", taskGroupName +"   "+pid);
		return taskGroupRepository.findByNameIgnoreCaseAndPid(taskGroupName,pid).map(taskGroup -> {
					TaskGroupDTO taskGroupDTO = taskGroupMapper.taskGroupToTaskGroupDTO(taskGroup);
					return taskGroupDTO;
				});
	}

}
