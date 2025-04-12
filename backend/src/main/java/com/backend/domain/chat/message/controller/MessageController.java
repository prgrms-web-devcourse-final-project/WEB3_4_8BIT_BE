package com.backend.domain.chat.message.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.backend.domain.chat.message.dto.request.MessageRequest;
import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.message.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chat/send")
	public void sendMessage(
		@Payload final MessageRequest requestDto,
		@Header("simpSessionAttributes") final Map<String, Object> sessionAttributes
	) {
		// TODO 나중에 메세지 저장을 비동기 처리할 수 있으면 처리
		Long senderId = (Long) sessionAttributes.get("senderId");
		String nickname = (String)sessionAttributes.get("nickname");
		String fileUrl = (String)sessionAttributes.get("fileUrl");

		MessageResponse.Basic messageResponse = messageService.saveMessage(senderId, nickname, fileUrl, requestDto);

		messagingTemplate.convertAndSend("/topic/chat/" + requestDto.roomId(), messageResponse);
	}
}
