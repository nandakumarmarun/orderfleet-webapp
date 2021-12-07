package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfileDynamicDocumentAccountprofile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.repository.AccountProfileDynamicDocumentAccountprofileRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Service Implementation for managing ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class ExecutiveTaskExecutionServiceImpl implements ExecutiveTaskExecutionService {

	private final Logger log = LoggerFactory.getLogger(ExecutiveTaskExecutionServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AccountProfileDynamicDocumentAccountprofileRepository accountProfileDynamicDocumentAccountprofileRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	/**
	 * Save a executiveTaskExecution.
	 * 
	 * @param executiveTaskExecutionDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ExecutiveTaskExecutionDTO save(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO) {
		log.debug("Request to save ExecutiveTaskExecution : {}", executiveTaskExecutionDTO);

		ExecutiveTaskExecution executiveTaskExecution = new ExecutiveTaskExecution();
		// set pid
		executiveTaskExecution.setPid(ExecutiveTaskExecutionService.PID_PREFIX + RandomUtil.generatePid());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		executiveTaskExecution.setAccountProfile(
				accountProfileRepository.findOneByPid(executiveTaskExecutionDTO.getAccountProfilePid()).get());
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
	                DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id1 = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description1 ="get one by pid";
	        		LocalDateTime startLCTime1 = LocalDateTime.now();
	        		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
	        		String startDate1 = startLCTime1.format(DATE_FORMAT1);
	        		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		executiveTaskExecution.setAccountType(
				accountTypeRepository.findOneByPid(executiveTaskExecutionDTO.getAccountTypePid()).get());
		 String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);
		executiveTaskExecution
				.setActivity(activityRepository.findOneByPid(executiveTaskExecutionDTO.getActivityPid()).get());
		executiveTaskExecution.setDate(executiveTaskExecutionDTO.getDate());
		executiveTaskExecution.setRemarks(executiveTaskExecutionDTO.getRemarks());
		executiveTaskExecution.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
		executiveTaskExecution.setLatitude(executiveTaskExecutionDTO.getLatitude());
		executiveTaskExecution.setLongitude(executiveTaskExecutionDTO.getLongitude());
		// set company
		executiveTaskExecution.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));

		executiveTaskExecution = executiveTaskExecutionRepository.save(executiveTaskExecution);
		ExecutiveTaskExecutionDTO result = new ExecutiveTaskExecutionDTO(executiveTaskExecution);
		return result;
	}

	@Override
	public void changeActivityStatus(String pid, ActivityStatus activityStatus, String reason) {
		log.debug("Request to change ExecutiveTaskExecution ActivityStatus : {}", activityStatus);
		executiveTaskExecutionRepository.findOneByPid(pid).map(executiveTaskExecution -> {
			executiveTaskExecution.setActivityStatus(activityStatus);
			executiveTaskExecution.setRejectReasonRemark(reason);
			return null;
		});
	}

	/**
	 * Get all the executiveTaskExecutions.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ExecutiveTaskExecution> findAll(Pageable pageable) {
		log.debug("Request to get all ExecutiveTaskExecutions");
		Page<ExecutiveTaskExecution> result = executiveTaskExecutionRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the executiveTaskExecutions.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskExecutionDTO> findAllByCompany() {
		log.debug("Request to get all ExecutiveTaskExecutions");
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdOrderByDateDesc();
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the executiveTaskExecutions.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ExecutiveTaskExecutionDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all ExecutiveTaskExecutions");
		Page<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdOrderByDateDesc(pageable);
		List<ExecutiveTaskExecutionDTO> executiveTaskExecutionList = executiveTaskExecutions.getContent().stream()
				.map(ExecutiveTaskExecutionDTO::new).collect(Collectors.toList());
		Page<ExecutiveTaskExecutionDTO> result = new PageImpl<ExecutiveTaskExecutionDTO>(executiveTaskExecutionList,
				pageable, executiveTaskExecutions.getTotalElements());
		return result;
	}

	/**
	 * Get one executiveTaskExecution by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ExecutiveTaskExecutionDTO findOne(Long id) {
		log.debug("Request to get ExecutiveTaskExecution : {}", id);
		ExecutiveTaskExecution executiveTaskExecution = executiveTaskExecutionRepository.findOne(id);
		ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO(executiveTaskExecution);
		return executiveTaskExecutionDTO;
	}

	/**
	 * Get one executiveTaskExecution by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ExecutiveTaskExecutionDTO> findOneByPid(String pid) {
		log.debug("Request to get ExecutiveTaskExecution by pid : {}", pid);
		return executiveTaskExecutionRepository.findOneByPid(pid).map(executiveTaskExecution -> {
			ExecutiveTaskExecutionDTO executiveTaskExecutionDTO = new ExecutiveTaskExecutionDTO(executiveTaskExecution);
			return executiveTaskExecutionDTO;
		});
	}

	/**
	 * Delete the executiveTaskExecution by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ExecutiveTaskExecution : {}", pid);
		executiveTaskExecutionRepository.findOneByPid(pid).ifPresent(executiveTaskExecution -> {
			executiveTaskExecutionRepository.delete(executiveTaskExecution.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Long countByUserIdAndActivityIdAndDateBetweenAndActivityStatusNot(Long userId, Long activityId,
			ActivityStatus activityStatus, LocalDateTime startDate, LocalDateTime endDate) {
		return executiveTaskExecutionRepository.countByUserIdAndActivityIdAndActivityStatusNotAndDateBetween(userId,
				activityId, activityStatus, startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdAndDateBetweenOrderByDateDesc(fromDate, toDate);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdUserPidActivityPidAndDateBetween(String userPid,
			String activityPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdUserPidActivityPidAndDateBetweenOrderByDateDesc(userPid, activityPid, fromDate,
						toDate);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(userPid, fromDate, toDate);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(userPid, fromDate, toDate);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdActivityPidAndDateBetween(String activityPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdActivityPidAndDateBetweenOrderByDateDesc(activityPid, fromDate, toDate);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateExecutiveTaskExecutionStatus(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO) {
		ExecutiveTaskExecution executiveTaskExecution = executiveTaskExecutionRepository
				.findOneByPid(executiveTaskExecutionDTO.getPid()).get();
		executiveTaskExecution.setStatus(true);
		executiveTaskExecutionRepository.save(executiveTaskExecution);
	}

	@Override
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdAndStatusFalse() {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdAndStatusFalse();
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ExecutiveTaskExecutionDTO> findAllByCompanyIdActivityPidAndAccountProfilePid(String activityPid,
			String accountPid) {
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.findAllByCompanyIdActivityPidAccountPid(activityPid, accountPid);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ExecutiveTaskExecutionDTO> findAllByCompanyIdAndClientTransactionKey(String clientTransactionKey) {
		log.debug("Request to get ExecutiveTaskExecution by clientTransactionKey : {}", clientTransactionKey);
		return executiveTaskExecutionRepository.findByCompanyIdAndClientTransactionKey(clientTransactionKey)
				.map(executiveTaskExecution -> {
					ExecutiveTaskExecutionDTO result = new ExecutiveTaskExecutionDTO(executiveTaskExecution);
					return result;
				});
	}

	@Override
	public Map<String, String> findAccountFromForm(String formPid, String dynamicDocumentPid, String exePid) {

		Map<String, String> map = new HashMap<>();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "FORM_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the FilledForm Details By Document FormPid And ExecutiveTaskExecution";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<FilledFormDetail> filledFormDetails = filledFormRepository
				.findFilledFormDetailsByDocumentAndFormPidAndExecutiveTaskExecution(dynamicDocumentPid, formPid,
						exePid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
	                DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
	        		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        		String id1 = "APDD_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description1 ="get all by docPid and formPid";
	        		LocalDateTime startLCTime1 = LocalDateTime.now();
	        		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
	        		String startDate1 = startLCTime1.format(DATE_FORMAT1);
	        		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfileDynamicDocumentAccountprofile> accountProfileDynamicDocumentAccountprofiles = accountProfileDynamicDocumentAccountprofileRepository
				.findAllByDocumentPidAndFormPid(dynamicDocumentPid, formPid);
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);

		if (!accountProfileDynamicDocumentAccountprofiles.isEmpty() && !filledFormDetails.isEmpty()) {
			for (AccountProfileDynamicDocumentAccountprofile accountProfileDynamicDocumentAccountprofile : accountProfileDynamicDocumentAccountprofiles) {
				for (FilledFormDetail filledFormDetail : filledFormDetails) {
					if (filledFormDetail.getFormElement().getId() == accountProfileDynamicDocumentAccountprofile
							.getFormElement().getId()) {
						map.put(accountProfileDynamicDocumentAccountprofile.getAccountProfleField(),
								filledFormDetail.getValue());
						break;
					}
				}
			}
		}
		return map;
	}

	@Override
	public List<UserDTO> findAllUniqueUsersFromExecutiveTaskExecution(String companyPid) {
		List<User> users = executiveTaskExecutionRepository.findAllUniqueUsersFromExecutiveTaskExecution(companyPid);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<UserDTO> getCountUniqueUsersFromExecutiveTaskExecutionAndCreateDateBetween(String companyPid,
			LocalDateTime monthStartDate, LocalDateTime monthEndDate) {
		List<User> users = executiveTaskExecutionRepository
				.getCountUniqueUsersFromExecutiveTaskExecutionAndCreateDateBetween(companyPid, monthStartDate,
						monthEndDate);
		List<UserDTO> result = users.stream().map(UserDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Long countAndUserNameByCompanyPidAndLocationInAndDateBetweenAndLocationType(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate,LocationType locationType) {
		log.debug("Request to Get Count of unable to find locations using company Pid: {}", companyPid);
		return executiveTaskExecutionRepository.countAndUserNameByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations,
				companyPid, fromDate, toDate,locationType);
	}

	@Override
	public List<ExecutiveTaskExecutionDTO> getByCompanyPidAndLocationInAndDateBetween(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		log.debug("Request to Get unable to find locations using company Pid: {}", companyPid);
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.getByCompanyPidAndLocationInAndDateBetween(locations, companyPid, fromDate, toDate);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<ExecutiveTaskExecutionDTO> getByCompanyPidAndLocationInAndDateBetweenAndLocationType(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate,LocationType locationType) {
		log.debug("Request to Get unable to find locations using company Pid: {}", companyPid);
		List<ExecutiveTaskExecution> executiveTaskExecutions = executiveTaskExecutionRepository
				.getByCompanyPidAndLocationInAndDateBetweenAndLocationType(locations, companyPid, fromDate, toDate,locationType);
		List<ExecutiveTaskExecutionDTO> result = executiveTaskExecutions.stream().map(ExecutiveTaskExecutionDTO::new)
				.collect(Collectors.toList());
		return result;
	}

}
