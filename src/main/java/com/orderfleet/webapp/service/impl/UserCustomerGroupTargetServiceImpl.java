package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UserCustomerGroupTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.UserCustomerGroupTargetRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserCustomerGroupTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.MonthlyCustomerGroupTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserCustomerGroupTargetDTO;

/**
 * Service Implementation for managing UserCustomerGroupTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 15, 2016
 */
@Service
@Transactional
public class UserCustomerGroupTargetServiceImpl implements UserCustomerGroupTargetService {

	private final Logger log = LoggerFactory.getLogger(UserCustomerGroupTargetServiceImpl.class);

	@Inject
	private UserCustomerGroupTargetRepository userCustomerGroupTargetRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private StageRepository stageRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a userCustomerGroupTarget.
	 * 
	 * @param userCustomerGroupTargetDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UserCustomerGroupTargetDTO save(UserCustomerGroupTargetDTO userCustomerGroupTargetDTO) {
		log.debug("Request to save UserCustomerGroupTarget : {}", userCustomerGroupTargetDTO);

		// set pid
		userCustomerGroupTargetDTO.setPid(UserCustomerGroupTargetService.PID_PREFIX + RandomUtil.generatePid());
		UserCustomerGroupTarget userCustomerGroupTarget = new UserCustomerGroupTarget();
		userCustomerGroupTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userCustomerGroupTarget.setEndDate(userCustomerGroupTargetDTO.getEndDate());
		userCustomerGroupTarget.setStartDate(userCustomerGroupTargetDTO.getStartDate());
		userCustomerGroupTarget.setStage(stageRepository.findOneByPid(userCustomerGroupTargetDTO.getStagePid()).get());
		userCustomerGroupTarget.setTargetNumber(userCustomerGroupTargetDTO.getTargetNumber());
		userCustomerGroupTarget.setUser(userRepository.findOneByPid(userCustomerGroupTargetDTO.getUserPid()).get());
		userCustomerGroupTarget = userCustomerGroupTargetRepository.save(userCustomerGroupTarget);

		UserCustomerGroupTargetDTO result = new UserCustomerGroupTargetDTO(userCustomerGroupTarget);

		return result;
	}

	@Override
	public UserCustomerGroupTargetDTO saveMonthlyTarget(MonthlyCustomerGroupTargetDTO monthlyTargetDTO,
			LocalDate startDate, LocalDate endDate) {

		UserCustomerGroupTarget userCustomerGroupTarget = new UserCustomerGroupTarget();
		// set pid
		userCustomerGroupTarget.setPid(UserCustomerGroupTargetService.PID_PREFIX + RandomUtil.generatePid());
		userCustomerGroupTarget.setStage(stageRepository.findOneByPid(monthlyTargetDTO.getStagePid()).get());
		userCustomerGroupTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userCustomerGroupTarget.setStartDate(startDate);
		userCustomerGroupTarget.setEndDate(endDate);
		userCustomerGroupTarget.setTargetNumber(monthlyTargetDTO.getTarget());
		userCustomerGroupTarget.setUser(userRepository.findOneByPid(monthlyTargetDTO.getUserPid()).get());
		userCustomerGroupTarget = userCustomerGroupTargetRepository.save(userCustomerGroupTarget);

		UserCustomerGroupTargetDTO result = new UserCustomerGroupTargetDTO(userCustomerGroupTarget);

		return result;
	}

	/**
	 * Update a userCustomerGroupTarget.
	 * 
	 * @param userCustomerGroupTargetDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public UserCustomerGroupTargetDTO update(UserCustomerGroupTargetDTO userCustomerGroupTargetDTO) {
		log.debug("Request to Update UserCustomerGroupTarget : {}", userCustomerGroupTargetDTO);

		return userCustomerGroupTargetRepository.findOneByPid(userCustomerGroupTargetDTO.getPid())
				.map(userCustomerGroupTarget -> {
					userCustomerGroupTarget.setStartDate(userCustomerGroupTargetDTO.getStartDate());
					userCustomerGroupTarget.setEndDate(userCustomerGroupTargetDTO.getEndDate());
					userCustomerGroupTarget
							.setStage(stageRepository.findOneByPid(userCustomerGroupTargetDTO.getStagePid()).get());
					userCustomerGroupTarget
							.setUser(userRepository.findOneByPid(userCustomerGroupTargetDTO.getUserPid()).get());
					userCustomerGroupTarget.setTargetNumber(userCustomerGroupTargetDTO.getTargetNumber());
					userCustomerGroupTarget = userCustomerGroupTargetRepository.save(userCustomerGroupTarget);
					UserCustomerGroupTargetDTO result = new UserCustomerGroupTargetDTO(userCustomerGroupTarget);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserCustomerGroupTargetDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<UserCustomerGroupTarget> userCustomerGroupTargetList = userCustomerGroupTargetRepository
				.findAllByCompanyId();
		List<UserCustomerGroupTargetDTO> result = userCustomerGroupTargetList.stream()
				.map(ucg -> new UserCustomerGroupTargetDTO(ucg)).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UserCustomerGroupTargetDTO> findAllByCompany(Pageable pageable) {
		/*
		 * log.debug("Request to get all Activities"); Page<UserCustomerGroupTarget>
		 * activities = userCustomerGroupTargetRepository.findAllByCompanyId(pageable);
		 * Page<UserCustomerGroupTargetDTO> result = new
		 * PageImpl<UserCustomerGroupTargetDTO>(userCustomerGroupTargetMapper
		 * .userCustomerGroupTargetsToUserCustomerGroupTargetDTOs(activities.getContent(
		 * )), pageable, activities.getTotalElements());
		 */
		return null;
	}

	/**
	 * Get one userCustomerGroupTarget by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public UserCustomerGroupTargetDTO findOne(Long id) {
		log.debug("Request to get UserCustomerGroupTarget : {}", id);
		UserCustomerGroupTarget userCustomerGroupTarget = userCustomerGroupTargetRepository.findOne(id);
		UserCustomerGroupTargetDTO userCustomerGroupTargetDTO = new UserCustomerGroupTargetDTO(userCustomerGroupTarget);
		return userCustomerGroupTargetDTO;
	}

	/**
	 * Get one userCustomerGroupTarget by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserCustomerGroupTargetDTO> findOneByPid(String pid) {
		log.debug("Request to get UserCustomerGroupTarget by pid : {}", pid);
		return userCustomerGroupTargetRepository.findOneByPid(pid).map(userCustomerGroupTarget -> {
			UserCustomerGroupTargetDTO userCustomerGroupTargetDTO = new UserCustomerGroupTargetDTO(
					userCustomerGroupTarget);
			return userCustomerGroupTargetDTO;
		});
	}

	/**
	 * Delete the userCustomerGroupTarget by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete UserCustomerGroupTarget : {}", pid);
		userCustomerGroupTargetRepository.findOneByPid(pid).ifPresent(userCustomerGroupTarget -> {
			userCustomerGroupTargetRepository.delete(userCustomerGroupTarget.getId());
		});
	}

}
