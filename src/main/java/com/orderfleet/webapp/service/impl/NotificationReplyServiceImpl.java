package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.domain.NotificationReply;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.NotificationReplyRepository;
import com.orderfleet.webapp.repository.NotificationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.NotificationReplyService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.NotificationReplyDTO;

@Service
public class NotificationReplyServiceImpl implements NotificationReplyService {

	private final Logger log = LoggerFactory.getLogger(NotificationReplyServiceImpl.class);
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private NotificationRepository notificationRepository;
	
	@Inject
	private NotificationReplyRepository notificationReplyRepository;
	
	@Override
	public NotificationReply saveNotificationReply(NotificationReplyDTO notificationReplyDTO) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<User> opUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		NotificationReply notificationReply = new NotificationReply();
		log.info("Saving Notification reply ...");
		if(opUser.isPresent()) {
			notificationReply.setPid(NotificationReplyService.PID_PREFIX +  RandomUtil.generatePid());
			notificationReply.setCompany(company);
			notificationReply.setCreatedBy(opUser.get());
			notificationReply.setCreatedDate(LocalDateTime.now());
			notificationReply.setReplyMessage(notificationReplyDTO.getMessage());
			Notification notification = notificationRepository.findByPidOrderByCreatedDateDesc(notificationReplyDTO.getNotificationPid());
			if(notification != null) {
				notificationReply.setNotification(notification);
				notificationReply = notificationReplyRepository.save(notificationReply);
				log.info("Notification reply saved successfully");
			}
		}
		return notificationReply;
	}

	@Override
	public List<NotificationReplyDTO> getAllNotificationReplyByNotificationPidOrderByCreatedDate(String pid) {

		List<NotificationReply> notificationReplyList = notificationReplyRepository.findAllByNotificationPidOrderByCreatedDate(pid);
		List<NotificationReplyDTO> notificationReply = new ArrayList<NotificationReplyDTO>();
		notificationReply = notificationReplyList.stream().map(notifi -> new NotificationReplyDTO(notifi)).collect(Collectors.toList());
		return notificationReply;
	}

}
