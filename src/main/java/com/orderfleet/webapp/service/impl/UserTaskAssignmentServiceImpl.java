package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.domain.enums.TaskStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserTaskAssignmentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserTaskAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskAssignmentMapper;

/**
 * Service Implementation for managing UserTaskAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
@Service
@Transactional
public class UserTaskAssignmentServiceImpl implements UserTaskAssignmentService {

	private final Logger log = LoggerFactory.getLogger(UserTaskAssignmentServiceImpl.class);

	@Inject
	private UserTaskAssignmentRepository userTaskAssignmentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserTaskAssignmentMapper userTaskAssignmentMapper;

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a userTaskAssignment.
	 * 
	 * @param userTaskAssignmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UserTaskAssignmentDTO save(UserTaskAssignmentDTO userTaskAssignmentDTO) {
		log.debug("Request to save UserTaskAssignment : {}", userTaskAssignmentDTO);

		// set pid
		userTaskAssignmentDTO.setPid(UserTaskAssignmentService.PID_PREFIX + RandomUtil.generatePid());
		UserTaskAssignment userTaskAssignment = userTaskAssignmentMapper
				.userTaskAssignmentDTOToUserTaskAssignment(userTaskAssignmentDTO);
		userTaskAssignment.setTaskStatus(TaskStatus.OPENED);
		// set user
		userTaskAssignment.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get());
		// set company
		userTaskAssignment.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userTaskAssignment = userTaskAssignmentRepository.save(userTaskAssignment);
		UserTaskAssignmentDTO result = userTaskAssignmentMapper
				.userTaskAssignmentToUserTaskAssignmentDTO(userTaskAssignment);
		return result;
	}

	@Override
	public void save(List<UserTaskAssignmentDTO> userTaskAssignmentDTOs) {

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get();
		Company company = user.getCompany();

		List<UserTaskAssignment> userTaskAssignments = new ArrayList<>();
		for (UserTaskAssignmentDTO userTaskAssignmentDTO : userTaskAssignmentDTOs) {
			// set pid
			userTaskAssignmentDTO.setPid(UserTaskAssignmentService.PID_PREFIX + RandomUtil.generatePid());
			UserTaskAssignment userTaskAssignment = userTaskAssignmentMapper
					.userTaskAssignmentDTOToUserTaskAssignment(userTaskAssignmentDTO);
			userTaskAssignment.setTaskStatus(TaskStatus.OPENED);
			// set user
			userTaskAssignment.setUser(user);
			// set company
			userTaskAssignment.setCompany(company);
			userTaskAssignment = userTaskAssignmentRepository.save(userTaskAssignment);
			userTaskAssignments.add(userTaskAssignment);
		}
		userTaskAssignmentRepository.save(userTaskAssignments);
	}

	@Override
	public void closeUserTaskAssignment(String userTaskAssignmentPid) {
		userTaskAssignmentRepository.findOneByPid(userTaskAssignmentPid).map(userTaskAssignment -> {
			userTaskAssignment.setTaskStatus(TaskStatus.CLOSED);
			userTaskAssignmentRepository.save(userTaskAssignment);
			return true;
		}).orElse(null);
	}

	/**
	 * Update a userTaskAssignment.
	 * 
	 * @param userTaskAssignmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public UserTaskAssignmentDTO update(UserTaskAssignmentDTO userTaskAssignmentDTO) {
		log.debug("Request to Update UserTaskAssignment : {}", userTaskAssignmentDTO);

		return userTaskAssignmentRepository.findOneByPid(userTaskAssignmentDTO.getPid()).map(userTaskAssignment -> {
			userTaskAssignment
					.setExecutiveUser(userRepository.findOneByPid(userTaskAssignmentDTO.getExecutiveUserPid()).get());
			userTaskAssignment.setPriorityStatus(userTaskAssignmentDTO.getPriorityStatus());
			userTaskAssignment.setRemarks(userTaskAssignmentDTO.getRemarks());
			userTaskAssignment.setStartDate(userTaskAssignmentDTO.getStartDate());
			userTaskAssignment.setTask(taskRepository.findOneByPid(userTaskAssignmentDTO.getTaskPid()).get());
			userTaskAssignment = userTaskAssignmentRepository.save(userTaskAssignment);
			UserTaskAssignmentDTO result = userTaskAssignmentMapper
					.userTaskAssignmentToUserTaskAssignmentDTO(userTaskAssignment);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the user task assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserTaskAssignment> findAll(Pageable pageable) {
		log.debug("Request to get all UserTaskAssignments");
		Page<UserTaskAssignment> result = userTaskAssignmentRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the user task assignments.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserTaskAssignmentDTO> findAllByCompany() {
		log.debug("Request to get all UserTaskAssignments");
		List<UserTaskAssignment> userTaskAssignmentList = userTaskAssignmentRepository.findAllByCompanyId();
		List<UserTaskAssignmentDTO> result = userTaskAssignmentMapper
				.userTaskAssignmentsToUserTaskAssignmentDTOs(userTaskAssignmentList);
		return result;
	}

	/**
	 * Get the user task assignments.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserTaskAssignmentDTO> findUserTasksByUserPid(String userPid) {
		log.debug("Request to get all UserTaskAssignments");
		List<UserTaskAssignment> userTaskAssignmentList = userTaskAssignmentRepository.findUserTasksByUserPid(userPid);
		List<UserTaskAssignmentDTO> result = userTaskAssignmentMapper
				.userTaskAssignmentsToUserTaskAssignmentDTOs(userTaskAssignmentList);
		return result;
	}

	/**
	 * Get all the user task assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserTaskAssignmentDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UserTaskAssignments");
		Page<UserTaskAssignment> userTaskassignments = userTaskAssignmentRepository.findAllByCompanyId(pageable);
		Page<UserTaskAssignmentDTO> result = new PageImpl<UserTaskAssignmentDTO>(
				userTaskAssignmentMapper.userTaskAssignmentsToUserTaskAssignmentDTOs(userTaskassignments.getContent()),
				pageable, userTaskassignments.getTotalElements());
		return result;
	}

	/**
	 * Get one userTaskAssignment by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public UserTaskAssignmentDTO findOne(Long id) {
		log.debug("Request to get UserTaskAssignment : {}", id);
		UserTaskAssignment userTaskAssignment = userTaskAssignmentRepository.findOne(id);
		UserTaskAssignmentDTO userTaskAssignmentDTO = userTaskAssignmentMapper
				.userTaskAssignmentToUserTaskAssignmentDTO(userTaskAssignment);
		return userTaskAssignmentDTO;
	}

	/**
	 * Get one userTaskAssignment by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserTaskAssignmentDTO> findOneByPid(String pid) {
		log.debug("Request to get UserTaskAssignment by pid : {}", pid);
		return userTaskAssignmentRepository.findOneByPid(pid).map(userTaskAssignment -> {
			UserTaskAssignmentDTO userTaskAssignmentDTO = userTaskAssignmentMapper
					.userTaskAssignmentToUserTaskAssignmentDTO(userTaskAssignment);
			return userTaskAssignmentDTO;
		});
	}

	/**
	 * Delete the userTaskAssignment by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete UserTaskAssignment : {}", pid);
		userTaskAssignmentRepository.findOneByPid(pid).ifPresent(userTaskAssignment -> {
			userTaskAssignmentRepository.delete(userTaskAssignment.getId());
		});
	}

}
