package com.orderfleet.webapp.web.websocket;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/web")
public class WSTransaction {
	
	private final Logger log = LoggerFactory.getLogger(WSTransaction.class);
	
	@GetMapping("/sockets")
	public String getSocketPage() {
		return "print/socketsample";
	}
	
	@Inject
	private SimpMessagingTemplate messagingTemplate;
	
	@GetMapping("/sockets/sendMessage")
	@ResponseBody
	public String getSocketPage(@RequestParam("msg") String message) {
		log.debug("=============================================");
		log.debug(message);
		messagingTemplate.convertAndSend("/messages", message);
		return message;
	}

}
