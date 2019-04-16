package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.TaskUserSetting;
import com.orderfleet.webapp.web.rest.dto.TaskUserSettingDTO;

/**
 * Service Interface for managing TaskUserSetting.
 * 
 * @author Muhammed Riyas T
 * @since October 04, 2016
 */
public interface TaskUserSettingService {

	String PID_PREFIX = "TUST-";

	/**
	 * Save a taskUserSetting.
	 * 
	 * @param taskUserSettingDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	TaskUserSettingDTO save(TaskUserSettingDTO taskUserSettingDTO);

	/**
	 * Update a taskUserSetting.
	 * 
	 * @param taskUserSettingDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	TaskUserSettingDTO update(TaskUserSettingDTO taskUserSettingDTO);

	/**
	 * Get all the taskUserSettings.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskUserSetting> findAll(Pageable pageable);

	/**
	 * Get all the taskUserSettings.
	 * 
	 * @return the list of entities
	 */
	List<TaskUserSettingDTO> findAllByCompany();

	/**
	 * Get all the taskUserSettings of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<TaskUserSettingDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" taskUserSetting.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	TaskUserSettingDTO findOne(Long id);

	/**
	 * Get the taskUserSetting by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<TaskUserSettingDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" taskUserSetting.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	TaskUserSetting findByExecutorPidAndTaskSettingPid(String executorPid, String taskSettingPid);
}
