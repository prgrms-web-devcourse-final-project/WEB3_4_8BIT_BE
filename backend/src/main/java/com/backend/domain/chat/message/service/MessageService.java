package com.backend.domain.chat.message.service;

import com.backend.domain.chat.dto.request.CursorRequest;
import com.backend.domain.chat.dto.response.CursorResponse;
import com.backend.domain.chat.message.dto.request.MessageRequest;
import com.backend.domain.chat.message.dto.response.MessageResponse;

public interface MessageService {

	/**
	 * 주어진 메시지 요청 정보를 기반으로 메시지를 저장 하고,
	 * 저장된 메시지에 대한 응답 정보를 반환
	 *
	 * @param senderId 메세지를 전송한 회원의 ID
	 * @param nickname 메세지를 전송한 회원의 닉네임
	 * @param fileUrl 메세지를 전송한 회원의 프로필 이미지 RUL
	 * @param requestDto 저장할 메시지의 정보를 담은 DTO
	 * @return 저장된 메시지에 대한 응답 DTO
	 */
	MessageResponse saveMessage(
		final Long senderId,
		final String nickname,
		final String fileUrl,
		final MessageRequest requestDto
	);

	/**
	 * 채팅방의 이전 메시지 목록을 커서 기반으로 조회
	 *
	 * @param roomId 메시지를 조회할 채팅방 ID
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보
	 * @return 메시지 목록과 다음 커서 정보
	 */
	CursorResponse<MessageResponse> getMessagesByRoomId(final Long roomId, final CursorRequest cursorRequestDto);
}
