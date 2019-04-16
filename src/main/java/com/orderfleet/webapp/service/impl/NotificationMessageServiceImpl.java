package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.NotificationMessage;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.NotificationMessageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.NotificationMessageService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.NotificationDTO;
import com.orderfleet.webapp.web.rest.dto.NotificationMessageDTO;
import com.orderfleet.webapp.web.rest.dto.NotificationMessageRecipientDTO;

/**
 * Service Implementation for managing NotificationMessage.
 * 
 * @author Shaheer
 * @since November 03, 2016
 */
@Service
@Transactional
public class NotificationMessageServiceImpl implements NotificationMessageService {

	@Inject
	private NotificationMessageRepository notificationMessageRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public NotificationMessage saveNotificationMessage(FirebaseData data) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		NotificationMessage message = new NotificationMessage();
		message.setPid(NotificationMessageService.PID_PREFIX + RandomUtil.generatePid());
		message.setCreatedBy(SecurityUtils.getCurrentUserLogin());
		message.setMessageData(data);
		message.setNotificationType(data.getMessageType());
		message.setExpiryDate(LocalDateTime.now().plusDays(30));
		message.setCompany(company);

		// find all recipient users and save to database
		/*
		 * NotificationMessageRecipient messageRecipient = new
		 * NotificationMessageRecipient();
		 * messageRecipient.setNotificationMessage(message);
		 * messageRecipient.setUserDevice(userDeviceRepository.findOne(32L));
		 * messageRecipient.setCompany(company);
		 * messageRecipient.setNotificationMessage(message);
		 * 
		 * Set<NotificationMessageRecipient> messageRecipientList = new
		 * HashSet<>(); messageRecipientList.add(messageRecipient);
		 * message.setMessageRecipients(messageRecipientList);
		 */

		return notificationMessageRepository.save(message);
	}

	@Override
	public List<NotificationMessageDTO> findAllByNotificationMessageType(NotificationMessageType notificationMessageType) {

		List<NotificationMessage> notificationList = notificationMessageRepository.findByNotificationType(notificationMessageType);
		
		List<NotificationMessageDTO> notificationMessageDtoList = new ArrayList<>();
		for(NotificationMessage nm : notificationList) {
			NotificationMessageDTO dto = new NotificationMessageDTO();
			dto.setPid(nm.getPid());
			dto.setCreatedBy(nm.getCreatedBy());
			dto.setMessageData(nm.getMessageData().getMessage());
			dto.setCreatedDate(nm.getCreatedDate());
			dto.setNotificationType(nm.getNotificationType());
			dto.setExpiryDate(nm.getExpiryDate());
			dto.setRemind(nm.isRemind());
			dto.setNextRemindDate(nm.getNextRemindDate());
			
			dto.setNotificationReceipts(nm.getMessageRecipients()
											.stream().map(rec -> 
												new NotificationMessageRecipientDTO(
														rec.getUserDevice().getUser().getFirstName()
														,rec.getMessageStatus())).collect(Collectors.toList()));
			notificationMessageDtoList.add(dto);
		}
		
//		 notificationList.stream().map(nm -> new NotificationMessageDTO(nm)).collect(Collectors.toList());
		return notificationMessageDtoList;
	}

	
	
}
