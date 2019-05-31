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
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.NotificationReplyService;
import com.orderfleet.webapp.service.NotificationService;
import com.orderfleet.webapp.service.UserDeviceService;
import com.orderfleet.webapp.service.async.FirebaseService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;
import com.orderfleet.webapp.web.rest.dto.NotificationDTO;
import com.orderfleet.webapp.web.rest.dto.NotificationDetailDto;
import com.orderfleet.webapp.web.rest.dto.NotificationReplyDTO;
import com.orderfleet.webapp.web.rest.dto.NotificationStatusDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class NotificationReplyResource {

	private final Logger log = LoggerFactory.getLogger(NotificationReplyResource.class);

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
	private NotificationReplyService notificationReplyService;

	@Inject
	public NotificationReplyResource(FirebaseService firebaseService, NotificationService notificationService,
			UserDeviceService userDeviceService, EmployeeHierarchyService employeeHierarchyService) {
		super();
		this.firebaseService = firebaseService;
		this.notificationService = notificationService;
		this.userDeviceService = userDeviceService;
		this.employeeHierarchyService = employeeHierarchyService;
	}
	

	@RequestMapping("/notifications-reply")
	@Timed
	@Transactional(readOnly = true)
	public String notificationReport() {

		return "company/notification-reply-report";
	}

	
	@RequestMapping(value = "/notifications-reply/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@ResponseBody

	public ResponseEntity<Map<String, List<NotificationDetailDto>>> filterNotifications(@RequestParam("filterBy") String filterBy,
			@RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		log.debug("Web request to filter notifications report");
		if (filterBy.equals(NotificationReplyResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;
		} else if (filterBy.equals(NotificationReplyResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(NotificationReplyResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(NotificationReplyResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(NotificationReplyResource.CUSTOM)) {
		}
		
		Map<String, List<NotificationDetailDto>> notifications = getFilterData(fromDate, toDate);
		return new ResponseEntity<>(notifications, HttpStatus.OK);
	}

	private Map<String, List<NotificationDetailDto>> getFilterData(LocalDate fDate,LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<NotificationDetail> notificationDetails = notificationDetailRepository
				.findByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate);
		List<EmployeeProfileDTO> employeeDtos = employeeProfileService.findAllEmployeeByUserIdsIn(
																				notificationDetails.stream()
																				.map(NotificationDetail::getUserId).collect(Collectors.toList()));
		for(EmployeeProfileDTO emp : employeeDtos) {
			System.out.println(emp.toString());
		}
		List<NotificationDetailDto> notificationDtoList = notificationDetails.stream().
				map(nD -> new NotificationDetailDto(nD)).collect(Collectors.toList());
		
		for(NotificationDetailDto notificationDetailDto : notificationDtoList) {
			String userName = employeeDtos.stream().filter(emp ->
														notificationDetailDto.getUserPid().equals(emp.getUserPid()))
														.findAny().get().getName();
			notificationDetailDto.setUserName(userName);
			List<NotificationReplyDTO> notificationReplyDtos = notificationReplyService.getAllNotificationReplyByNotificationPidOrderByCreatedDate(notificationDetailDto.getNotification().getPid());
			notificationDetailDto.setNotificationReplyDtoList(notificationReplyDtos);
		}

		Map<String,List<NotificationDetailDto>> notificationMap = 
				notificationDtoList.stream().collect(Collectors.groupingBy(no -> 
															no.getNotification().getPid()
															+"~"+no.getNotification().getTitle()
															+"~"+no.getNotification().getMessage()));
		
		return notificationMap;
	}

}
