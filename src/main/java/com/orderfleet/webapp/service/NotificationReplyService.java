package com.orderfleet.webapp.service;

import java.util.List;


import com.orderfleet.webapp.domain.Notification;
import com.orderfleet.webapp.domain.NotificationReply;
import com.orderfleet.webapp.web.rest.dto.FirebaseResponse;
import com.orderfleet.webapp.web.rest.dto.NotificationDTO;
import com.orderfleet.webapp.web.rest.dto.NotificationReplyDTO;


public interface NotificationReplyService {

	String PID_PREFIX = "NOTIFIREP-";

	NotificationReply saveNotificationReply(NotificationReplyDTO notificationReplyDTO);

	List<NotificationReplyDTO> getAllNotificationReplyByNotificationPidOrderByCreatedDate(String pid);

}
