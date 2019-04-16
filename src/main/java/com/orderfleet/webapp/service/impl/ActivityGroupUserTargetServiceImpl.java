package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.repository.ActivityGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityGroupUserTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupUserTargetMapper;

/**
 * Service Implementation for managing ActivityGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Service
@Transactional
public class ActivityGroupUserTargetServiceImpl implements ActivityGroupUserTargetService {

	private final Logger log = LoggerFactory.getLogger(ActivityGroupUserTargetServiceImpl.class);

	@Inject
	private ActivityGroupUserTargetRepository activityGroupUserTargetRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityGroupUserTargetMapper activityGroupUserTargetMapper;

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a activityGroupUserTarget.
	 * 
	 * @param activityGroupUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityGroupUserTargetDTO save(ActivityGroupUserTargetDTO activityGroupUserTargetDTO) {
		log.debug("Request to save ActivityGroupUserTarget : {}", activityGroupUserTargetDTO);

		// set pid
		activityGroupUserTargetDTO.setPid(ActivityGroupUserTargetService.PID_PREFIX + RandomUtil.generatePid());
		ActivityGroupUserTarget activityGroupUserTarget = activityGroupUserTargetMapper
				.activityGroupUserTargetDTOToActivityGroupUserTarget(activityGroupUserTargetDTO);
		// set company
		activityGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityGroupUserTarget = activityGroupUserTargetRepository.save(activityGroupUserTarget);
		ActivityGroupUserTargetDTO result = activityGroupUserTargetMapper
				.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
		return result;
	}

	/**
	 * Update a activityGroupUserTarget.
	 * 
	 * @param activityGroupUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityGroupUserTargetDTO update(ActivityGroupUserTargetDTO activityGroupUserTargetDTO) {
		log.debug("Request to Update ActivityGroupUserTarget : {}", activityGroupUserTargetDTO);

		return activityGroupUserTargetRepository.findOneByPid(activityGroupUserTargetDTO.getPid())
				.map(activityGroupUserTarget -> {
					activityGroupUserTarget.setTargetNumber(activityGroupUserTargetDTO.getTargetNumber());
					activityGroupUserTarget.setStartDate(activityGroupUserTargetDTO.getStartDate());
					activityGroupUserTarget.setEndDate(activityGroupUserTargetDTO.getEndDate());
					activityGroupUserTarget.setActivityGroup(activityGroupRepository
							.findOneByPid(activityGroupUserTargetDTO.getActivityGroupPid()).get());
					activityGroupUserTarget
							.setUser(userRepository.findOneByPid(activityGroupUserTargetDTO.getUserPid()).get());
					activityGroupUserTarget = activityGroupUserTargetRepository.save(activityGroupUserTarget);
					ActivityGroupUserTargetDTO result = activityGroupUserTargetMapper
							.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ActivityGroupUserTarget> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ActivityGroupUserTarget> result = activityGroupUserTargetRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityGroupUserTargetDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<ActivityGroupUserTarget> activityGroupUserTargetList = activityGroupUserTargetRepository
				.findAllByCompanyId();
		List<ActivityGroupUserTargetDTO> result = activityGroupUserTargetMapper
				.activityGroupUserTargetsToActivityGroupUserTargetDTOs(activityGroupUserTargetList);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ActivityGroupUserTargetDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ActivityGroupUserTarget> activities = activityGroupUserTargetRepository.findAllByCompanyId(pageable);
		Page<ActivityGroupUserTargetDTO> result = new PageImpl<ActivityGroupUserTargetDTO>(activityGroupUserTargetMapper
				.activityGroupUserTargetsToActivityGroupUserTargetDTOs(activities.getContent()), pageable,
				activities.getTotalElements());
		return result;
	}

	/**
	 * Get one activityGroupUserTarget by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityGroupUserTargetDTO findOne(Long id) {
		log.debug("Request to get ActivityGroupUserTarget : {}", id);
		ActivityGroupUserTarget activityGroupUserTarget = activityGroupUserTargetRepository.findOne(id);
		ActivityGroupUserTargetDTO activityGroupUserTargetDTO = activityGroupUserTargetMapper
				.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
		return activityGroupUserTargetDTO;
	}

	/**
	 * Get one activityGroupUserTarget by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityGroupUserTargetDTO> findOneByPid(String pid) {
		log.debug("Request to get ActivityGroupUserTarget by pid : {}", pid);
		return activityGroupUserTargetRepository.findOneByPid(pid).map(activityGroupUserTarget -> {
			ActivityGroupUserTargetDTO activityGroupUserTargetDTO = activityGroupUserTargetMapper
					.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
			return activityGroupUserTargetDTO;
		});
	}

	/**
	 * Delete the activityGroupUserTarget by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ActivityGroupUserTarget : {}", pid);
		activityGroupUserTargetRepository.findOneByPid(pid).ifPresent(activityGroupUserTarget -> {
			activityGroupUserTargetRepository.delete(activityGroupUserTarget.getId());
		});
	}

}
