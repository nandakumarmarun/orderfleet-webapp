package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityType;
import com.orderfleet.webapp.domain.ActivityTypeActivity;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.ActivityTypeActivityRepository;
import com.orderfleet.webapp.repository.ActivityTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityTypeService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityTypeMapper;

@Service
@Transactional
public class ActivityTypeServiceImpl implements ActivityTypeService {

	private final Logger log = LoggerFactory.getLogger(ActivityTypeServiceImpl.class);

	@Inject
	private ActivityTypeRepository activityTypeRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityTypeMapper activityTypeMapper;
	
	@Inject
	private ActivityTypeActivityRepository activityTypeActivityRepository;
	
	@Inject
	private ActivityMapper activityMapper;
	
	@Inject
	private ActivityRepository activityRepository;

	/**
	 * Save a activityType.
	 * 
	 * @param activityTypeDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityTypeDTO save(ActivityTypeDTO activityTypeDTO) {
		log.debug("Request to save ActivityType : {}", activityTypeDTO);
		activityTypeDTO.setPid(ActivityTypeService.PID_PREFIX + RandomUtil.generatePid()); // set
		ActivityType activityType = activityTypeMapper.activityTypeDTOToActivityType(activityTypeDTO);
		activityType.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityType = activityTypeRepository.save(activityType);
		ActivityTypeDTO result = activityTypeMapper.activityTypeToActivityTypeDTO(activityType);
		return result;
	}

	/**
	 * Update a activityType.
	 * 
	 * @param activityTypeDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityTypeDTO update(ActivityTypeDTO activityTypeDTO) {
		log.debug("Request to Update ActivityType : {}", activityTypeDTO);
		return activityTypeRepository.findOneByPid(activityTypeDTO.getPid()).map(activityType -> {
			activityType.setName(activityTypeDTO.getName());
			activityType.setAlias(activityTypeDTO.getAlias());
			activityType.setDescription(activityTypeDTO.getDescription());

			activityType = activityTypeRepository.save(activityType);
			ActivityTypeDTO result = activityTypeMapper.activityTypeToActivityTypeDTO(activityType);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the activityTypes.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityTypeDTO> findAllByCompany() {
		log.debug("Request to get all ActivityTypes");
		List<ActivityType> activityTypeList = activityTypeRepository.findAllByCompanyId();
		List<ActivityTypeDTO> result = activityTypeMapper.activityTypesToActivityTypeDTOs(activityTypeList);
		return result;
	}

	/**
	 * Get one activityType by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityTypeDTO findOne(Long id) {
		log.debug("Request to get ActivityType : {}", id);
		ActivityType activityType = activityTypeRepository.findOne(id);
		ActivityTypeDTO activityTypeDTO = activityTypeMapper.activityTypeToActivityTypeDTO(activityType);
		return activityTypeDTO;
	}

	/**
	 * Get one activityType by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityTypeDTO> findOneByPid(String pid) {
		log.debug("Request to get ActivityType by pid : {}", pid);
		return activityTypeRepository.findOneByPid(pid).map(activityType -> {
			ActivityTypeDTO activityTypeDTO = activityTypeMapper.activityTypeToActivityTypeDTO(activityType);
			return activityTypeDTO;
		});
	}

	/**
	 * Get one activityType by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityTypeDTO> findByName(String name) {
		log.debug("Request to get ActivityType by name : {}", name);
		return activityTypeRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(activityType -> {
					ActivityTypeDTO activityTypeDTO = activityTypeMapper.activityTypeToActivityTypeDTO(activityType);
					return activityTypeDTO;
				});
	}

	/**
	 * Delete the activityType by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	@Override
	public void delete(String pid) {
		log.debug("Request to delete ActivityType : {}", pid);
		activityTypeRepository.findOneByPid(pid).ifPresent(activityType -> {
			activityTypeRepository.delete(activityType.getId());
		});
	}

	/**
	 * 
	 * Update the ActivityType status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public ActivityTypeDTO updateActivityTypeStatus(String pid, boolean activate) {
		log.debug("Request to update activityType status: {}");
		return activityTypeRepository.findOneByPid(pid).map(activityType -> {
			activityType.setActivated(activate);
			activityType = activityTypeRepository.save(activityType);
			ActivityTypeDTO result = activityTypeMapper.activityTypeToActivityTypeDTO(activityType);
			return result;
		}).orElse(null);
	}

	/**
	 * 
	 * find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<ActivityTypeDTO> findAllByCompanyAndDeactivatedActivityType(boolean deactive) {
		log.debug("Request to get Deactivated ActivityType ");
		List<ActivityType> activityTypes = activityTypeRepository.findAllByCompanyAndDeactivatedActivityType(deactive);
		List<ActivityTypeDTO> activityTypeDTOs = activityTypeMapper.activityTypesToActivityTypeDTOs(activityTypes);
		return activityTypeDTOs;
	}

	@Override
	public List<ActivityDTO> findAllActivityByActivityTypePid(String activityTypePid) {
		List<Activity> activities=activityTypeActivityRepository.findActivityByActivityTypePid(activityTypePid);
		 List<ActivityDTO> activityDTOs=activityMapper.activitiesToActivityDTOs(activities);
		return activityDTOs;
	}

	@Override
	public void saveActivityTypeActivity(String activityTypePid, String assignedActivities) {
		activityTypeActivityRepository.deleteByActivityTypePid(activityTypePid);
		String[] activities = assignedActivities.split(",");
		List<ActivityTypeActivity>activityTypeActivities=new ArrayList<>();
		ActivityType activityType=activityTypeRepository.findOneByPid(activityTypePid).get();
		for (String activityPid : activities) {
			ActivityTypeActivity activityTypeActivity=new ActivityTypeActivity();
				activityTypeActivity.setActivity(activityRepository.findOneByPid(activityPid).get());
			activityTypeActivity.setActivityType(activityType);
			activityTypeActivity.setCompany(activityType.getCompany());
			activityTypeActivities.add(activityTypeActivity);
		}
		activityTypeActivityRepository.save(activityTypeActivities);
	}

	@Override
	public void deleteActivityTypeActivityByActivityTypePid(String activityTypePid) {
		activityTypeActivityRepository.deleteByActivityTypePid(activityTypePid);
	}

}
