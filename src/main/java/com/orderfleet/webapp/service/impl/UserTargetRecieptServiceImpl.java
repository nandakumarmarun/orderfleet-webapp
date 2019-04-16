package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
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

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.UserReceiptTarget;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.UserReceiptTargetDocumentRepository;
import com.orderfleet.webapp.repository.UserReceiptTargetRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserReceiptTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.UserReceiptTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.UserReceiptTargetMapper;

/**
 * Service Implementation for managing UserReceiptTarget.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
@Service
@Transactional
public class UserTargetRecieptServiceImpl implements UserReceiptTargetService {

	private final Logger log = LoggerFactory.getLogger(UserTargetRecieptServiceImpl.class);

	@Inject
	private UserReceiptTargetRepository userReceiptTargetRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserReceiptTargetMapper userReceiptTargetMapper;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserReceiptTargetDocumentRepository userReceiptTargetDocumentRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	/**
	 * Save a userReceiptTarget.
	 * 
	 * @param userReceiptTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UserReceiptTargetDTO save(UserReceiptTargetDTO userReceiptTargetDTO) {
		log.debug("Request to save UserReceiptTarget : {}", userReceiptTargetDTO);

		// set pid
		userReceiptTargetDTO.setPid(UserReceiptTargetService.PID_PREFIX + RandomUtil.generatePid());
		UserReceiptTarget userReceiptTarget = userReceiptTargetMapper
				.userReceiptTargetDTOToUserReceiptTarget(userReceiptTargetDTO);
		// set company
		userReceiptTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		userReceiptTarget = userReceiptTargetRepository.save(userReceiptTarget);
		UserReceiptTargetDTO result = userReceiptTargetMapper
				.userReceiptTargetToUserReceiptTargetDTO(userReceiptTarget);
		return result;
	}

	/**
	 * Update a userReceiptTarget.
	 * 
	 * @param userReceiptTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public UserReceiptTargetDTO update(UserReceiptTargetDTO userReceiptTargetDTO) {
		log.debug("Request to Update UserReceiptTarget : {}", userReceiptTargetDTO);

		return userReceiptTargetRepository.findOneByPid(userReceiptTargetDTO.getPid()).map(userReceiptTarget -> {
			userReceiptTarget.setTargetPercentage(userReceiptTargetDTO.getTargetPercentage());
			userReceiptTarget.setTargetAmount(userReceiptTargetDTO.getTargetAmount());
			userReceiptTarget.setStartDate(userReceiptTargetDTO.getStartDate());
			userReceiptTarget.setEndDate(userReceiptTargetDTO.getEndDate());
			userReceiptTarget.setUser(userRepository.findOneByPid(userReceiptTargetDTO.getUserPid()).get());
			userReceiptTarget = userReceiptTargetRepository.save(userReceiptTarget);
			UserReceiptTargetDTO result = userReceiptTargetMapper
					.userReceiptTargetToUserReceiptTargetDTO(userReceiptTarget);
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
	public Page<UserReceiptTarget> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<UserReceiptTarget> result = userReceiptTargetRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserReceiptTargetDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		List<UserReceiptTarget> userReceiptTargetList = userReceiptTargetRepository.findAllByCompanyId();
		List<UserReceiptTargetDTO> result = userReceiptTargetMapper
				.userReceiptTargetsToUserReceiptTargetDTOs(userReceiptTargetList);
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
	public Page<UserReceiptTargetDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<UserReceiptTarget> activities = userReceiptTargetRepository.findAllByCompanyId(pageable);
		Page<UserReceiptTargetDTO> result = new PageImpl<UserReceiptTargetDTO>(
				userReceiptTargetMapper.userReceiptTargetsToUserReceiptTargetDTOs(activities.getContent()), pageable,
				activities.getTotalElements());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public UserReceiptTargetDTO findOneByCurrentUserAndDate(LocalDate date) {
		UserReceiptTarget userReceiptTarget = userReceiptTargetRepository.findOneByCurrentUserAndDate(date);
		if (userReceiptTarget != null) {
			UserReceiptTargetDTO userReceiptTargetDTO = userReceiptTargetMapper
					.userReceiptTargetToUserReceiptTargetDTO(userReceiptTarget);
			// get user receipt target documents
			List<Document> documents = userReceiptTargetDocumentRepository
					.findDocumentsByUserReceiptTargetPid(userReceiptTarget.getPid());
			if (!documents.isEmpty()) {
				// get and set achieved amount
				Double achievedAmount = accountingVoucherHeaderRepository.getCurrentUserAchievedAmount(documents,
						userReceiptTargetDTO.getStartDate().atTime(0, 0),
						userReceiptTargetDTO.getEndDate().atTime(23, 59));
				if (achievedAmount != null)
					userReceiptTargetDTO.setAchievedAmount(achievedAmount);
			}
			
			if(userReceiptTargetDTO.getTargetAmount() > 0) {
				if (userReceiptTarget.getTargetPercentage() > 0) {
					// find percentage and set
					double amount = ((double)userReceiptTarget.getTargetPercentage() / 100) * userReceiptTargetDTO.getTargetAmount();
					userReceiptTargetDTO.setTargetAmount((long) amount);
				}
				return userReceiptTargetDTO;
			} else {
				// get receivables
				// current user employee locations
				Set<Long> locationIds = employeeProfileLocationRepository.findLocationIdsByEmployeeProfileIsCurrentUser();
				Set<String> accountProfilePids = locationAccountProfileRepository.findAccountProfilePidsByLocationIdIn(locationIds);
				Double dueAmount = receivablePayableRepository.findDueAmountByAccountProfilePidInAndCompanyId(accountProfilePids);
				if (dueAmount != null) {
					if (userReceiptTarget.getTargetPercentage() > 0) {
						// find percentage and set
						double amount = ((double)userReceiptTarget.getTargetPercentage() / 100) * dueAmount;
						userReceiptTargetDTO.setTargetAmount((long) amount);
					} else {
						userReceiptTargetDTO.setTargetAmount(dueAmount.longValue());
					}

				}
			}
			return userReceiptTargetDTO;
		}
		return null;
	}

	/**
	 * Get one userReceiptTarget by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public UserReceiptTargetDTO findOne(Long id) {
		log.debug("Request to get UserReceiptTarget : {}", id);
		UserReceiptTarget userReceiptTarget = userReceiptTargetRepository.findOne(id);
		UserReceiptTargetDTO userReceiptTargetDTO = userReceiptTargetMapper
				.userReceiptTargetToUserReceiptTargetDTO(userReceiptTarget);
		return userReceiptTargetDTO;
	}

	/**
	 * Get one userReceiptTarget by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UserReceiptTargetDTO> findOneByPid(String pid) {
		log.debug("Request to get UserReceiptTarget by pid : {}", pid);
		return userReceiptTargetRepository.findOneByPid(pid).map(userReceiptTarget -> {
			UserReceiptTargetDTO userReceiptTargetDTO = userReceiptTargetMapper
					.userReceiptTargetToUserReceiptTargetDTO(userReceiptTarget);
			return userReceiptTargetDTO;
		});
	}

	/**
	 * Delete the userReceiptTarget by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete UserReceiptTarget : {}", pid);
		userReceiptTargetRepository.findOneByPid(pid).ifPresent(userReceiptTarget -> {
			userReceiptTargetRepository.delete(userReceiptTarget.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserReceiptTarget> findUserAndDateWiseDuplicate(String userPid, LocalDate startDate,
			LocalDate endDate) {
		return userReceiptTargetRepository.findUserAndDateWiseDuplicate(userPid, startDate, endDate);
	}

}