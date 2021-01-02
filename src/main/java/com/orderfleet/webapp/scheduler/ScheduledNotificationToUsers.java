package com.orderfleet.webapp.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.FirebaseService;
import com.orderfleet.webapp.web.rest.api.NotificationController;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;

@Component
public class ScheduledNotificationToUsers {

	private final Logger log = LoggerFactory.getLogger(NotificationController.class);

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private UserDeviceRepository userDeviceRepository;

	@Inject
	private FirebaseService firebaseService;

	private FirebaseRequest firebaseRequest;
	private List<UserDevice> userDevices;

	@Scheduled(cron = "0 0 3 * * ?")
//	@Scheduled(cron = "50 * * * * ?")
	public void sendNotificationBasedOnPlan() {
		System.out.println("Executing notification based on plan");
		List<ExecutiveTaskPlan> executiveTaskPlanList;
		List<Long> companyIds = new ArrayList<>();
		companyIds.add(304935L);
		LocalDate currentDate = LocalDate.now();
		executiveTaskPlanList = executiveTaskPlanRepository.findByPlannedDateBetweenAndCompanyIdOrderByIdAsc(
				currentDate.atTime(0, 0), currentDate.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
		for (ExecutiveTaskPlan etp : executiveTaskPlanList) {
			if (companyIds.stream().filter(id -> id.longValue() == etp.getCompany().getId().longValue()).findAny()
					.isPresent()) {
				System.out.println(etp.getAccountProfile().getName());
				System.out.println(etp.getPlannedDate().toString());
				System.out.println(etp.getActivity().getName());
				System.out.println(etp.getUser().getFirstName());
				sendTaskNotificationToUsers(etp);
			}
		}
		log.info("***********************************************");
		System.out.println("Executing notification based on plan....");
	}

	private void sendTaskNotificationToUsers(ExecutiveTaskPlan executiveTaskPlan) {
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

		// send notifications
		List<UserDevice> userDevices = userDeviceRepository
				.findAllUserDeviceByCompanyAndLastModifiedDate(executiveTaskPlan.getCompany().getPid());
		userDevices.removeIf(ud -> !ud.getUser().getPid().equals(executiveTaskPlan.getUser().getPid()));
		if (CollectionUtils.isEmpty(userDevices))
			return;

		String[] usersFcmKeys = userDevices.stream().map(ud -> ud.getFcmKey()).toArray(String[]::new);
		for (String fcm : usersFcmKeys) {
			log.info(fcm);
			log.info("----------------------------------------");
		}
		FirebaseRequest firebaseRequest = new FirebaseRequest();
		firebaseRequest.setRegistrationIds(usersFcmKeys);

		String activityName = executiveTaskPlan.getActivity().getName();
		String accountName = executiveTaskPlan.getAccountProfile().getName();
		String taskCreatedUser = executiveTaskPlan.getUser().getLogin();

		String executionDate = formatter.format(executiveTaskPlan.getPlannedDate());
		FirebaseData data = new FirebaseData();
		data.setTitle("Remainder Notification");
		data.setMessage(activityName + " is  scheduled with customer : " + accountName + " on, Date :" + executionDate);
		data.setMessageType(NotificationMessageType.ACCOUNT_TASK);
		data.setPidUrl("");
		data.setNotificationPid("");
		data.setSentDate(LocalDateTime.now().toString());
		firebaseRequest.setData(data);

		setFirebaseRequest(firebaseRequest);
		setUserDevices(userDevices);
		// task-usertask send notification asynchronous
		firebaseService.sendNotificationToUsers(firebaseRequest, userDevices, taskCreatedUser);
		// firebaseService.sendSynchronousNotificationToUsers(firebaseRequest,
		// taskCreatedUser);
	}

	private void setFirebaseRequest(FirebaseRequest firebaseRequest) {
		this.firebaseRequest = firebaseRequest;
	}

	private void setUserDevices(List<UserDevice> userDevices) {
		this.userDevices = userDevices;
	}

}
