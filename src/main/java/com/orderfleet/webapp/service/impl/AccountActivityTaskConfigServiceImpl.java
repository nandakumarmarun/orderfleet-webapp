package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.orderfleet.webapp.domain.AccountActivityTaskConfig;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.AccountActivityTaskConfigRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountActivityTaskConfigService;
import com.orderfleet.webapp.service.TaskService;
import com.orderfleet.webapp.service.async.FirebaseService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountActivityTaskConfigDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;

@Service
public class AccountActivityTaskConfigServiceImpl implements AccountActivityTaskConfigService {
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	AccountActivityTaskConfigRepository activityConfigRepository;

	@Inject
	AccountTypeRepository accountTypeRepository;

	@Inject
	ActivityRepository activityRepository;

	@Inject
	private UserDeviceRepository userDeviceRepository;

	@Inject
	private FirebaseService firebaseService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileRepository;

	@Inject
	private TaskRepository taskRepository;

	@Override
	public AccountActivityTaskConfigDTO saveActivityAccountTypeConfig(AccountActivityTaskConfigDTO activityAccountTypeConfigDTO) {
		AccountActivityTaskConfig accountActivity = activityConfigRepository.save(convertActivityConfigDtoToDomain(activityAccountTypeConfigDTO));
		AccountActivityTaskConfigDTO dto = new AccountActivityTaskConfigDTO(accountActivity);
		return dto;
	}

	@Override
	public void createTaskAndSendNotification(LeadManagementDTO leadmanagementDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountProfile> opAccountProfile = accountProfileRepository.findOneByPid(leadmanagementDTO.getPid());
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
		if(!opAccountProfile.isPresent()) {
			return;
		}
		AccountProfile accountProfile = opAccountProfile.get();
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "AATC_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 ="get activity by accountType pid";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountActivityTaskConfig> activityAccountType = activityConfigRepository
				.findActivityByAccountTypePid(accountProfile.getAccountType().getPid());
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

		Map<String, String> activityNamesAndTaskPid = new HashMap<>();
		for (AccountActivityTaskConfig activityConfig : activityAccountType) {
			Task task = taskRepository.save(createTask(accountProfile, activityConfig.getActivity(), leadmanagementDTO.getRemarks()));
			if(activityConfig.getAssignNotification()) {
				activityNamesAndTaskPid.put(activityConfig.getActivity().getName(), task.getPid());
			}
		}
		if(!activityNamesAndTaskPid.isEmpty()) {
			sendContactCreatedNotification(leadmanagementDTO, activityNamesAndTaskPid);
		}
		
	}
	
	private void sendContactCreatedNotification(LeadManagementDTO leadmanagementDTO, Map<String, String> activityNamesAndTaskPid) {
		Set<Long> userIds = employeeProfileRepository.findUserIdByLocationPid(leadmanagementDTO.getLocationPid());
		List<UserDevice> userDevices = userDeviceRepository.findByUserIdInAndActivatedTrue(userIds);
		if (CollectionUtils.isEmpty(userDevices)) {
			return;
		}
		String[] usersFcmKeys = userDevices.stream().map(ud -> ud.getFcmKey()).toArray(String[]::new);
		FirebaseRequest firebaseRequest = new FirebaseRequest();
		firebaseRequest.setRegistrationIds(usersFcmKeys);
		String notProvided = "\"__\"";
		
		String accountName = "\""+leadmanagementDTO.getName()+"\"";
		
		String contactPerson = leadmanagementDTO.getContactPerson()==null || leadmanagementDTO.getContactPerson().isEmpty() ? notProvided : "\""+leadmanagementDTO.getContactPerson()+"\"";
		
		String phone = leadmanagementDTO.getPhone()==null || leadmanagementDTO.getPhone().isEmpty() ? notProvided : "\""+leadmanagementDTO.getPhone()+"\"";
		
		String location = leadmanagementDTO.getLocationName()==null || leadmanagementDTO.getLocationName().isEmpty() ? notProvided :"\""+leadmanagementDTO.getLocationName()+"\"";
		
		String address = "\""+leadmanagementDTO.getAddress()+"\"";
		
		StringBuilder message = new StringBuilder();
		message.append(accountName).append(" has assigned you a Contact ").append(contactPerson)
		.append(" for execution - Phone number : ").append(phone).append(", Location: ")
		.append(location).append(", Adress: ").append(address);
		
		System.out.println("Message :"+message);
		
//		message.append("Name - ").append(accountName).append(", Contact Person - ")
//				.append(leadmanagementDTO.getContactPerson()).append(" Phone - ")
//				.append(leadmanagementDTO.getPhone()).append(", Task - ");
		activityNamesAndTaskPid.forEach((name, tPid) -> {
			FirebaseData data = new FirebaseData();
			data.setTitle("New Contact Added - " + accountName);
			String actualMessage = message.toString() + name +". Remarks - " + leadmanagementDTO.getRemarks();
			data.setMessage(actualMessage);
			data.setMessageType(NotificationMessageType.ACCOUNT_TASK);
			data.setPidUrl(tPid);
			data.setSentDate(LocalDateTime.now().toString());
			firebaseRequest.setData(data);
			firebaseService.sendNotificationToUsers(firebaseRequest, userDevices, SecurityUtils.getCurrentUserLogin());
		});
	}

	private Task createTask(AccountProfile accountProfile, Activity activity, String remarks) {
		// create task
		Task task = new Task();
		task.setAccountProfile(accountProfile);
		task.setAccountType(accountProfile.getAccountType());
		task.setActivity(activity);
		task.setCompany(accountProfile.getCompany());
		task.setPid(TaskService.PID_PREFIX + RandomUtil.generatePid());
		task.setRemarks(remarks);
		return task;
	}
	
	private AccountActivityTaskConfig convertActivityConfigDtoToDomain(AccountActivityTaskConfigDTO dto) {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AT_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<AccountType> accountType = accountTypeRepository.findOneByPid(dto.getAccountTypePid());
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
	        		String id1 = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
	        		String description1 ="get one by pid";
	        		LocalDateTime startLCTime1 = LocalDateTime.now();
	        		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
	        		String startDate1 = startLCTime1.format(DATE_FORMAT1);
	        		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		Optional<Activity> activity = activityRepository.findOneByPid(dto.getActivityPid());
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
		AccountActivityTaskConfig domain = new AccountActivityTaskConfig();
		if(accountType.isPresent() && activity.isPresent()) {
			domain.setAccountType(accountType.get());
			domain.setCompany(accountType.get().getCompany());
			domain.setActivity(activity.get());
			domain.setAssignNotification(dto.isAssignNotification());
		}
		return domain;
	}
}
