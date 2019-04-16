package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.TaskUserNotificationSetting;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskNotificationSettingRepository;
import com.orderfleet.webapp.repository.TaskUserNotificationSettingRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskUserNotificationSettingService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.TaskUserNotificationSettingDTO;



/**
 *ServiceImpl for TaskUserNotificationSetting
 *
 * @author fahad
 * @since May 31, 2017
 */
@Service
@Transactional
public class TaskUserNotificationSettingServiceImpl implements TaskUserNotificationSettingService{

	private final Logger log = LoggerFactory.getLogger(TaskUserNotificationSettingServiceImpl.class);
	
	@Inject
	private TaskUserNotificationSettingRepository taskUserNotificationSettingRepository;
	
	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private TaskNotificationSettingRepository taskNotificationSettingRepository;
	
	/**
	 * Save a taskUserNotificationSetting.
	 * 
	 * @param taskUserNotificationSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public TaskUserNotificationSettingDTO save(TaskUserNotificationSettingDTO taskUserNotificationSettingDTO) {
		log.debug("Request to save TaskUserNotificationSetting : {}", taskUserNotificationSettingDTO);

		TaskUserNotificationSetting taskUserNotificationSetting = new TaskUserNotificationSetting();
		// set pid
		taskUserNotificationSetting.setPid(TaskUserNotificationSettingService.PID_PREFIX + RandomUtil.generatePid());

		taskUserNotificationSetting.setExecutor(userRepository.findOneByPid(taskUserNotificationSettingDTO.getExecutorPid()).get());
		taskUserNotificationSetting
				.setTaskNotificationSetting(taskNotificationSettingRepository.findOneByPid(taskUserNotificationSettingDTO.getTaskNotificationSettingPid()).get());
		taskUserNotificationSetting.setApprovers(new ArrayList<>());
		for (UserDTO userDTO : taskUserNotificationSettingDTO.getApprovers()) {
			taskUserNotificationSetting.getApprovers().add(userRepository.findOneByPid(userDTO.getPid()).get());
		}
		taskUserNotificationSetting.setEnableTerritory(taskUserNotificationSettingDTO.getEnableTerritory());
		// set company
		taskUserNotificationSetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taskUserNotificationSetting = taskUserNotificationSettingRepository.save(taskUserNotificationSetting);
		TaskUserNotificationSettingDTO result = new TaskUserNotificationSettingDTO(taskUserNotificationSetting);
		return result;
	}

	/**
	 * Update a taskUserNotificationSetting.
	 * 
	 * @param taskUserNotificationSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public TaskUserNotificationSettingDTO update(TaskUserNotificationSettingDTO taskUserNotificationSettingDTO) {
		log.debug("Request to Update TaskUserNotificationSetting : {}", taskUserNotificationSettingDTO);
		return taskUserNotificationSettingRepository.findOneByPid(taskUserNotificationSettingDTO.getPid()).map(taskUserNotificationSetting -> {
			taskUserNotificationSetting.setExecutor(userRepository.findOneByPid(taskUserNotificationSettingDTO.getExecutorPid()).get());
			taskUserNotificationSetting
					.setTaskNotificationSetting(taskNotificationSettingRepository.findOneByPid(taskUserNotificationSettingDTO.getTaskNotificationSettingPid()).get());
			taskUserNotificationSetting.setApprovers(new ArrayList<>());
			for (UserDTO userDTO : taskUserNotificationSettingDTO.getApprovers()) {
				taskUserNotificationSetting.getApprovers().add(userRepository.findOneByPid(userDTO.getPid()).get());
			}
			taskUserNotificationSetting.setEnableTerritory(taskUserNotificationSettingDTO.getEnableTerritory());
			taskUserNotificationSetting = taskUserNotificationSettingRepository.save(taskUserNotificationSetting);
			TaskUserNotificationSettingDTO result = new TaskUserNotificationSettingDTO(taskUserNotificationSetting);
			return result;
		}).orElse(null);
	}

	
	
	/**
	 * Get one taskUserNotificationSetting by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskUserNotificationSettingDTO> findOneByPid(String pid) {
		log.debug("Request to get TaskUserNotificationSetting by pid : {}", pid);
		return taskUserNotificationSettingRepository.findOneByPid(pid).map(taskUserNotificationSetting -> {
			TaskUserNotificationSettingDTO result = new TaskUserNotificationSettingDTO(taskUserNotificationSetting);
			return result;
		});
	}

	/**
	 * Delete the taskUserNotificationSetting by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	@Override
	public void delete(String pid) {
		log.debug("Request to delete taskUserNotificationSetting : {}", pid);
		taskUserNotificationSettingRepository.findOneByPid(pid).ifPresent(taskUserNotificationSetting -> {
			taskUserNotificationSettingRepository.delete(taskUserNotificationSetting.getId());
		});	
	}

	/**
	 * get a taskUserNotificationSetting by executivePid and taskNotificationSettingPid.
	 * 
	 * @param executivePid
	 *            the executivePid of the entity
	 *            
	 * @param taskNotificationSettingPid
	 *            the taskNotificationSettingPid of the entity
	 */
	@Override
	public TaskUserNotificationSetting findByExecutorPidAndTaskNotificationSettingPid(String executivePid,
			String taskNotificationSettingPid) {
		log.debug("Request to get  TaskUserNotificationSettings: {}",executivePid);
		return taskUserNotificationSettingRepository.findByExecutorPidAndTaskNotificationSettingPid(executivePid, taskNotificationSettingPid);
	}

	/**
	 * get all taskUserNotificationSetting by companyId.
	 * 
	 *return list of taskUserNotificationSettingDTO
	 */
	@Override
	public List<TaskUserNotificationSettingDTO> findAllByCompanyId() {
		
		log.debug("Request to get all TaskUserNotificationSettings");
		List<TaskUserNotificationSetting> taskUserNotificationSettingList = taskUserNotificationSettingRepository.findAllByCompanyId();
		List<TaskUserNotificationSettingDTO> result = taskUserNotificationSettingList.stream().map(TaskUserNotificationSettingDTO::new)
				.collect(Collectors.toList());
		return result;
	}


}
