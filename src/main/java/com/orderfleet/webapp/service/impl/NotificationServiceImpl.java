package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.domain.NotificationDetail;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.enums.MessageStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.NotificationDetailRepository;
import com.orderfleet.webapp.repository.NotificationRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.NotificationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponseResult;
import com.orderfleet.webapp.web.rest.dto.NotificationDTO;

/**
 * Service Implementation for managing Notification.
 * 
 * @author Sarath
 * @since Sep 19, 2016
 */

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

	private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Inject
	private NotificationRepository notificationRepository;

	@Inject
	private NotificationDetailRepository notificationDetailRepository;

	@Inject
	private UserDeviceRepository userDevicesRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Override
	public Notification saveNotification(NotificationDTO notificationDTO) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (!optionalUser.isPresent()) {
			return null;
		}

		Notification notification = new Notification();
		notification.setPid(NotificationService.PID_PREFIX + RandomUtil.generatePid());
		notification.setTitle(notificationDTO.getTitle());
		notification.setMessage(notificationDTO.getMessage());
		notification.setisImportant(notificationDTO.getIsImportant());
		notification.setResendTime(notificationDTO.getResendTime());
		notification.setCreatedBy(optionalUser.get());
		notification.setCompany(company);

		List<NotificationDetail> notificationDetails = new ArrayList<>();
		List<UserDevice> userDevices = userDevicesRepository
				.findByCompanyIdAndActivatedAndUserDevicePidIn(notificationDTO.getUsers());
		for (UserDevice userDevice : userDevices) {
			NotificationDetail notificationDetail = new NotificationDetail();
			notificationDetail.setNotification(notification);
			notificationDetail.setUserDeviceId(userDevice.getId());
			notificationDetail.setMessageStatus(MessageStatus.NONE);
			notificationDetail.setFcmKey(userDevice.getFcmKey());
			notificationDetail.setUserId(userDevice.getUser().getId());
			notificationDetail.setUserPid(userDevice.getUser().getPid());
			notificationDetail.setCompany(company);
			notificationDetails.add(notificationDetail);
		}
		notification = notificationRepository.save(notification);
		notificationDetails = notificationDetailRepository.save(notificationDetails);
		notification.setNotificationDetails(notificationDetails);
		return notification;
	}

	@Override
	public List<NotificationDTO> getAllNotificationsByIsImportantTrueAndResendTime(Long count) {
		List<Notification> notifications = notificationRepository
				.getAllNotificationsByIsImportantTrueAndResendTime(count);
		List<NotificationDTO> result = notifications.stream().map(NotificationDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateNotificationWithFirebaseResponse(Notification notification, String[] usersFcmKeys,
			FirebaseResponse response) {
		notification.setSuccess(response.getSuccess());
		notification.setFailure(response.getFailure());
		notificationRepository.save(notification);

		List<FirebaseResponseResult> firebaseResults = response.getResults();
		List<NotificationDetail> notificatinDetails = notification.getNotificationDetails();
		int size = firebaseResults.size();
		for (int i = 0; i < size; i++) {
			FirebaseResponseResult result = firebaseResults.get(i);
			String fcmKey = usersFcmKeys[i];
			Optional<NotificationDetail> opNotificationDetail = notificatinDetails.stream()
					.filter(nd -> nd.getFcmKey().equals(fcmKey)).findAny();
			if (opNotificationDetail.isPresent()) {
				NotificationDetail notificationDetail = opNotificationDetail.get();
				if (result.getError() != null && !result.getError().isEmpty()) {
					log.info("Notification Fire Base Error:- " + result.getError());
					switch (result.getError()) {

					case "NotRegistered":
						notificationDetail.setMessageStatus(MessageStatus.FAILED);
						notificationDetail.setFailedReason(
								"The application was uninstalled from the device, or the client app isn't configured to receive messages");
						break;
					case "InvalidRegistration":
						notificationDetail.setMessageStatus(MessageStatus.FAILED);
						notificationDetail.setFailedReason("Malformed registration token");
						break;
					default:
						notificationDetail.setMessageStatus(MessageStatus.FAILED);
						notificationDetail.setFailedReason(
								"There is something wrong in the registration token passed in the request");
						break;
					}
					notificationDetailRepository.save(notificationDetail);
				} else {
					notificationDetail.setMessageStatus(MessageStatus.SUCCESS);
					notificationDetailRepository.save(notificationDetail);
				}
			}
		}
	}
}
