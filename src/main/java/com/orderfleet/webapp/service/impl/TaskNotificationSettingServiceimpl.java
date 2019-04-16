package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.TaskNotificationSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.TaskNotificationSettingRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaskNotificationSettingService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.TaskNotificationSettingDTO;

/**
 *Service impl for TaskNotificationSetting
 *
 * @author fahad
 * @since May 31, 2017
 */
@Service
@Transactional
public class TaskNotificationSettingServiceimpl implements TaskNotificationSettingService{

	private final Logger log = LoggerFactory.getLogger(TaskNotificationSettingServiceimpl.class);
	
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private TaskNotificationSettingRepository taskNotificationSettingRepository;
	
	@Override
	public TaskNotificationSettingDTO save(TaskNotificationSettingDTO taskNotificationSettingDTO) {
		log.debug("Request to save TaskNotificationSetting : {}", taskNotificationSettingDTO);
		TaskNotificationSetting taskNotificationSetting=new TaskNotificationSetting();
		taskNotificationSetting.setPid(TaskNotificationSettingService.PID_PREFIX + RandomUtil.generatePid());

		taskNotificationSetting.setActivity(activityRepository.findOneByPid(taskNotificationSettingDTO.getActivityPid()).get());
		taskNotificationSetting.setActivityEvent(taskNotificationSettingDTO.getActivityEvent());
		taskNotificationSetting.setDocument(documentRepository.findOneByPid(taskNotificationSettingDTO.getDocumentPid()).get());
		taskNotificationSetting.setTaskNotificationSettingColumns(taskNotificationSettingDTO.getNotificationSettingColumns());
		taskNotificationSetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taskNotificationSetting=taskNotificationSettingRepository.save(taskNotificationSetting);
		TaskNotificationSettingDTO result=new TaskNotificationSettingDTO(taskNotificationSetting);
		return result;
	}

	@Override
	public TaskNotificationSettingDTO update(TaskNotificationSettingDTO taskNotificationSettingDTO) {
		return taskNotificationSettingRepository.findOneByPid(taskNotificationSettingDTO.getPid()).map(taskNotificationSetting -> {
			taskNotificationSetting.setActivity(activityRepository.findOneByPid(taskNotificationSettingDTO.getActivityPid()).get());
			taskNotificationSetting.setActivityEvent(taskNotificationSettingDTO.getActivityEvent());
			taskNotificationSetting.setDocument(documentRepository.findOneByPid(taskNotificationSettingDTO.getDocumentPid()).get());
			taskNotificationSetting.setTaskNotificationSettingColumns(taskNotificationSettingDTO.getNotificationSettingColumns());
			taskNotificationSetting = taskNotificationSettingRepository.save(taskNotificationSetting);
			TaskNotificationSettingDTO result = new TaskNotificationSettingDTO(taskNotificationSetting);
			return result;
		}).orElse(null);
	}
	@Override
	public Optional<TaskNotificationSettingDTO> findByActivityPidAndDocumentPidAndActivityEvent(String activityPid,
			String documentPid, ActivityEvent activityEvent) {
		
		log.debug("Request to get TaskNotificationSetting by pid : {}", activityPid);
		return taskNotificationSettingRepository.findByActivityPidAndDocumentPidAndActivityEvent(activityPid, documentPid, activityEvent).map(taskNotificationSetting -> {
			TaskNotificationSettingDTO taskNotificationSettingDTO = new TaskNotificationSettingDTO(taskNotificationSetting);
			return taskNotificationSettingDTO;
		});
	}

	@Override
	public List<TaskNotificationSettingDTO> findAllByCompanyId() {
		List<TaskNotificationSetting> taskNotificationSettings=taskNotificationSettingRepository.findAllByCompanyId();
		List<TaskNotificationSettingDTO>taskNotificationSettingDTOs=new ArrayList<TaskNotificationSettingDTO>();
		for(TaskNotificationSetting taskNotificationSetting: taskNotificationSettings){
			TaskNotificationSettingDTO taskNotificationSettingDTO=new TaskNotificationSettingDTO(taskNotificationSetting);
			taskNotificationSettingDTOs.add(taskNotificationSettingDTO);
		}
		return taskNotificationSettingDTOs;
	}

	@Override
	public Optional<TaskNotificationSettingDTO> findOneByPid(String pid) {
		log.debug("Request to get TaskNotificationSetting by pid : {}", pid);
		return taskNotificationSettingRepository.findOneByPid(pid).map(taskNotificationSetting -> {
			TaskNotificationSettingDTO taskNotificationSettingDTO = new TaskNotificationSettingDTO(taskNotificationSetting);
			return taskNotificationSettingDTO;
		});
	}

	/**
	 * Delete the taskNotificationSetting by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete TaskNotificationSetting : {}", pid);
		taskNotificationSettingRepository.findOneByPid(pid).ifPresent(taskNotificationSetting -> {
			taskNotificationSettingRepository.delete(taskNotificationSetting.getId());
		});
	}

	@Override
	public List<TaskNotificationSettingDTO> findByActivityPidAndDocumentPid(String activityPid, String documentPid) {
		List<TaskNotificationSetting> taskNotificationSettings=taskNotificationSettingRepository.findByActivityPidAndDocumentPid(activityPid, documentPid);
		List<TaskNotificationSettingDTO>taskNotificationSettingDTOs=new ArrayList<TaskNotificationSettingDTO>();
		for(TaskNotificationSetting taskNotificationSetting: taskNotificationSettings){
			TaskNotificationSettingDTO taskNotificationSettingDTO=new TaskNotificationSettingDTO(taskNotificationSetting);
			taskNotificationSettingDTOs.add(taskNotificationSettingDTO);
		}
		return taskNotificationSettingDTOs;
	}

}
