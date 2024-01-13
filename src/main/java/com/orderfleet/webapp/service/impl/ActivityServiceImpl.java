package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
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

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.ActivityStage;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.enums.ContactManagement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.ActivityStageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountTypeMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;

/**
 * Service Implementation for managing Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

	private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityMapper activityMapper;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountTypeMapper accountTypeMapper;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private StageRepository stageRepository;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	@Inject
	private ActivityStageRepository activityStageRepository;

	/**
	 * Save a activity.
	 * 
	 * @param activityDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityDTO save(ActivityDTO activityDTO) {
		log.debug("Request to save Activity : {}", activityDTO);

		// set pid
		activityDTO.setPid(ActivityService.PID_PREFIX + RandomUtil.generatePid());
		Activity activity = activityMapper.activityDTOToActivity(activityDTO);
		// code added to fix issue of modern sales uploading
		if (activity.getContactManagement() == null) {
			activity.setContactManagement(ContactManagement.ENABLED);
		}
		// set company
		activity.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activity = activityRepository.save(activity);
		ActivityDTO result = activityMapper.activityToActivityDTO(activity);
		return result;
	}

	@Override
	public void saveAssignedAccountTypes(String pid, String assignedAccountTypes) {
		log.debug("Request to save Assigned Account Types");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Activity activity = activityRepository.findOneByPid(pid).get();
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

		String[] accountTypes = assignedAccountTypes.split(",");
		Set<AccountType> activityAccountTypes = new HashSet<AccountType>();
		for (String acountTypePid : accountTypes) {
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 = "get one by pid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			AccountType accountType = accountTypeRepository.findOneByPid(acountTypePid).get();
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
			logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1
					+ "," + description1);

			activityAccountTypes.add(accountType);
		}
		activity.setActivityAccountTypes(activityAccountTypes);
		activityRepository.save(activity);
	}

	@Override
	public void saveAssignedDocuments(String pid, List<ActivityDocumentDTO> assignedDocuments) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Activity activity = activityRepository.findOneByPid(pid).get();
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

		List<ActivityDocument> listDocuments = new ArrayList<>();
		for (ActivityDocumentDTO activityDocumentDTO : assignedDocuments) {
			ActivityDocument activityDocument = new ActivityDocument();
			Document document = documentRepository.findOneByPid(activityDocumentDTO.getDocumentPid()).get();
			activityDocument.setDocument(document);
			activityDocument.setActivity(activity);
			activityDocument.setRequired(activityDocumentDTO.getRequired());
			activityDocument.setSortOrder(activityDocumentDTO.getSortOrder());
			listDocuments.add(activityDocument);
		}
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AD_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "delete by activty pid";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		activityDocumentRepository.deleteByActivityPid(pid);
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

		activityDocumentRepository.flush();
		activityDocumentRepository.save(listDocuments);
	}

	@Override
	public void saveAssignedDocumentsWithCompany(String pid, List<ActivityDocumentDTO> assignedDocuments) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Activity activity = activityRepository.findOneByPid(pid).get();

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
		List<ActivityDocument> listDocuments = new ArrayList<>();
		for (ActivityDocumentDTO activityDocumentDTO : assignedDocuments) {
			ActivityDocument activityDocument = new ActivityDocument();
			Document document = documentRepository.findOneByPid(activityDocumentDTO.getDocumentPid()).get();
			activityDocument.setDocument(document);
			activityDocument.setActivity(activity);
			activityDocument.setRequired(activityDocumentDTO.getRequired());
			activityDocument.setSortOrder(activityDocumentDTO.getSortOrder());
			activityDocument.setCompany(document.getCompany());
			listDocuments.add(activityDocument);
		}
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AD_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "delete by activtyId";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		activityDocumentRepository.deleteByActivityDocumentActivityId(activity.getId());
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

		activityDocumentRepository.flush();
		activityDocumentRepository.save(listDocuments);
	}

	@Override
	public void saveAssignedStages(String pid, String[] stagePids) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		activityRepository.findOneByPid(pid).ifPresent(activity -> {
			List<ActivityStage> activityStages = new ArrayList<>();
			for (String stagePid : stagePids) {
				ActivityStage activityStage = new ActivityStage();
				Stage stage = stageRepository.findOneByPid(stagePid).get();
				activityStage.setStage(stage);
				activityStage.setActivity(activity);
				activityStage.setCompany(activity.getCompany());
				activityStages.add(activityStage);
			}
			 DateTimeFormatter DATE_TIME_FORMATS = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMATS = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String ids = "AS_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String descriptions ="delete by activityPid";
				LocalDateTime startLCTimes= LocalDateTime.now();
				String startTimes = startLCTimes.format(DATE_TIME_FORMATS);
				String startDates = startLCTimes.format(DATE_FORMATS);
				logger.info(ids + "," + startDates + "," + startTimes + ",_ ,0 ,START,_," + descriptions);
			activityStageRepository.deleteByActivityPid(pid);
			 String flags = "Normal";
				LocalDateTime endLCTimes = LocalDateTime.now();
				String endTimes = endLCTimes.format(DATE_TIME_FORMATS);
				String endDates = startLCTimes.format(DATE_FORMATS);
				Duration durations = Duration.between(startLCTimes, endLCTimes);
				long minute = durations.toMinutes();
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
			activityStageRepository.flush();
			activityStageRepository.save(activityStages);
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

	/**
	 * Update a activity.
	 * 
	 * @param activityDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityDTO update(ActivityDTO activityDTO) {
		log.debug("Request to Update Activity : {}", activityDTO);

		return activityRepository.findOneByPid(activityDTO.getPid()).map(activity -> {
			activity.setName(activityDTO.getName());
			activity.setAlias(activityDTO.getAlias());
			activity.setDescription(activityDTO.getDescription());
			activity.setHasDefaultAccount(activityDTO.getHasDefaultAccount());
			activity.setTargetDisplayOnDayplan(activityDTO.getTargetDisplayOnDayplan());
			activity.setCompletePlans(activityDTO.getCompletePlans());
			activity.setContactManagement(activityDTO.getContactManagement());
			activity.setHasSecondarySales(activityDTO.getHasSecondarySales());
			activity.setGeoFencing(activityDTO.getGeoFencing());
			activity.setHasTelephonicOrder(activityDTO.getHasTelephonicOrder());
			activity.setEmailTocomplaint(activityDTO.getEmailTocomplaint());
			activity.setLocationRadius(activityDTO.getLocationRadius());
			activity.setSecondarySalesThroughApi(activityDTO.getSecondarySalesThroughApi());
			activity.setKmCalculationDisabled(activityDTO.getKmCalculationDisabled());
			activity.setAutoTaskCreation(activityDTO.getAutoTaskCreation());
			activity = activityRepository.save(activity);
			ActivityDTO result = activityMapper.activityToActivityDTO(activity);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Activity> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<Activity> result = activityRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findAllByCompany() {
		log.debug("Request to get all Activities");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and order by name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Activity> activityList = activityRepository.findAllByCompanyIdOrderByName();
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

		List<ActivityDTO> result = activityMapper.activitiesToActivityDTOs(activityList);
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
	public Page<ActivityDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId using page";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<Activity> activities = activityRepository.findAllByCompanyId(pageable);
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

		Page<ActivityDTO> result = new PageImpl<ActivityDTO>(
				activityMapper.activitiesToActivityDTOs(activities.getContent()), pageable,
				activities.getTotalElements());
		return result;
	}

	/**
	 * Get one activity by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityDTO findOne(Long id) {
		log.debug("Request to get Activity : {}", id);
		Activity activity = activityRepository.findOne(id);
		ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
		return activityDTO;
	}

	/**
	 * Get one activity by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityDTO> findOneByPid(String pid) {
		log.debug("Request to get Activity by pid : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<ActivityDTO> actDTO = activityRepository.findOneByPid(pid).map(activity -> {
			ActivityDTO activityDTO = new ActivityDTO(activity);
			return activityDTO;
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
		return actDTO;

	}

	/**
	 * Get one activity by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityDTO> findByName(String name) {
		log.debug("Request to get Activity by name : {}", name);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by compId and name ignore";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<ActivityDTO> activityDto = activityRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name).map(activity -> {
					ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
					return activityDTO;
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
		return activityDto;

	}

	/**
	 * Delete the activity by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Activity : {}", pid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		activityRepository.findOneByPid(pid).ifPresent(activity -> {
			activityRepository.delete(activity.getId());
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

	@Override
	@Transactional(readOnly = true)
	public List<AccountTypeDTO> findActivityAccountTypesByPid(String pid) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get activity accounts by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountType> accountTypes = activityRepository.findActivityAccountTypesByPid(pid);
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

		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypes);
		return result;
	}

	/**
	 * Update the activity status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public ActivityDTO updateActivityStatus(String pid, boolean active) {
		log.debug("Request to update Activity  status: {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			ActivityDTO actDTO= activityRepository.findOneByPid(pid).map(activity -> {
			if (!active) {
				Set<AccountType> activityAccountTypes = null;
				DateTimeFormatter DATE_TIME_FORMATS = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMATS = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String ids = "AD_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String descriptions = "delete by activty pid";
				LocalDateTime startLCTimes = LocalDateTime.now();
				String startTimes = startLCTime.format(DATE_TIME_FORMAT);
				String startDates = startLCTime.format(DATE_FORMAT);
				logger.info(ids + "," + startDates + "," + startTimes + ",_ ,0 ,START,_," + descriptions);
				activityDocumentRepository.deleteByActivityPid(pid);
				String flags = "Normal";
				LocalDateTime endLCTimes = LocalDateTime.now();
				String endTimes = endLCTimes.format(DATE_TIME_FORMATS);
				String endDates = startLCTimes.format(DATE_FORMATS);
				Duration durations = Duration.between(startLCTimes, endLCTimes);
				long minute = durations.toMinutes();
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

				activityDocumentRepository.flush();
				activity.setActivityAccountTypes(activityAccountTypes);
			}
			activity.setActivated(active);
			activity = activityRepository.save(activity);
			ActivityDTO result = activityMapper.activityToActivityDTO(activity);
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
					return actDTO;
	}

	@Override
	public Page<ActivityDTO> findAllByCompanyAndActivatedActivityOrderByName(Pageable pageable, boolean active) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId and activated activity";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<Activity> pageActivity = activityRepository.findAllByCompanyIdAndActivatedActivityOrderByName(pageable,
				active);
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
		Page<ActivityDTO> pageActivityDTO = new PageImpl<ActivityDTO>(
				activityMapper.activitiesToActivityDTOs(pageActivity.getContent()), pageable,
				pageActivity.getTotalElements());
		return pageActivityDTO;
	}

	@Override
	public List<ActivityDTO> findAllByCompanyAndDeactivatedActivity(boolean deactive) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and deactivated activity";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Activity> activities = activityRepository.findAllByCompanyIdAndActivatedOrDeactivatedActivity(deactive);
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

		List<ActivityDTO> activityDTOs = activityMapper.activitiesToActivityDTOs(activities);
		return activityDTOs;
	}

	@Override
	public List<ActivityDTO> findAllByCompanyPid(String companyPid) {
		log.debug("Request to get all Activities by companyPid");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Activity> activityList = activityRepository.findAllByCompanyPid(companyPid);
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

		List<ActivityDTO> result = activityMapper.activitiesToActivityDTOs(activityList);
		return result;
	}

	@Override
	public ActivityDTO saveFormSAdmin(ActivityDTO activityDTO) {
		log.debug("Request to save Activity : {}", activityDTO);
		Optional<Company> opCompany = companyRepository.findOneByPid(activityDTO.getCompanyPid());
		if (opCompany.isPresent()) {
			// set pid
			activityDTO.setPid(ActivityService.PID_PREFIX + RandomUtil.generatePid());
			Activity activity = activityMapper.activityDTOToActivity(activityDTO);
			// set company
			activity.setCompany(opCompany.get());
			activity = activityRepository.save(activity);
			ActivityDTO result = activityMapper.activityToActivityDTO(activity);
			return result;
		}
		return null;
	}

	/**
	 * Get one activity by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityDTO> findByNameAndCompanyPid(String name, String companyPid) {
		log.debug("Request to get Activity by name : {}", name);
		return activityRepository.findByCompanyPidAndNameIgnoreCase(companyPid, name).map(activity -> {
			ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
			return activityDTO;
		});
	}
}
