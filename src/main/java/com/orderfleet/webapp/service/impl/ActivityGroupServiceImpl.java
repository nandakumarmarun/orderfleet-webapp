package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityGroup;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserActivityGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;

/**
 * Service Implementation for managing ActivityGroup.
 * 
 * @author Muhammed Riyas T
 * @since June 09, 2016
 */
@Service
@Transactional
public class ActivityGroupServiceImpl implements ActivityGroupService {

	private final Logger log = LoggerFactory.getLogger(ActivityGroupServiceImpl.class);

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityGroupMapper activityGroupMapper;

	@Inject
	private ActivityMapper activityMapper;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private UserActivityGroupRepository userActivityGroupRepository;

	/**
	 * Save a activityGroup.
	 * 
	 * @param activityGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityGroupDTO save(ActivityGroupDTO activityGroupDTO) {
		log.debug("Request to save ActivityGroup : {}", activityGroupDTO);

		// set pid
		activityGroupDTO.setPid(ActivityGroupService.PID_PREFIX + RandomUtil.generatePid());
		ActivityGroup activityGroup = activityGroupMapper.activityGroupDTOToActivityGroup(activityGroupDTO);
		// set company
		activityGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityGroup = activityGroupRepository.save(activityGroup);
		ActivityGroupDTO result = activityGroupMapper.activityGroupToActivityGroupDTO(activityGroup);
		return result;
	}

	@Override
	public void saveAssignedActivities(String pid, String assignedActivities) {
		ActivityGroup activityGroup = activityGroupRepository.findOneByPid(pid).get();
		String[] activities = assignedActivities.split(",");
		Set<Activity> listActivities = new HashSet<Activity>();
		for (String activityPid : activities) {
			Activity activity = activityRepository.findOneByPid(activityPid).get();
			listActivities.add(activity);
		}
		activityGroup.setActivities(listActivities);
		activityGroupRepository.save(activityGroup);
	}

	/**
	 * Update a activityGroup.
	 * 
	 * @param activityGroup
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityGroupDTO update(ActivityGroupDTO activityGroupDTO) {
		log.debug("Request to Update ActivityGroup : {}", activityGroupDTO);
		return activityGroupRepository.findOneByPid(activityGroupDTO.getPid()).map(activityGroup -> {
			activityGroup.setName(activityGroupDTO.getName());
			activityGroup.setAlias(activityGroupDTO.getAlias());
			activityGroup.setDescription(activityGroupDTO.getDescription());
			activityGroup = activityGroupRepository.save(activityGroup);
			ActivityGroupDTO result = activityGroupMapper.activityGroupToActivityGroupDTO(activityGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the activityGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ActivityGroup> findAll(Pageable pageable) {
		log.debug("Request to get all ActivityGroups");
		Page<ActivityGroup> result = activityGroupRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activityGroups.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityGroupDTO> findAllByCompany() {
		log.debug("Request to get all ActivityGroups");
		List<ActivityGroup> activityGroups = activityGroupRepository.findAllByCompanyId();
		List<ActivityGroupDTO> result = activityGroupMapper.activityGroupsToActivityGroupDTOs(activityGroups);
		return result;
	}

	/**
	 * Get all the activityGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ActivityGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ActivityGroups");
		Page<ActivityGroup> activityGroups = activityGroupRepository.findAllByCompanyId(pageable);
		Page<ActivityGroupDTO> result = new PageImpl<ActivityGroupDTO>(
				activityGroupMapper.activityGroupsToActivityGroupDTOs(activityGroups.getContent()), pageable,
				activityGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one activityGroup by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityGroupDTO findOne(Long id) {
		log.debug("Request to get ActivityGroup : {}", id);
		ActivityGroup activityGroup = activityGroupRepository.findOne(id);
		ActivityGroupDTO activityGroupDTO = activityGroupMapper.activityGroupToActivityGroupDTO(activityGroup);
		return activityGroupDTO;
	}

	/**
	 * Get one activityGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get ActivityGroup by pid : {}", pid);
		return activityGroupRepository.findOneByPid(pid).map(activityGroup -> {
			ActivityGroupDTO activityGroupDTO = activityGroupMapper.activityGroupToActivityGroupDTO(activityGroup);
			return activityGroupDTO;
		});
	}

	/**
	 * Get one activityGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityGroupDTO> findByName(String name) {
		log.debug("Request to get ActivityGroup by name : {}", name);
		return activityGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(activityGroup -> {
					ActivityGroupDTO activityGroupDTO = activityGroupMapper
							.activityGroupToActivityGroupDTO(activityGroup);
					return activityGroupDTO;
				});
	}

	/**
	 * Delete the activityGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ActivityGroup : {}", pid);
		activityGroupRepository.findOneByPid(pid).ifPresent(activityGroup -> {
			activityGroupRepository.delete(activityGroup.getId());
		});
	}

	@Override
	public List<ActivityDTO> findActivityGroupActivitiesByPid(String pid) {
		log.debug("Request to get all ActivityGroups");
		List<Activity> activities = activityGroupRepository.findActivityGroupActivitiesByPid(pid);
		List<ActivityDTO> result = activityMapper.activitiesToActivityDTOs(activities);
		return result;
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 6, 2017
	 * 
	 *        Update Status of activityGroup by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public ActivityGroupDTO updateActivityGroupActive(String pid, boolean active) {
		log.debug("Request to update Status of Activity Group", pid);
		return activityGroupRepository.findOneByPid(pid).map(activityGroup -> {
			if(!active){
				Set<Activity> listActivities = null;
				activityGroup.setActivities(listActivities);
			}
			activityGroup.setActivated(active);
			activityGroup = activityGroupRepository.save(activityGroup);
			ActivityGroupDTO result = activityGroupMapper.activityGroupToActivityGroupDTO(activityGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all activityGroupDTOs from ActivityGroup by status and
	 *        company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public List<ActivityGroupDTO> findActivityGroupsByUserIsCurrentUserAndActivityGroupActivated(boolean active) {
		log.debug("request to get all activated activitygroups");
		List<ActivityGroup> activityGroups = userActivityGroupRepository
				.findActivityGroupsByUserIsCurrentUserAndActivityGroupActivated(active);
		List<ActivityGroupDTO> activityGroupDTOs = activityGroupMapper
				.activityGroupsToActivityGroupDTOs(activityGroups);
		return activityGroupDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<ActivityGroupDTO> findAllByCompanyIdAndActivatedActivityGroupOrderByName(Pageable pageable,
			boolean active) {
		log.debug("request to get all activated activitygroups");
		Page<ActivityGroup> pageActivityGroup = activityGroupRepository
				.findAllByCompanyIdAndActivatedActivityGroupOrderByName(pageable, active);
		Page<ActivityGroupDTO> pageActivityGroupDTO = new PageImpl<ActivityGroupDTO>(
				activityGroupMapper.activityGroupsToActivityGroupDTOs(pageActivityGroup.getContent()), pageable,
				pageActivityGroup.getTotalElements());
		return pageActivityGroupDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<ActivityGroupDTO> findAllByCompanyIdAndDeactivatedActivityGroup(boolean deactive) {
		log.debug("request to get all deactivated activitygroups");
		List<ActivityGroup> activityGroups = activityGroupRepository
				.findAllByCompanyIdAndDeactivatedActivityGroup(deactive);
		List<ActivityGroupDTO> activityGroupDTOs = activityGroupMapper
				.activityGroupsToActivityGroupDTOs(activityGroups);
		return activityGroupDTOs;
	}

	
	/**
	 * 
	 */
	@Override
	public List<ActivityGroupDTO> findActivityGroupsByUserIsCurrentUserAndActivityGroupActivatedAndLastModifiedDate(boolean active,LocalDateTime lastModifiedDate) {
		log.debug("request to get all activated activitygroups");
		List<ActivityGroup> activityGroups = userActivityGroupRepository
				.findActivityGroupsByUserIsCurrentUserAndActivityGroupActivatedAndLastModifiedDate(active,lastModifiedDate);
		List<ActivityGroupDTO> activityGroupDTOs = activityGroupMapper
				.activityGroupsToActivityGroupDTOs(activityGroups);
		return activityGroupDTOs;
	}
}
