package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.TaskSetting;
import com.orderfleet.webapp.domain.enums.ActivityEvent;
import com.orderfleet.webapp.web.rest.dto.TaskSettingDTO;

/**
 * Service Interface for managing TaskSetting.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
public interface TaskSettingService {

	String PID_PREFIX = "TKST-";

	/**
	 * Save a taskSetting.
	 * 
	 * @param taskSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	TaskSettingDTO save(TaskSettingDTO taskSettingDTO);

	/**
	 * Update a taskSetting.
	 * 
	 * @param taskSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	TaskSettingDTO update(TaskSettingDTO taskSettingDTO);

	/**
	 * Get all the taskSettings.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskSetting> findAll(Pageable pageable);

	/**
	 * Get all the taskSettings.
	 * 
	 * @return the list of entities
	 */
	List<TaskSettingDTO> findAllByCompany();

	List<TaskSettingDTO> findByActivityPidAndDocumentPid(String activityPid, String documentPid);

	/**
	 * Get all the taskSettings of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskSettingDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" taskSetting.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	TaskSettingDTO findOne(Long id);

	/**
	 * Get the taskSetting by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<TaskSettingDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" taskSetting.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<TaskSetting> findByActivityPidAndDocumentPidAndActivityEvent(String activityPid, String documentPid,
			ActivityEvent activityEvent);
}
