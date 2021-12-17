package com.orderfleet.webapp.service.impl;

import java.time.Duration;
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

import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.ActivityGroupRepository;
import com.orderfleet.webapp.repository.ActivityGroupUserTargetRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityGroupUserTargetService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupUserTargetMapper;

/**
 * Service Implementation for managing ActivityGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Service
@Transactional
public class ActivityGroupUserTargetServiceImpl implements ActivityGroupUserTargetService {

	private final Logger log = LoggerFactory.getLogger(ActivityGroupUserTargetServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ActivityGroupUserTargetRepository activityGroupUserTargetRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityGroupUserTargetMapper activityGroupUserTargetMapper;

	@Inject
	private ActivityGroupRepository activityGroupRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a activityGroupUserTarget.
	 * 
	 * @param activityGroupUserTargetDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityGroupUserTargetDTO save(ActivityGroupUserTargetDTO activityGroupUserTargetDTO) {
		log.debug("Request to save ActivityGroupUserTarget : {}", activityGroupUserTargetDTO);

		// set pid
		activityGroupUserTargetDTO.setPid(ActivityGroupUserTargetService.PID_PREFIX + RandomUtil.generatePid());
		ActivityGroupUserTarget activityGroupUserTarget = activityGroupUserTargetMapper
				.activityGroupUserTargetDTOToActivityGroupUserTarget(activityGroupUserTargetDTO);
		// set company
		activityGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityGroupUserTarget = activityGroupUserTargetRepository.save(activityGroupUserTarget);
		ActivityGroupUserTargetDTO result = activityGroupUserTargetMapper
				.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
		return result;
	}

	/**
	 * Update a activityGroupUserTarget.
	 * 
	 * @param activityGroupUserTargetDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityGroupUserTargetDTO update(ActivityGroupUserTargetDTO activityGroupUserTargetDTO) {
		log.debug("Request to Update ActivityGroupUserTarget : {}", activityGroupUserTargetDTO);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AGUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		ActivityGroupUserTargetDTO agutdto= activityGroupUserTargetRepository.findOneByPid(activityGroupUserTargetDTO.getPid())
				.map(activityGroupUserTarget -> {
					activityGroupUserTarget.setTargetNumber(activityGroupUserTargetDTO.getTargetNumber());
					activityGroupUserTarget.setStartDate(activityGroupUserTargetDTO.getStartDate());
					activityGroupUserTarget.setEndDate(activityGroupUserTargetDTO.getEndDate());
					 DateTimeFormatter DATE_TIME_FORMATS = DateTimeFormatter.ofPattern("hh:mm:ss a");
						DateTimeFormatter DATE_FORMATS = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						String ids = "AG_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
						String descriptions ="get one by pid";
						LocalDateTime startLCTimes = LocalDateTime.now();
						String startTimes = startLCTimes.format(DATE_TIME_FORMATS);
						String startDates = startLCTimes.format(DATE_FORMATS);
						logger.info(ids + "," + startDates + "," + startTimes + ",_ ,0 ,START,_," + descriptions);
					activityGroupUserTarget.setActivityGroup(activityGroupRepository
							.findOneByPid(activityGroupUserTargetDTO.getActivityGroupPid()).get());
					 String flag = "Normal";
						LocalDateTime endLCTimes = LocalDateTime.now();
						String endTimes= endLCTimes.format(DATE_TIME_FORMATS);
						String endDates = startLCTimes.format(DATE_FORMATS);
						Duration durations = Duration.between(startLCTimes, endLCTimes);
						long minute = durations.toMinutes();
						if (minute <= 1 && minute >= 0) {
							flag = "Fast";
						}
						if (minute > 1 && minute <= 2) {
							flag = "Normal";
						}
						if (minute > 2 && minute <= 10) {
							flag = "Slow";
						}
						if (minute > 10) {
							flag = "Dead Slow";
						}
				                logger.info(ids + "," + endDates + "," + startTimes + "," + endTimes + "," + minute + ",END," + flag + ","
								+ descriptions);

					activityGroupUserTarget
							.setUser(userRepository.findOneByPid(activityGroupUserTargetDTO.getUserPid()).get());
					activityGroupUserTarget = activityGroupUserTargetRepository.save(activityGroupUserTarget);
					ActivityGroupUserTargetDTO result = activityGroupUserTargetMapper
							.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
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
					return agutdto;

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
	public Page<ActivityGroupUserTarget> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<ActivityGroupUserTarget> result = activityGroupUserTargetRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityGroupUserTargetDTO> findAllByCompany() {
		log.debug("Request to get all TaskHeaders");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AGUT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityGroupUserTarget> activityGroupUserTargetList = activityGroupUserTargetRepository
				.findAllByCompanyId();
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
		List<ActivityGroupUserTargetDTO> result = activityGroupUserTargetMapper
				.activityGroupUserTargetsToActivityGroupUserTargetDTOs(activityGroupUserTargetList);
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
	public Page<ActivityGroupUserTargetDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AGUT_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId using page";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<ActivityGroupUserTarget> activities = activityGroupUserTargetRepository.findAllByCompanyId(pageable);
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

		Page<ActivityGroupUserTargetDTO> result = new PageImpl<ActivityGroupUserTargetDTO>(activityGroupUserTargetMapper
				.activityGroupUserTargetsToActivityGroupUserTargetDTOs(activities.getContent()), pageable,
				activities.getTotalElements());
		return result;
	}

	/**
	 * Get one activityGroupUserTarget by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityGroupUserTargetDTO findOne(Long id) {
		log.debug("Request to get ActivityGroupUserTarget : {}", id);
		ActivityGroupUserTarget activityGroupUserTarget = activityGroupUserTargetRepository.findOne(id);
		ActivityGroupUserTargetDTO activityGroupUserTargetDTO = activityGroupUserTargetMapper
				.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
		return activityGroupUserTargetDTO;
	}

	/**
	 * Get one activityGroupUserTarget by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityGroupUserTargetDTO> findOneByPid(String pid) {
		log.debug("Request to get ActivityGroupUserTarget by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AGUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<ActivityGroupUserTargetDTO> agutDTO= activityGroupUserTargetRepository.findOneByPid(pid).map(activityGroupUserTarget -> {
			ActivityGroupUserTargetDTO activityGroupUserTargetDTO = activityGroupUserTargetMapper
					.activityGroupUserTargetToActivityGroupUserTargetDTO(activityGroupUserTarget);
			return activityGroupUserTargetDTO;
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
					return agutDTO;

	}

	/**
	 * Delete the activityGroupUserTarget by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete ActivityGroupUserTarget : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AGUT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		activityGroupUserTargetRepository.findOneByPid(pid).ifPresent(activityGroupUserTarget -> {
			activityGroupUserTargetRepository.delete(activityGroupUserTarget.getId());
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
