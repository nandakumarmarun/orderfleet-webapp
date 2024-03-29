package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityNotification;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.NotificationType;
import com.orderfleet.webapp.repository.ActivityNotificationRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityNotificationService;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;

/**
 * Service Implimentation for Activity Notification
 *
 * @author fahad
 * @since May 10, 2017
 */
@Service
@Transactional
public class ActivityNotificationServiceImpl implements ActivityNotificationService {

	private final Logger log = LoggerFactory.getLogger(ActivityNotificationServiceImpl.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private final ActivityNotificationRepository activityNotificationRepository;

	private final ActivityRepository activityRepository;

	private final DocumentRepository documentRepository;

	private final CompanyRepository companyRepository;

	public ActivityNotificationServiceImpl(ActivityNotificationRepository activityNotificationRepository,
			CompanyRepository companyRepository, ActivityRepository activityRepository,
			DocumentRepository documentRepository) {
		super();
		this.activityNotificationRepository = activityNotificationRepository;
		this.activityRepository = activityRepository;
		this.documentRepository = documentRepository;
		this.companyRepository = companyRepository;
	}

	/**
	 * Get all the activityNotifications.
	 * 
	 * @return the list of entities
	 */
	@Override
	public List<ActivityNotificationDTO> findAllActivityNotificationByCompanyId() {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AN_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compId";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<ActivityNotification> activityNotifications = activityNotificationRepository.findAllByCompanyId();
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
		List<ActivityNotificationDTO> activityNotificationDTOs = setToActivityNotificationDTO(activityNotifications);
		return activityNotificationDTOs;
	}

	/**
	 * Save a Activity Notification.
	 * 
	 * @param activityPid
	 *            the entity to save
	 * @param documentPid
	 *            the entity to save
	 * @param notificationType
	 *            the entity to save
	 */
	@Override
	public void save(ActivityNotificationDTO activityNotificationDTO) {
		log.debug("Request to save  Activity Notification");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Activity activity = activityRepository.findOneByPid(activityNotificationDTO.getActivityPid()).get();
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

		Document document = documentRepository.findOneByPid(activityNotificationDTO.getDocumentPid()).get();
		ActivityNotification activityNotification = new ActivityNotification();
		activityNotification.setDocument(document);
		activityNotification.setActivity(activity);
		activityNotification.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activityNotification.setNotificationType(activityNotificationDTO.getNotificationType());
		activityNotification.setOther(activityNotificationDTO.getOther());
		activityNotification.setSendCustomer(activityNotificationDTO.getSendCustomer());
		activityNotification.setPhoneNumbers(activityNotificationDTO.getPhoneNumbers());
		activityNotificationRepository.save(activityNotification);

	}

	public List<ActivityNotificationDTO> setToActivityNotificationDTO(
			List<ActivityNotification> activityNotifications) {
		List<ActivityNotificationDTO> activityNotificationDTOs = new ArrayList<ActivityNotificationDTO>();
		for (ActivityNotification activityNotification : activityNotifications) {
			ActivityNotificationDTO activityNotificationDTO = new ActivityNotificationDTO(activityNotification);
			activityNotificationDTOs.add(activityNotificationDTO);
		}
		return activityNotificationDTOs;

	}

	/**
	 * Get one activityNotification by activityPid,documentPid,notificationType.
	 *
	 * @param activityPid
	 *            the activityPid of the entity
	 * @param documentPid
	 *            the documentPid of the entity
	 * @param notificationType
	 *            the notificationType of the entity
	 * @return the entity
	 */
	@Override
	public Optional<ActivityNotificationDTO> findActivityNotificationByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(
			String activityPid, String documentPid, NotificationType notificationType) {
		// NotificationType notificationType2 = null;
		// if (notificationType.equals("SMS")) {
		// notificationType2 = NotificationType.SMS;
		// }
		// if (notificationType.equals("PUSH")) {
		// notificationType2 = NotificationType.PUSH;
		// }
		// if (notificationType.equals("EMAIL")) {
		// notificationType2 = NotificationType.EMAIL;
		// }
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AN_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by compId activityPid and doc Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<ActivityNotificationDTO> anDTO= activityNotificationRepository.findOneByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(
				activityPid, documentPid, notificationType).map(activityNotification -> {
					ActivityNotificationDTO activityNotificationDTO = new ActivityNotificationDTO(activityNotification);
					return activityNotificationDTO;
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
					return anDTO;
	}

	/**
	 * Delete the activityNotification by
	 * activityPid,documentPid,notificationType.
	 * 
	 * @param activityPid
	 *            the activityPid of the entity
	 * @param documentPid
	 *            the documentPid of the entity
	 * @param notificationType
	 *            the notificationType of the entity
	 */
	@Override
	public void delete(String activityPid, String documentPid, String notificationType) {
		NotificationType notificationType2 = null;
		log.debug("Request to delete Activity Notification : {}", activityPid);
		if (notificationType.equals("SMS")) {
			notificationType2 = NotificationType.SMS;
		}
		if (notificationType.equals("PUSH")) {
			notificationType2 = NotificationType.PUSH;
		}
		if (notificationType.equals("EMAIL")) {
			notificationType2 = NotificationType.EMAIL;
		}
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AN_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by compId activityPid and doc Pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		activityNotificationRepository.findOneByCompanyIdAndActivityPidAndDocumentPidAndNotificationType(activityPid,
				documentPid, notificationType2).ifPresent(activityNotification -> {
					activityNotificationRepository.delete(activityNotification.getId());
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
	 * Get one activityNotification by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	public ActivityNotificationDTO findActivityNotificationById(long id) {

		ActivityNotification activityNotification = activityNotificationRepository.findOne(id);
		ActivityNotificationDTO activityNotificationDTO = new ActivityNotificationDTO(activityNotification);
		return activityNotificationDTO;
	}

	/**
	 * update a Activity Notification.
	 * 
	 * @param activityPid
	 *            the entity to update
	 * @param documentPid
	 *            the entity to update
	 * @param notificationType
	 *            the entity to update
	 * @param id
	 *            the entity to update
	 */
	@Override
	public void update(ActivityNotificationDTO activityNotificationDTO) {

		log.debug("Request to update  Activity Notification");

		ActivityNotification activityNotification = activityNotificationRepository
				.findOne(activityNotificationDTO.getId());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Activity activity = activityRepository.findOneByPid(activityNotificationDTO.getActivityPid()).get();
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
		Document document = documentRepository.findOneByPid(activityNotificationDTO.getDocumentPid()).get();
		activityNotification.setDocument(document);
		activityNotification.setActivity(activity);
		activityNotification.setNotificationType(activityNotificationDTO.getNotificationType());
		activityNotification.setOther(activityNotificationDTO.getOther());
		activityNotification.setSendCustomer(activityNotificationDTO.getSendCustomer());
		activityNotification.setPhoneNumbers(activityNotificationDTO.getPhoneNumbers());
		activityNotificationRepository.save(activityNotification);

	}

}
