package com.orderfleet.webapp.service.impl;

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

import com.orderfleet.webapp.domain.TaskSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.TaskSettingRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskSettingService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.TaskSettingDTO;

/**
 * Service Implementation for managing TaskSetting.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
@Service
@Transactional
public class TaskSettingServiceImpl implements TaskSettingService {

	private final Logger log = LoggerFactory.getLogger(TaskSettingServiceImpl.class);

	@Inject
	private TaskSettingRepository taskSettingRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private DocumentRepository documentRepository;

	/**
	 * Save a taskSetting.
	 * 
	 * @param taskSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public TaskSettingDTO save(TaskSettingDTO taskSettingDTO) {
		log.debug("Request to save TaskSetting : {}", taskSettingDTO);

		TaskSetting taskSetting = new TaskSetting();
		// set pid
		taskSetting.setPid(TaskSettingService.PID_PREFIX + RandomUtil.generatePid());

		taskSetting.setActivity(activityRepository.findOneByPid(taskSettingDTO.getActivityPid()).get());
		taskSetting.setActivityEvent(taskSettingDTO.getActivityEvent());
		taskSetting.setDocument(documentRepository.findOneByPid(taskSettingDTO.getDocumentPid()).get());
		taskSetting.setTaskActivity(activityRepository.findOneByPid(taskSettingDTO.getTaskActivityPid()).get());
		taskSetting.setFormElementPid(taskSettingDTO.getFormElementPid());
		taskSetting.setStartDateColumn(taskSettingDTO.getStartDateColumn());
		taskSetting.setScript(taskSettingDTO.getScript());
		taskSetting.setRequired(taskSettingDTO.getRequired());
		taskSetting.setCreatePlan(taskSettingDTO.getCreatePlan());

		// set company
		taskSetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taskSetting = taskSettingRepository.save(taskSetting);
		TaskSettingDTO result = new TaskSettingDTO(taskSetting);
		return result;
	}

	/**
	 * Update a taskSetting.
	 * 
	 * @param taskSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public TaskSettingDTO update(TaskSettingDTO taskSettingDTO) {
		log.debug("Request to Update TaskSetting : {}", taskSettingDTO);

		return taskSettingRepository.findOneByPid(taskSettingDTO.getPid()).map(taskSetting -> {
			taskSetting.setActivity(activityRepository.findOneByPid(taskSettingDTO.getActivityPid()).get());
			taskSetting.setActivityEvent(taskSettingDTO.getActivityEvent());
			taskSetting.setDocument(documentRepository.findOneByPid(taskSettingDTO.getDocumentPid()).get());
			taskSetting.setTaskActivity(activityRepository.findOneByPid(taskSettingDTO.getTaskActivityPid()).get());
			taskSetting.setFormElementPid(taskSettingDTO.getFormElementPid());
			taskSetting.setStartDateColumn(taskSettingDTO.getStartDateColumn());
			taskSetting.setScript(taskSettingDTO.getScript());
			taskSetting.setRequired(taskSettingDTO.getRequired());
			taskSetting.setCreatePlan(taskSettingDTO.getCreatePlan());

			taskSetting = taskSettingRepository.save(taskSetting);
			TaskSettingDTO result = new TaskSettingDTO(taskSetting);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the taskSettings.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskSetting> findAll(Pageable pageable) {
		log.debug("Request to get all TaskSettings");
		Page<TaskSetting> result = taskSettingRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<TaskSettingDTO> findAllByCompany() {
		log.debug("Request to get all TaskSettings");
		List<TaskSetting> taskSettingList = taskSettingRepository.findAllByCompanyId();
		List<TaskSettingDTO> result = taskSettingList.stream().map(TaskSettingDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<TaskSettingDTO> findByActivityPidAndDocumentPid(String activityPid, String documentPid) {
		log.debug("Request to get all TaskSettings");
		List<TaskSetting> taskSettingList = taskSettingRepository.findByActivityPidAndDocumentPid(activityPid,
				documentPid);
		List<TaskSettingDTO> result = taskSettingList.stream().map(TaskSettingDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the taskSettings.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<TaskSettingDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all TaskSettings");
		Page<TaskSetting> taskSettings = taskSettingRepository.findAllByCompanyId(pageable);
		List<TaskSettingDTO> taskSettingDTOs = taskSettings.getContent().stream().map(TaskSettingDTO::new)
				.collect(Collectors.toList());
		Page<TaskSettingDTO> result = new PageImpl<TaskSettingDTO>(taskSettingDTOs, pageable,
				taskSettings.getTotalElements());
		return result;
	}

	/**
	 * Get one taskSetting by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public TaskSettingDTO findOne(Long id) {
		log.debug("Request to get TaskSetting : {}", id);
		TaskSetting taskSetting = taskSettingRepository.findOne(id);
		TaskSettingDTO taskSettingDTO = new TaskSettingDTO(taskSetting);
		return taskSettingDTO;
	}

	/**
	 * Get one taskSetting by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<TaskSettingDTO> findOneByPid(String pid) {
		log.debug("Request to get TaskSetting by pid : {}", pid);
		return taskSettingRepository.findOneByPid(pid).map(taskSetting -> {
			TaskSettingDTO taskSettingDTO = new TaskSettingDTO(taskSetting);
			return taskSettingDTO;
		});
	}

	/**
	 * Delete the taskSetting by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete TaskSetting : {}", pid);
		taskSettingRepository.findOneByPid(pid).ifPresent(taskSetting -> {
			taskSettingRepository.delete(taskSetting.getId());
		});
	}

	@Override
	public List<TaskSetting> findByActivityPidAndDocumentPidAndActivityEvent(String activityPid, String documentPid,
			ActivityEvent activityEvent) {
		return taskSettingRepository.findByActivityPidAndDocumentPidAndActivityEvent(activityPid, documentPid,
				activityEvent);
	}

}
