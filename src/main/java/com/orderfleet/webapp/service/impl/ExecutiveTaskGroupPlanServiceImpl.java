package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.ExecutiveTaskGroupPlan;
import com.orderfleet.webapp.domain.TaskGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskGroupPlanRepository;
import com.orderfleet.webapp.repository.TaskGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveTaskGroupPlanService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskGroupPlanDTO;
import com.orderfleet.webapp.web.rest.mapper.ExecutiveTaskGroupPlanMapper;

/**
 * Service Implementation for managing ExecutiveTaskGroupPlan.
 * 
 * @author Sarath
 * @since July 23, 2016
 */
@Service
@Transactional
public class ExecutiveTaskGroupPlanServiceImpl implements ExecutiveTaskGroupPlanService {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskGroupPlanServiceImpl.class);

	@Inject
	private ExecutiveTaskGroupPlanRepository executiveTaskGroupPlanRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ExecutiveTaskGroupPlanMapper executiveTaskGroupPlanMapper;

	@Inject
	private TaskGroupRepository taskGroupRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a executiveTaskGroupPlan.
	 * 
	 * @param executiveTaskGroupPlanDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskGroupPlanDTO save(ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO) {
		log.debug("Request to save ExecutiveTaskGroupPlan : {}", executiveTaskGroupPlanDTO);

		ExecutiveTaskGroupPlan executiveTaskGroupPlan = new ExecutiveTaskGroupPlan();
		// set pid
		executiveTaskGroupPlan.setPid(ExecutiveTaskGroupPlanService.PID_PREFIX + RandomUtil.generatePid());
		executiveTaskGroupPlan.setPlannedDate(executiveTaskGroupPlanDTO.getPlannedDate());
		executiveTaskGroupPlan.setCreatedDate(LocalDateTime.now());
		executiveTaskGroupPlan.setRemarks(executiveTaskGroupPlanDTO.getRemarks());
		Optional<TaskGroup> taskGroup = taskGroupRepository
				.findByNameIgnoreCaseAndPid(executiveTaskGroupPlanDTO.getTaskGroupName(),
						executiveTaskGroupPlanDTO.getTaskGroupPid());
		if(taskGroup.isPresent()){
			executiveTaskGroupPlan.setTaskGroup(taskGroup.get());
		}
		// currentUser
		executiveTaskGroupPlan.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername())
						.get());
		// set company
		executiveTaskGroupPlan.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		executiveTaskGroupPlan = executiveTaskGroupPlanRepository.save(executiveTaskGroupPlan);
		ExecutiveTaskGroupPlanDTO result = executiveTaskGroupPlanMapper
				.executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO(executiveTaskGroupPlan);
		return result;
	}
	
	

	/**
	 * Update a executiveTaskGroupPlan.
	 * 
	 * @param executiveTaskGroupPlanDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskGroupPlanDTO update(ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO) {
		log.debug("Request to Update ExecutiveTaskGroupPlan : {}", executiveTaskGroupPlanDTO);

		return executiveTaskGroupPlanRepository
				.findOneByPid(executiveTaskGroupPlanDTO.getPid())
				.map(executiveTaskGroupPlan -> {
					// update code write here

					executiveTaskGroupPlan = executiveTaskGroupPlanRepository.save(executiveTaskGroupPlan);
					ExecutiveTaskGroupPlanDTO result = executiveTaskGroupPlanMapper
							.executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO(executiveTaskGroupPlan);
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
	public Page<ExecutiveTaskGroupPlan> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ExecutiveTaskGroupPlan> result = executiveTaskGroupPlanRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskGroupPlanDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<ExecutiveTaskGroupPlan> executiveTaskGroupPlanList = executiveTaskGroupPlanRepository.findAllByCompanyId();
		List<ExecutiveTaskGroupPlanDTO> result = executiveTaskGroupPlanMapper
				.executiveTaskGroupPlansToExecutiveTaskGroupPlanDTOs(executiveTaskGroupPlanList);
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
	public Page<ExecutiveTaskGroupPlanDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ExecutiveTaskGroupPlan> activities = executiveTaskGroupPlanRepository.findAllByCompanyId(pageable);
		Page<ExecutiveTaskGroupPlanDTO> result = new PageImpl<ExecutiveTaskGroupPlanDTO>(
				executiveTaskGroupPlanMapper.executiveTaskGroupPlansToExecutiveTaskGroupPlanDTOs(activities
						.getContent()), pageable, activities.getTotalElements());
		return result;
	}

	/**
	 * Get one executiveTaskGroupPlan by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ExecutiveTaskGroupPlanDTO findOne(Long id) {
		log.debug("Request to get ExecutiveTaskGroupPlan : {}", id);
		ExecutiveTaskGroupPlan executiveTaskGroupPlan = executiveTaskGroupPlanRepository.findOne(id);
		ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO = executiveTaskGroupPlanMapper
				.executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO(executiveTaskGroupPlan);
		return executiveTaskGroupPlanDTO;
	}

	/**
	 * Get one executiveTaskGroupPlan by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ExecutiveTaskGroupPlanDTO> findOneByPid(String pid) {
		log.debug("Request to get ExecutiveTaskGroupPlan by pid : {}", pid);
		return executiveTaskGroupPlanRepository.findOneByPid(pid).map(
				executiveTaskGroupPlan -> {
					ExecutiveTaskGroupPlanDTO executiveTaskGroupPlanDTO = executiveTaskGroupPlanMapper
							.executiveTaskGroupPlanToExecutiveTaskGroupPlanDTO(executiveTaskGroupPlan);
					return executiveTaskGroupPlanDTO;
				});
	}

	/**
	 * Delete the executiveTaskGroupPlan by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ExecutiveTaskGroupPlan : {}", pid);
		executiveTaskGroupPlanRepository.findOneByPid(pid).ifPresent(executiveTaskGroupPlan -> {
			executiveTaskGroupPlanRepository.delete(executiveTaskGroupPlan.getId());
		});
	}

}
