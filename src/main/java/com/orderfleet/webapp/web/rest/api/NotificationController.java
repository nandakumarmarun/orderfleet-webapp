package com.orderfleet.webapp.web.rest.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.NotificationMessageRecipient;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.UserTaskAssignment;
import com.orderfleet.webapp.domain.enums.MessageStatus;
import com.orderfleet.webapp.domain.enums.PriorityStatus;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.NotificationMessageRecipientRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.repository.UserTaskAssignmentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserTaskAssignmentService;
import com.orderfleet.webapp.service.util.RandomUtil;

/**
 * REST controller for managing Notification.
 * 
 * @author Shaheer
 * @since November 11, 2016
 */
@RestController
@RequestMapping(value = "/api")
public class NotificationController {

	private final Logger log = LoggerFactory.getLogger(NotificationController.class);

	private NotificationMessageRecipientRepository notificationMessageRecipientRepository;
	
	private UserDeviceRepository userDeviceRepository;
	
	@Inject
	private TaskRepository taskRepository;
	
	@Inject
	private UserTaskAssignmentRepository userTaskAssignmentRepository;
	
	@Inject
	public NotificationController(NotificationMessageRecipientRepository notificationMessageRecipientRepository, UserDeviceRepository userDeviceRepository) {
		super();
		this.notificationMessageRecipientRepository = notificationMessageRecipientRepository;
		this.userDeviceRepository = userDeviceRepository;
	}

	/**
     * GET  /notifications : get all the NotificationMessages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of notificationMessages in body
     */
    @GetMapping("/notifications")
    @Timed
    public List<FirebaseData> getAllNotificationMessageByUserIsCurrentUser() {
        log.debug("REST request to get all NotificationMessage");
        List<NotificationMessageRecipient> nmRecipients = notificationMessageRecipientRepository.findByUserIsCurrentUser();
        return nmRecipients.stream().filter(nmr -> {
        	LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        	return nmr.getNotificationMessage().getExpiryDate().isAfter(oneMonthAgo);
        }).map(nmr -> nmr.getNotificationMessage().getMessageData()).collect(Collectors.toList());
    }
    
	@PostMapping(path = "/notifications/message-status", produces = { MediaType.APPLICATION_JSON_VALUE })
	@Timed
	public ResponseEntity<String> updateNotificationStatus(@RequestParam MessageStatus messageStatus, @RequestParam String notificationPid, @RequestParam(required = false) String taskPid, @RequestParam(required = false) LocalDate planDate) {
		Optional<UserDevice> optionalDevice = this.userDeviceRepository
				.findByUserLoginAndActivatedTrue(SecurityUtils.getCurrentUserLogin());
		if (optionalDevice.isPresent()) {
			Optional<NotificationMessageRecipient> nmrOptional = notificationMessageRecipientRepository
					.findOneByNotificationMessagePidAndUserDeviceId(notificationPid, optionalDevice.get().getId());
			if (nmrOptional.isPresent()) {
				NotificationMessageRecipient savedNotificationMessageRecipient = nmrOptional.get();
				savedNotificationMessageRecipient.setMessageStatus(messageStatus);
				notificationMessageRecipientRepository.save(savedNotificationMessageRecipient);
				//assign task to this user
				if(messageStatus.equals(MessageStatus.ACCEPTED)) {
					taskRepository.findOneByPid(taskPid).ifPresent(task -> assignTaskToUser(task, optionalDevice.get().getUser(), planDate));
				}
			}
			return new ResponseEntity<>("Updated Successfully", HttpStatus.OK);
		}
		return new ResponseEntity<>("No device found for this user", HttpStatus.OK);
	}
	
	private void assignTaskToUser(Task task, User user, LocalDate startDate) {
		UserTaskAssignment userTaskAssignment = new UserTaskAssignment();
		userTaskAssignment.setCompany(user.getCompany());
		userTaskAssignment.setExecutiveUser(user);
		userTaskAssignment.setPid(UserTaskAssignmentService.PID_PREFIX + RandomUtil.generatePid());
		userTaskAssignment.setPriorityStatus(PriorityStatus.MEDIUM);
		userTaskAssignment.setRemarks(task.getRemarks());
		userTaskAssignment.setStartDate(startDate);
		userTaskAssignment.setTask(task);
		userTaskAssignment.setUser(user);
		userTaskAssignmentRepository.save(userTaskAssignment);
	}
}
