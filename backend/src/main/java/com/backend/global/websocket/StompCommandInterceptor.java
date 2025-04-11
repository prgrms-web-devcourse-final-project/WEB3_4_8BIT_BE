package com.backend.global.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompCommandInterceptor implements ChannelInterceptor {

	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (accessor != null) {
			StompCommand command = accessor.getCommand();

			if (command == null) {
				return message;
			}

			// TODO 필요 시 추가 작성

			// switch (command) {
			// 	case CONNECT:
			// 		log.debug("STOMP 연결 요청: {}", accessor.getSessionId());
			// 		// 연결 요청 시 인증 추가 로직
			// 		break;
			// 	case SUBSCRIBE:
			// 		log.debug("STOMP 구독 요청: {}", accessor.getDestination());
			// 		// 구독 요청 시 권한 체크 로직
			// 		break;
			// 	case SEND:
			// 		log.debug("STOMP 메시지 전송: {}", accessor.getDestination());
			// 		// 메시지 전송 시 내용 필터링 또는 추가 로직
			// 		break;
			// 	case DISCONNECT:
			// 		log.debug("STOMP 연결 종료: {}", accessor.getSessionId());
			// 		// 연결 종료 시 정리 작업
			// 		break;
			// 	default:
			// 		log.debug("기타 STOMP Command: {}", command);
			// }
		}

		return message;
	}
}
