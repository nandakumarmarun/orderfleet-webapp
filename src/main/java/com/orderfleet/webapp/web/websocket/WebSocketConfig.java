package com.orderfleet.webapp.web.websocket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	private final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new MessageHandler(), "/messages");
	}
	
	class MessageHandler extends TextWebSocketHandler {
		
		private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
		
		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			log.info("New connection with session ID : " + session.getId());
			sessions.add(session);
		}
		
		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			Thread.sleep(1000); // simulated delay
			log.info("BroadCasting message : " + message);
			for(WebSocketSession s : sessions) {
				try {
					s.sendMessage(message);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				
			}
		}
		
		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			log.info("Closing connection : " + session.getId());
			sessions.remove(session);
		}

	}

}