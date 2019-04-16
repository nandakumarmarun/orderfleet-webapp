package com.orderfleet.webapp.service.impl;

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

import com.orderfleet.webapp.domain.TaskUserSetting;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskSettingRepository;
import com.orderfleet.webapp.repository.TaskUserSettingRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskUserSettingService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.TaskUserSettingDTO;

/**
 * Service Implementation for managing TaskUserSetting.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
@Service
@Transactional
public class TaskUserSettingServiceImpl implements TaskUserSettingService {

	private final Logger log = LoggerFactory.getLogger(TaskUserSettingServiceImpl.class);

	@Inject
	private TaskUserSettingRepository taskUserSettingRepository;

	@Inject
	private TaskSettingRepository taskSettingRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a taskUserSetting.
	 * 
	 * @param taskUserSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public TaskUserSettingDTO save(TaskUserSettingDTO taskUserSettingDTO) {
		log.debug("Request to save TaskUserSetting : {}", taskUserSettingDTO);

		TaskUserSetting taskUserSetting = new TaskUserSetting();
		// set pid
		taskUserSetting.setPid(TaskUserSettingService.PID_PREFIX + RandomUtil.generatePid());

		taskUserSetting.setExecutor(userRepository.findOneByPid(taskUserSettingDTO.getExecutorPid()).get());
		taskUserSetting
				.setTaskSetting(taskSettingRepository.findOneByPid(taskUserSettingDTO.getTaskSettingPid()).get());
		taskUserSetting.setApprovers(new ArrayList<>());
		for (UserDTO userDTO : taskUserSettingDTO.getApprovers()) {
			taskUserSetting.getApprovers().add(userRepository.findOneByPid(userDTO.getPid()).get());
		}
		// set company
		taskUserSetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taskUserSetting = taskUserSettingRepository.save(taskUserSetting);
		TaskUserSettingDTO result = new TaskUserSettingDTO(taskUserSetting);
		return result;
	}

	/**
	 * Update a taskUserSetting.
	 * 
	 * @param taskUserSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public TaskUserSettingDTO update(TaskUserSettingDTO taskUserSettingDTO) {
		log.debug("Request to Update TaskUserSetting : {}", taskUserSettingDTO);
		return taskUserSettingRepository.findOneByPid(taskUserSettingDTO.getPid()).map(taskUserSetting -> {
			taskUserSetting.setExecutor(userRepository.findOneByPid(taskUserSettingDTO.getExecutorPid()).get());
			taskUserSetting
					.setTaskSetting(taskSettingRepository.findOneByPid(taskUserSettingDTO.getTaskSettingPid()).get());
			taskUserSetting.setApprovers(new ArrayList<>());
			for (UserDTO userDTO : taskUserSettingDTO.getApprovers()) {
				taskUserSetting.getApprovers().add(userRepository.findOneByPid(userDTO.getPid()).get());
			}
			taskUserSetting = taskUserSettingRepository.save(taskUserSetting);
			TaskUserSettingDTO result = new TaskUserSettingDTO(taskUserSetting);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the taskUserSettings.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskUserSetting> findAll(Pageable pageable) {
		log.debug("Request to get all TaskUserSettings");
		Page<TaskUserSetting> result = taskUserSettingRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<TaskUserSettingDTO> findAllByCompany() {
		log.debug("Request to get all TaskUserSettings");
		List<TaskUserSetting> taskUserSettingList = taskUserSettingRepository.findAllByCompanyId();
		List<TaskUserSettingDTO> result = taskUserSettingList.stream().map(TaskUserSettingDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the taskUserSettings.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskUserSettingDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all TaskUserSettings");
		Page<TaskUserSetting> taskUserSettings = taskUserSettingRepository.findAllByCompanyId(pageable);
		List<TaskUserSettingDTO> taskUserSettingDTOs = taskUserSettings.getContent().stream()
				.map(TaskUserSettingDTO::new).collect(Collectors.toList());
		Page<TaskUserSettingDTO> result = new PageImpl<TaskUserSettingDTO>(taskUserSettingDTOs, pageable,
				taskUserSettings.getTotalElements());
		return result;
	}

	/**
	 * Get one taskUserSetting by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public TaskUserSettingDTO findOne(Long id) {
		log.debug("Request to get TaskUserSetting : {}", id);
		TaskUserSetting taskUserSetting = taskUserSettingRepository.findOne(id);
		TaskUserSettingDTO result = new TaskUserSettingDTO(taskUserSetting);
		return result;
	}

	/**
	 * Get one taskUserSetting by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskUserSettingDTO> findOneByPid(String pid) {
		log.debug("Request to get TaskUserSetting by pid : {}", pid);
		return taskUserSettingRepository.findOneByPid(pid).map(taskUserSetting -> {
			TaskUserSettingDTO result = new TaskUserSettingDTO(taskUserSetting);
			return result;
		});
	}

	/**
	 * Delete the taskUserSetting by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete TaskUserSetting : {}", pid);
		taskUserSettingRepository.findOneByPid(pid).ifPresent(taskUserSetting -> {
			taskUserSettingRepository.delete(taskUserSetting.getId());
		});
	}

	@Override
	public TaskUserSetting findByExecutorPidAndTaskSettingPid(String executorPid, String taskSettingPid) {
		return taskUserSettingRepository.findByExecutorPidAndTaskSettingPid(executorPid, taskSettingPid);
	}

}
