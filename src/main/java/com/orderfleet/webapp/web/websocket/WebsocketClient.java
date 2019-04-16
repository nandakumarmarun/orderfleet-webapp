package com.orderfleet.webapp.web.websocket;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WebsocketClient {
	
	private Session session;

	public WebsocketClient(String uri) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, new URI(uri));
		} catch (Exception ex) {

		}
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	@OnMessage
	public void onMessage(String message, Session session) {
	}

	public void sendMessage(String message) {
		try {
			session.getBasicRemote().sendText(message);
			Thread.sleep(1000);
			session.close();
		} catch (IOException | InterruptedException ex) {

		}
	}
}