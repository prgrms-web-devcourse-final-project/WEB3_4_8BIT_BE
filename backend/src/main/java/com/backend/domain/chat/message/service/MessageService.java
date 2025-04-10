package com.backend.domain.chat.message.service;

import com.backend.domain.chat.message.dto.request.MessageRequest;
import com.backend.domain.chat.message.dto.response.MessageResponse;

public interface MessageService {

	/**
	 * 주어진 메시지 요청 정보를 기반으로 메시지를 저장 하고,
	 * 저장된 메시지에 대한 응답 정보를 반환
	 *
	 * @param requestDto 저장할 메시지의 정보를 담은 DTO
	 * @return 저장된 메시지에 대한 응답 DTO
	 */
	MessageResponse saveMessage(final MessageRequest requestDto);
}
