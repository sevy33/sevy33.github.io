package com.example.demo;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WSHandler extends TextWebSocketHandler {
	
	public void handleTextMessage(WebSocketSession sess, TextMessage message) {
		System.out.println(message.getPayload().charAt(0));
		
		System.out.println(message.getPayload().charAt(1));
		System.out.println(message.getPayload().charAt(2));
	}

}
