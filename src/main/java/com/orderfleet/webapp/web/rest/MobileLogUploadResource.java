package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.domain.NotificationDetail;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.MessageStatus;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.NotificationDetailRepository;
import com.orderfleet.webapp.repository.NotificationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.NotificationService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.async.FirebaseService;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;
import com.orderfleet.webapp.web.rest.dto.NotificationDTO;
import com.orderfleet.webapp.web.rest.dto.NotificationStatusDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class MobileLogUploadResource {

	private final Logger log = LoggerFactory.getLogger(MobileLogUploadResource.class);

	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	private FirebaseService firebaseService;

	private NotificationService notificationService;

	private UserDeviceService userDeviceService;

	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private NotificationDetailRepository notificationDetailRepository;

	@Inject
	private NotificationRepository notificationRepository;

	@Inject
	public MobileLogUploadResource(FirebaseService firebaseService, NotificationService notificationService,
			UserDeviceService userDeviceService, EmployeeHierarchyService employeeHierarchyService) {
		super();
		this.firebaseService = firebaseService;
		this.notificationService = notificationService;
		this.userDeviceService = userDeviceService;
		this.employeeHierarchyService = employeeHierarchyService;
	}

	@RequestMapping(value = "/mobile-log-upload", method = RequestMethod.GET)
	@Timed
	public String mobileLOgUpload(Model model) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("userDevices", userDeviceService.findAllByCompanyIdAndActivatedTrueConsistEmployee());
		} else {
			model.addAttribute("userDevices",
					userDeviceService.findByCompanyIdAndActivatedTrueAndUserPidInConsistEmployee(userIds));
		}

		return "company/mobileLogUpload";
	}

	@RequestMapping(value = "/mobile-log-upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotificationStatusDTO> sendNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
		log.debug("Rest request to send push notification {}", notificationDTO);
		// validateMessage
		if (notificationDTO.getMessage() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("notification", "messageEmpty", "Message cannot be empty"))
					.body(null);
		}
		// validateUserIdsToSend
		if (notificationDTO.getUsers() == null || notificationDTO.getUsers().isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("notification", "userIdsEmpty", "UserIds cannot be empty"))
					.body(null);
		}
		// save notification to DB
		Notification notification = notificationService.covertNotificationDTOtoNotification(notificationDTO);
		if (notification == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("notification", "loginEmpty", "Please re-login and send message."))
					.body(null);
		}

		NotificationStatusDTO notificationStatusDTO = pushNotificationToFCM(notification);

		return new ResponseEntity<>(notificationStatusDTO, HttpStatus.CREATED);
	}

	private NotificationStatusDTO pushNotificationToFCM(Notification notification) {
		// create fcm request object
		User createdUser = notification.getCreatedBy();
		String[] usersFcmKeys = notification.getNotificationDetails().stream().map(NotificationDetail::getFcmKey)
				.toArray(String[]::new);
		FirebaseRequest firebaseRequest = new FirebaseRequest();
		firebaseRequest.setRegistrationIds(usersFcmKeys);
		if (notification.getIsImportant()) {
			firebaseRequest.setPriority("high");
		}
		FirebaseData data = new FirebaseData();
		data.setTitle(notification.getTitle() == null ? "Message" : notification.getTitle());
		data.setMessage(notification.getMessage() + "\n\ncreated by : " + createdUser.getFirstName() + " "
				+ createdUser.getLastName());

		data.setMessageType(NotificationMessageType.INFO);

		data.setPidUrl(notification.getPid());
		data.setNotificationPid("");
		data.setSentDate(LocalDateTime.now().toString());
		firebaseRequest.setData(data);
		try {
			FirebaseResponse response = firebaseService.sendSynchronousNotificationToUsers(firebaseRequest,
					createdUser.getLogin());
			NotificationStatusDTO notificationStatusDTO = new NotificationStatusDTO();
			notificationStatusDTO.setSuccess(response.getSuccess());
			notificationStatusDTO.setFailed(response.getFailure());
			notificationStatusDTO.setTotal(response.getSuccess() + response.getFailure());
			// notificationService.updateNotificationWithFirebaseResponse(notification,
			// usersFcmKeys, response);
			return notificationStatusDTO;
		} catch (Exception e) {
			log.warn("Notification could not be sent to devices '{}'",
					Arrays.toString(firebaseRequest.getRegistrationIds()), e);
		}
		return null;
	}

}
