package com.orderfleet.webapp.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.service.NotificationMessageService;
import com.orderfleet.webapp.web.rest.dto.NotificationMessageDTO;

@Controller
@RequestMapping("/web")
public class ContactNotificationResource {

	private final Logger log = LoggerFactory.getLogger(ContactNotificationResource.class);
	
	@Inject
	NotificationMessageService notificationMessageService;
	
	@Timed
	@RequestMapping(value = "/contact-notifications", method = RequestMethod.GET)
	public String getAllContactNotifications(Model model)
	{
		log.debug("Web request to get a page of contact notification");
//		model.addAttribute("notificationMessages", notificationMessageService.findAllByNotificationMessageType(NotificationMessageType.ACCOUNT_TASK));
		return "company/contact-notifications";
	}
	
	@Timed
	@RequestMapping(value = "/contact-notifications/load", method = RequestMethod.GET)
	@ResponseBody
	public List<NotificationMessageDTO> loadAllNotificationMessages(){
		log.debug("Web request to load notification");
		return  notificationMessageService.findAllByNotificationMessageType(NotificationMessageType.ACCOUNT_TASK);
	}
}
