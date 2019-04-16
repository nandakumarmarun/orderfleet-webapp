package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
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

import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.ActivityUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityUserTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.MonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityUserTargetMapper;

/**
 * Service Implementation for managing ActivityUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
@Service
@Transactional
public class ActivityUserTargetServiceImpl implements ActivityUserTargetService {

	private final Logger log = LoggerFactory.getLogger(ActivityUserTargetServiceImpl.class);

	@Inject
	private ActivityUserTargetRepository activityUserTargetRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityUserTargetMapper activityUserTargetMapper;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a activityUserTarget.
	 * 
	 * @param activityUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityUserTargetDTO save(ActivityUserTargetDTO activityUserTargetDTO) {
		log.debug("Request to save ActivityUserTarget : {}", activityUserTargetDTO);

		// set pid
		activityUserTargetDTO.setPid(ActivityUserTargetService.PID_PREFIX + RandomUtil.generatePid());
		ActivityUserTarget activityUserTarget = activityUserTargetMapper
				.activityUserTargetDTOToActivityUserTarget(activityUserTargetDTO);
		// set company
		activityUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityUserTarget = activityUserTargetRepository.save(activityUserTarget);
		ActivityUserTargetDTO result = activityUserTargetMapper
				.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
		return result;
	}

	@Override
	public ActivityUserTargetDTO saveMonthlyTarget(MonthlyTargetDTO monthlyTargetDTO, LocalDate startDate,
			LocalDate endDate) {

		ActivityUserTarget activityUserTarget = new ActivityUserTarget();
		// set pid
		activityUserTarget.setPid(ActivityUserTargetService.PID_PREFIX + RandomUtil.generatePid());
		activityUserTarget.setActivity(activityRepository.findOneByPid(monthlyTargetDTO.getActivityPid()).get());
		// set company
		activityUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityUserTarget.setStartDate(startDate);
		activityUserTarget.setEndDate(endDate);
		activityUserTarget.setTargetNumber(monthlyTargetDTO.getTarget());
		activityUserTarget.setUser(userRepository.findOneByPid(monthlyTargetDTO.getUserPid()).get());

		activityUserTarget = activityUserTargetRepository.save(activityUserTarget);
		ActivityUserTargetDTO result = activityUserTargetMapper
				.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
		return result;
	}

	/**
	 * Update a activityUserTarget.
	 * 
	 * @param activityUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityUserTargetDTO update(ActivityUserTargetDTO activityUserTargetDTO) {
		log.debug("Request to Update ActivityUserTarget : {}", activityUserTargetDTO);

		return activityUserTargetRepository.findOneByPid(activityUserTargetDTO.getPid()).map(activityUserTarget -> {
			activityUserTarget.setStartDate(activityUserTargetDTO.getStartDate());
			activityUserTarget.setEndDate(activityUserTargetDTO.getEndDate());
			activityUserTarget
					.setActivity(activityRepository.findOneByPid(activityUserTargetDTO.getActivityPid()).get());
			activityUserTarget.setUser(userRepository.findOneByPid(activityUserTargetDTO.getUserPid()).get());
			activityUserTarget.setTargetNumber(activityUserTargetDTO.getTargetNumber());
			activityUserTarget = activityUserTargetRepository.save(activityUserTarget);
			ActivityUserTargetDTO result = activityUserTargetMapper
					.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
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
	public Page<ActivityUserTarget> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ActivityUserTarget> result = activityUserTargetRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityUserTargetDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<ActivityUserTarget> activityUserTargetList = activityUserTargetRepository.findAllByCompanyId();
		List<ActivityUserTargetDTO> result = activityUserTargetMapper
				.activityUserTargetsToActivityUserTargetDTOs(activityUserTargetList);
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
	public Page<ActivityUserTargetDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ActivityUserTarget> activities = activityUserTargetRepository.findAllByCompanyId(pageable);
		Page<ActivityUserTargetDTO> result = new PageImpl<ActivityUserTargetDTO>(
				activityUserTargetMapper.activityUserTargetsToActivityUserTargetDTOs(activities.getContent()), pageable,
				activities.getTotalElements());
		return result;
	}

	/**
	 * Get one activityUserTarget by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityUserTargetDTO findOne(Long id) {
		log.debug("Request to get ActivityUserTarget : {}", id);
		ActivityUserTarget activityUserTarget = activityUserTargetRepository.findOne(id);
		ActivityUserTargetDTO activityUserTargetDTO = activityUserTargetMapper
				.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
		return activityUserTargetDTO;
	}

	/**
	 * Get one activityUserTarget by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityUserTargetDTO> findOneByPid(String pid) {
		log.debug("Request to get ActivityUserTarget by pid : {}", pid);
		return activityUserTargetRepository.findOneByPid(pid).map(activityUserTarget -> {
			ActivityUserTargetDTO activityUserTargetDTO = activityUserTargetMapper
					.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
			return activityUserTargetDTO;
		});
	}

	/**
	 * Delete the activityUserTarget by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ActivityUserTarget : {}", pid);
		activityUserTargetRepository.findOneByPid(pid).ifPresent(activityUserTarget -> {
			activityUserTargetRepository.delete(activityUserTarget.getId());
		});
	}

}
