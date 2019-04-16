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

import com.orderfleet.webapp.domain.ExecutiveTaskListPlan;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskListPlanRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveTaskListPlanService;
import com.orderfleet.webapp.service.TaskListService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskListPlanDTO;
import com.orderfleet.webapp.web.rest.dto.TaskListDTO;
import com.orderfleet.webapp.web.rest.mapper.ExecutiveTaskListPlanMapper;
import com.orderfleet.webapp.web.rest.mapper.TaskListMapper;

/**
 * Service Implementation for managing ExecutiveTaskListPlan.
 * 
 * @author Sarath
 * @since July 23, 2016
 */
@Service
@Transactional
public class ExecutiveTaskListPlanServiceImpl implements ExecutiveTaskListPlanService {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskListPlanServiceImpl.class);

	@Inject
	private ExecutiveTaskListPlanRepository executiveTaskListPlanRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ExecutiveTaskListPlanMapper executiveTaskListPlanMapper;

	@Inject
	private UserRepository userRepository;

	@Inject
	private TaskListService taskListService;

	@Inject
	private TaskListMapper taskListMapper;

	/**
	 * Save a executiveTaskListPlan.
	 * 
	 * @param executiveTaskListPlanDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskListPlanDTO save(ExecutiveTaskListPlanDTO executiveTaskListPlanDTO) {
		log.debug("Request to save ExecutiveTaskListPlan : {}", executiveTaskListPlanDTO);

		ExecutiveTaskListPlan executiveTaskListPlan = new ExecutiveTaskListPlan();
		// set pid
		executiveTaskListPlan.setPid(ExecutiveTaskListPlanService.PID_PREFIX + RandomUtil.generatePid());
		// set company
		executiveTaskListPlan.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		// currentUser
		executiveTaskListPlan
				.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get());
		executiveTaskListPlan.setPlannedDate(executiveTaskListPlanDTO.getPlannedDate());
		executiveTaskListPlan.setRemarks(executiveTaskListPlanDTO.getRemarks());
		executiveTaskListPlan.setCreatedDate(LocalDateTime.now());

		Optional<TaskListDTO> taskLists = taskListService.findByNameAndPid(executiveTaskListPlanDTO.getTaskListName(),
				executiveTaskListPlanDTO.getTaskListPid());
		TaskList taskList = taskListMapper.taskListDTOToTaskList(taskLists.get());
		executiveTaskListPlan.setTaskList(taskList);

		executiveTaskListPlan = executiveTaskListPlanRepository.save(executiveTaskListPlan);
		ExecutiveTaskListPlanDTO result = executiveTaskListPlanMapper
				.executiveTaskListPlanToExecutiveTaskListPlanDTO(executiveTaskListPlan);

		return result;
	}

	/**
	 * Update a executiveTaskListPlan.
	 * 
	 * @param executiveTaskListPlanDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskListPlanDTO update(ExecutiveTaskListPlanDTO executiveTaskListPlanDTO) {
		log.debug("Request to Update ExecutiveTaskListPlan : {}", executiveTaskListPlanDTO);

		return executiveTaskListPlanRepository
				.findOneByPid(executiveTaskListPlanDTO.getPid())
				.map(executiveTaskListPlan -> {
					// update code write here

					executiveTaskListPlan = executiveTaskListPlanRepository.save(executiveTaskListPlan);
					ExecutiveTaskListPlanDTO result = executiveTaskListPlanMapper
							.executiveTaskListPlanToExecutiveTaskListPlanDTO(executiveTaskListPlan);
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
	public Page<ExecutiveTaskListPlan> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ExecutiveTaskListPlan> result = executiveTaskListPlanRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskListPlanDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<ExecutiveTaskListPlan> executiveTaskListPlanList = executiveTaskListPlanRepository.findAllByCompanyId();
		List<ExecutiveTaskListPlanDTO> result = executiveTaskListPlanMapper
				.executiveTaskListPlansToExecutiveTaskListPlanDTOs(executiveTaskListPlanList);
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
	public Page<ExecutiveTaskListPlanDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ExecutiveTaskListPlan> activities = executiveTaskListPlanRepository.findAllByCompanyId(pageable);
		Page<ExecutiveTaskListPlanDTO> result = new PageImpl<ExecutiveTaskListPlanDTO>(
				executiveTaskListPlanMapper.executiveTaskListPlansToExecutiveTaskListPlanDTOs(activities.getContent()),
				pageable, activities.getTotalElements());
		return result;
	}

	/**
	 * Get one executiveTaskListPlan by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ExecutiveTaskListPlanDTO findOne(Long id) {
		log.debug("Request to get ExecutiveTaskListPlan : {}", id);
		ExecutiveTaskListPlan executiveTaskListPlan = executiveTaskListPlanRepository.findOne(id);
		ExecutiveTaskListPlanDTO executiveTaskListPlanDTO = executiveTaskListPlanMapper
				.executiveTaskListPlanToExecutiveTaskListPlanDTO(executiveTaskListPlan);
		return executiveTaskListPlanDTO;
	}

	/**
	 * Get one executiveTaskListPlan by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ExecutiveTaskListPlanDTO> findOneByPid(String pid) {
		log.debug("Request to get ExecutiveTaskListPlan by pid : {}", pid);
		return executiveTaskListPlanRepository.findOneByPid(pid).map(
				executiveTaskListPlan -> {
					ExecutiveTaskListPlanDTO executiveTaskListPlanDTO = executiveTaskListPlanMapper
							.executiveTaskListPlanToExecutiveTaskListPlanDTO(executiveTaskListPlan);
					return executiveTaskListPlanDTO;
				});
	}

	/**
	 * Delete the executiveTaskListPlan by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ExecutiveTaskListPlan : {}", pid);
		executiveTaskListPlanRepository.findOneByPid(pid).ifPresent(executiveTaskListPlan -> {
			executiveTaskListPlanRepository.delete(executiveTaskListPlan.getId());
		});
	}

}
