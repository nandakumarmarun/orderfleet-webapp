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
import com.orderfleet.webapp.domain.UserTaskListAssignment;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserTaskListAssignmentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserTaskListAssignmentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserTaskListAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskListAssignmentMapper;

/**
 * Service Implementation for managing UserTaskList.
 * 
 * @author Sarath
 * @since July 14, 2016
 */
@Service
@Transactional
public class UserTaskListAssignmentServiceImpl implements UserTaskListAssignmentService {
	private final Logger log = LoggerFactory.getLogger(UserTaskListAssignmentServiceImpl.class);

	@Inject
	private UserTaskListAssignmentRepository userTaskListAssignmentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserTaskListAssignmentMapper userTaskListAssignmentMapper;

	@Inject
	private TaskListRepository taskListRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a userTaskListAssignment.
	 * 
	 * @param userTaskListAssignmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UserTaskListAssignmentDTO save(UserTaskListAssignmentDTO userTaskListAssignmentDTO) {
		log.debug("Request to save UserTaskListAssignment : {}", userTaskListAssignmentDTO);

		// set pid
		userTaskListAssignmentDTO.setPid(UserTaskListAssignmentService.PID_PREFIX + RandomUtil.generatePid());
		UserTaskListAssignment userTaskListAssignment = userTaskListAssignmentMapper
				.userTaskListAssignmentDTOToUserTaskListAssignment(userTaskListAssignmentDTO);

		// set user
		userTaskListAssignment
				.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get());
		// set company
		userTaskListAssignment.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));

		// set executiveUser
		userTaskListAssignment
				.setExecutiveUser(userRepository.findOneByPid(userTaskListAssignmentDTO.getExecutiveUserPid()).get());

		userTaskListAssignment = userTaskListAssignmentRepository.save(userTaskListAssignment);
		UserTaskListAssignmentDTO result = userTaskListAssignmentMapper
				.userTaskListAssignmentToUserTaskListAssignmentDTO(userTaskListAssignment);
		return result;
	}

	@Override
	public void save(List<UserTaskListAssignmentDTO> userTaskListAssignmentDTOs) {

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get();
		Company company = user.getCompany();

		List<UserTaskListAssignment> userTaskListAssignments = new ArrayList<>();
		for (UserTaskListAssignmentDTO userTaskListAssignmentDTO : userTaskListAssignmentDTOs) {
			// set pid
			userTaskListAssignmentDTO.setPid(UserTaskListAssignmentService.PID_PREFIX + RandomUtil.generatePid());
			UserTaskListAssignment userTaskListAssignment = userTaskListAssignmentMapper
					.userTaskListAssignmentDTOToUserTaskListAssignment(userTaskListAssignmentDTO);
			// set user
			userTaskListAssignment.setUser(user);
			// set company
			userTaskListAssignment.setCompany(company);
			userTaskListAssignment = userTaskListAssignmentRepository.save(userTaskListAssignment);
			userTaskListAssignments.add(userTaskListAssignment);
		}
		userTaskListAssignmentRepository.save(userTaskListAssignments);

	}

	/**
	 * Update a userTaskListAssignment.
	 * 
	 * @param userTaskListAssignmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public UserTaskListAssignmentDTO update(UserTaskListAssignmentDTO userTaskListAssignmentDTO) {
		log.debug("Request to Update UserTaskListAssignment : {}", userTaskListAssignmentDTO);

		return userTaskListAssignmentRepository.findOneByPid(userTaskListAssignmentDTO.getPid())
				.map(userTaskListAssignment -> {
					userTaskListAssignment.setExecutiveUser(
							userRepository.findOneByPid(userTaskListAssignmentDTO.getExecutiveUserPid()).get());
					userTaskListAssignment.setPriorityStatus(userTaskListAssignmentDTO.getPriorityStatus());
					userTaskListAssignment.setRemarks(userTaskListAssignmentDTO.getRemarks());
					userTaskListAssignment.setStartDate(userTaskListAssignmentDTO.getStartDate());
					userTaskListAssignment.setTaskList(
							taskListRepository.findOneByPid(userTaskListAssignmentDTO.getTaskListPid()).get());
					userTaskListAssignment = userTaskListAssignmentRepository.save(userTaskListAssignment);
					UserTaskListAssignmentDTO result = userTaskListAssignmentMapper
							.userTaskListAssignmentToUserTaskListAssignmentDTO(userTaskListAssignment);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the user task list assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserTaskListAssignment> findAll(Pageable pageable) {
		log.debug("Request to get all UserTaskListAssignment");
		Page<UserTaskListAssignment> result = userTaskListAssignmentRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the user task list assignments.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserTaskListAssignmentDTO> findAllByCompany() {
		log.debug("Request to get all UserTaskListAssignment");
		List<UserTaskListAssignment> userTaskListAssignmentList = userTaskListAssignmentRepository.findAllByCompanyId();
		List<UserTaskListAssignmentDTO> result = userTaskListAssignmentMapper
				.userTaskListAssignmentsToUserTaskListAssignmentDTOs(userTaskListAssignmentList);
		return result;
	}

	/**
	 * Get all the user task list assignments.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserTaskListAssignmentDTO> findUserTaskListByUserPid(String userPid) {
		log.debug("Request to get all UserTaskListAssignment");
		List<UserTaskListAssignment> userTaskListAssignmentList = userTaskListAssignmentRepository
				.findUserTaskListByUserPid(userPid);
		List<UserTaskListAssignmentDTO> result = userTaskListAssignmentMapper
				.userTaskListAssignmentsToUserTaskListAssignmentDTOs(userTaskListAssignmentList);
		return result;
	}

	/**
	 * Get all the user task list assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserTaskListAssignmentDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UserTaskListAssignment");
		Page<UserTaskListAssignment> userTaskListAssignments = userTaskListAssignmentRepository
				.findAllByCompanyId(pageable);
		Page<UserTaskListAssignmentDTO> result = new PageImpl<UserTaskListAssignmentDTO>(userTaskListAssignmentMapper
				.userTaskListAssignmentsToUserTaskListAssignmentDTOs(userTaskListAssignments.getContent()), pageable,
				userTaskListAssignments.getTotalElements());
		return result;
	}

	/**
	 * Get one userTaskListAssignment by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public UserTaskListAssignmentDTO findOne(Long id) {
		log.debug("Request to get UserTaskListAssignment : {}", id);
		UserTaskListAssignment userTaskListAssignment = userTaskListAssignmentRepository.findOne(id);
		UserTaskListAssignmentDTO userTaskListAssignmentDTO = userTaskListAssignmentMapper
				.userTaskListAssignmentToUserTaskListAssignmentDTO(userTaskListAssignment);
		return userTaskListAssignmentDTO;
	}

	/**
	 * Get one userTaskListAssignment by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserTaskListAssignmentDTO> findOneByPid(String pid) {
		log.debug("Request to get UserTaskListAssignment by pid : {}", pid);
		return userTaskListAssignmentRepository.findOneByPid(pid).map(userTaskListAssignment -> {
			UserTaskListAssignmentDTO userTaskListAssignmentDTO = userTaskListAssignmentMapper
					.userTaskListAssignmentToUserTaskListAssignmentDTO(userTaskListAssignment);
			return userTaskListAssignmentDTO;
		});
	}

	/**
	 * Delete the userTaskListAssignment by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete UserTaskListAssignment : {}", pid);
		userTaskListAssignmentRepository.findOneByPid(pid).ifPresent(userTaskListAssignment -> {
			userTaskListAssignmentRepository.delete(userTaskListAssignment.getId());
		});
	}

}
