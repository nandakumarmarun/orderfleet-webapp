package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate1 = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
		activityUserTarget.setActivity(activityRepository.findOneByPid(monthlyTargetDTO.getActivityPid()).get());
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate1 = startLCTime.format(DATE_FORMAT);
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
	                logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		 ActivityUserTargetDTO AUTDTO= activityUserTargetRepository.findOneByPid(activityUserTargetDTO.getPid()).map(activityUserTarget -> {
			activityUserTarget.setStartDate(activityUserTargetDTO.getStartDate());
			activityUserTarget.setEndDate(activityUserTargetDTO.getEndDate());
			DateTimeFormatter DATE_TIME_FORMATS = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMATS= DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String ids = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String descriptions ="get one by pid";
			LocalDateTime startLCTimes = LocalDateTime.now();
			String startTimes = startLCTimes.format(DATE_TIME_FORMATS);
			String startDates = startLCTimes.format(DATE_FORMATS);
			logger.info(ids + "," + startDates + "," + startTimes + ",_ ,0 ,START,_," + descriptions);
			activityUserTarget
					.setActivity(activityRepository.findOneByPid(activityUserTargetDTO.getActivityPid()).get());
			String flags = "Normal";
			LocalDateTime endLCTimes = LocalDateTime.now();
			String endTimes = endLCTimes.format(DATE_TIME_FORMATS);
			String endDates= startLCTimes.format(DATE_FORMATS);
			Duration durations = Duration.between(startLCTimes, endLCTimes);
			long minute= durations.toMinutes();
			if (minute <= 1 && minute >= 0) {
				flags = "Fast";
			}
			if (minute > 1 && minute <= 2) {
				flags = "Normal";
			}
			if (minute > 2 && minute <= 10) {
				flags = "Slow";
			}
			if (minute > 10) {
				flags = "Dead Slow";
			}
	                logger.info(ids + "," + endDates + "," + startTimes + "," + endTimes + "," + minute + ",END," + flags + ","
					+ descriptions);
			activityUserTarget.setUser(userRepository.findOneByPid(activityUserTargetDTO.getUserPid()).get());
			activityUserTarget.setTargetNumber(activityUserTargetDTO.getTargetNumber());
			activityUserTarget = activityUserTargetRepository.save(activityUserTarget);
			ActivityUserTargetDTO result = activityUserTargetMapper
					.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
			return result;
		}).orElse(null);
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
					return AUTDTO;
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AUT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityUserTarget> activityUserTargetList = activityUserTargetRepository.findAllByCompanyId();
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AUT_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId using page";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<ActivityUserTarget> activities = activityUserTargetRepository.findAllByCompanyId(pageable);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<ActivityUserTargetDTO> autDTO= activityUserTargetRepository.findOneByPid(pid).map(activityUserTarget -> {
			ActivityUserTargetDTO activityUserTargetDTO = activityUserTargetMapper
					.activityUserTargetToActivityUserTargetDTO(activityUserTarget);
			return activityUserTargetDTO;
		});
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
					return autDTO;
	}

	/**
	 * Delete the activityUserTarget by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ActivityUserTarget : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		activityUserTargetRepository.findOneByPid(pid).ifPresent(activityUserTarget -> {
			activityUserTargetRepository.delete(activityUserTarget.getId());
		});
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
	}

}
