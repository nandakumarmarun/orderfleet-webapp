package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import com.orderfleet.webapp.domain.UserTaskGroupAssignment;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaskGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserTaskGroupAssignmentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.UserTaskGroupAssignmentService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserTaskGroupAssignmentDTO;
import com.orderfleet.webapp.web.rest.mapper.UserTaskGroupAssignmentMapper;

/**
 * Service Implementation for managing UserTaskGroupAssignment.
 * 
 * @author Muhammed Riyas T
 * @since June 17, 2016
 */
@Service
@Transactional
public class UserTaskGroupAssignmentServiceImpl implements UserTaskGroupAssignmentService {

	private final Logger log = LoggerFactory.getLogger(UserTaskGroupAssignmentServiceImpl.class);

	@Inject
	private UserTaskGroupAssignmentRepository userTaskGroupAssignmentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserTaskGroupAssignmentMapper userTaskGroupAssignmentMapper;

	@Inject
	private TaskGroupRepository taskGroupRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a userTaskGroupAssignment.
	 * 
	 * @param userTaskGroupAssignmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UserTaskGroupAssignmentDTO save(UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO) {
		log.debug("Request to save UserTaskGroupAssignment : {}", userTaskGroupAssignmentDTO);

		// set pid
		userTaskGroupAssignmentDTO.setPid(UserTaskGroupAssignmentService.PID_PREFIX + RandomUtil.generatePid());
		UserTaskGroupAssignment userTaskGroupAssignment = userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentDTOToUserTaskGroupAssignment(userTaskGroupAssignmentDTO);

		// set user
		userTaskGroupAssignment
				.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get());
		// set company
		userTaskGroupAssignment.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userTaskGroupAssignment = userTaskGroupAssignmentRepository.save(userTaskGroupAssignment);
		UserTaskGroupAssignmentDTO result = userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(userTaskGroupAssignment);
		return result;
	}

	@Override
	public void save(List<UserTaskGroupAssignmentDTO> userTaskGroupAssignmentDTOs) {

		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUser().getUsername()).get();
		Company company = user.getCompany();

		List<UserTaskGroupAssignment> userTaskGroupAssignments = new ArrayList<>();
		for (UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO : userTaskGroupAssignmentDTOs) {
			// set pid
			userTaskGroupAssignmentDTO.setPid(UserTaskAssignmentService.PID_PREFIX + RandomUtil.generatePid());
			UserTaskGroupAssignment userTaskGroupAssignment = userTaskGroupAssignmentMapper
					.userTaskGroupAssignmentDTOToUserTaskGroupAssignment(userTaskGroupAssignmentDTO);
			// set user
			userTaskGroupAssignment.setUser(user);
			// set company
			userTaskGroupAssignment.setCompany(company);
			userTaskGroupAssignment = userTaskGroupAssignmentRepository.save(userTaskGroupAssignment);
			userTaskGroupAssignments.add(userTaskGroupAssignment);
		}
		userTaskGroupAssignmentRepository.save(userTaskGroupAssignments);
	}

	/**
	 * Update a userTaskGroupAssignment.
	 * 
	 * @param userTaskGroupAssignmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public UserTaskGroupAssignmentDTO update(UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO) {
		log.debug("Request to Update UserTaskGroupAssignment : {}", userTaskGroupAssignmentDTO);

		return userTaskGroupAssignmentRepository.findOneByPid(userTaskGroupAssignmentDTO.getPid())
				.map(userTaskGroupAssignment -> {
					userTaskGroupAssignment.setExecutiveUser(
							userRepository.findOneByPid(userTaskGroupAssignmentDTO.getExecutiveUserPid()).get());
					userTaskGroupAssignment.setPriorityStatus(userTaskGroupAssignmentDTO.getPriorityStatus());
					userTaskGroupAssignment.setRemarks(userTaskGroupAssignmentDTO.getRemarks());
					userTaskGroupAssignment.setStartDate(userTaskGroupAssignmentDTO.getStartDate());
					userTaskGroupAssignment.setTaskGroup(
							taskGroupRepository.findOneByPid(userTaskGroupAssignmentDTO.getTaskGroupPid()).get());
					userTaskGroupAssignment = userTaskGroupAssignmentRepository.save(userTaskGroupAssignment);
					UserTaskGroupAssignmentDTO result = userTaskGroupAssignmentMapper
							.userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(userTaskGroupAssignment);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the user task group assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserTaskGroupAssignment> findAll(Pageable pageable) {
		log.debug("Request to get all UserTaskGroupAssignment");
		Page<UserTaskGroupAssignment> result = userTaskGroupAssignmentRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the user task group assignments.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserTaskGroupAssignmentDTO> findAllByCompany() {
		log.debug("Request to get all UserTaskGroupAssignment");
		List<UserTaskGroupAssignment> userTaskGroupAssignmentList = userTaskGroupAssignmentRepository
				.findAllByCompanyId();
		List<UserTaskGroupAssignmentDTO> result = userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentsToUserTaskGroupAssignmentDTOs(userTaskGroupAssignmentList);
		return result;
	}

	/**
	 * Get all the user task group assignments.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserTaskGroupAssignmentDTO> findUserTaskGroupsByUserPid(String userPid) {
		log.debug("Request to get all UserTaskGroupAssignment");
		List<UserTaskGroupAssignment> userTaskGroupAssignmentList = userTaskGroupAssignmentRepository
				.findUserTaskGroupsByUserPid(userPid);
		List<UserTaskGroupAssignmentDTO> result = userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentsToUserTaskGroupAssignmentDTOs(userTaskGroupAssignmentList);
		return result;
	}

	/**
	 * Get all the user task group assignments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserTaskGroupAssignmentDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UserTaskGroupAssignment");
		Page<UserTaskGroupAssignment> userTaskGroupAssignments = userTaskGroupAssignmentRepository
				.findAllByCompanyId(pageable);
		Page<UserTaskGroupAssignmentDTO> result = new PageImpl<UserTaskGroupAssignmentDTO>(userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentsToUserTaskGroupAssignmentDTOs(userTaskGroupAssignments.getContent()), pageable,
				userTaskGroupAssignments.getTotalElements());
		return result;
	}

	/**
	 * Get one userTaskGroupAssignment by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public UserTaskGroupAssignmentDTO findOne(Long id) {
		log.debug("Request to get UserTaskGroupAssignment : {}", id);
		UserTaskGroupAssignment userTaskGroupAssignment = userTaskGroupAssignmentRepository.findOne(id);
		UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO = userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(userTaskGroupAssignment);
		return userTaskGroupAssignmentDTO;
	}

	/**
	 * Get one userTaskGroupAssignment by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserTaskGroupAssignmentDTO> findOneByPid(String pid) {
		log.debug("Request to get UserTaskGroupAssignment by pid : {}", pid);
		return userTaskGroupAssignmentRepository.findOneByPid(pid).map(userTaskGroupAssignment -> {
			UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO = userTaskGroupAssignmentMapper
					.userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(userTaskGroupAssignment);
			return userTaskGroupAssignmentDTO;
		});
	}

	/**
	 * Delete the userTaskGroupAssignment by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete UserTaskGroupAssignment : {}", pid);
		userTaskGroupAssignmentRepository.findOneByPid(pid).ifPresent(userTaskGroupAssignment -> {
			userTaskGroupAssignmentRepository.delete(userTaskGroupAssignment.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public UserTaskGroupAssignmentDTO findAllByTaskGroupPidAndStartDate(String taskGroupPid) {
		log.debug("Request to get UserTaskGroupAssignment : {}", taskGroupPid);

		Date input = new Date();
		LocalDate date = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		UserTaskGroupAssignment userTaskGroupAssignment = userTaskGroupAssignmentRepository
				.findAllByTaskGroupPidAndStartDate(taskGroupPid, date);
		UserTaskGroupAssignmentDTO userTaskGroupAssignmentDTO = userTaskGroupAssignmentMapper
				.userTaskGroupAssignmentToUserTaskGroupAssignmentDTO(userTaskGroupAssignment);
		return userTaskGroupAssignmentDTO;
	}

}
