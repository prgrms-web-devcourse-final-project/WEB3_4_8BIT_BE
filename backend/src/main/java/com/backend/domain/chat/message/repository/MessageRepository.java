package com.backend.domain.chat.message.repository;

import java.util.List;

import com.backend.domain.chat.dto.request.CursorRequest;
import com.backend.domain.chat.message.entity.Message;

public interface MessageRepository {

	/**
	 * 주어진 메시지를 저장
	 *
	 * @param message 저장할 {@link Message} 객체
	 * @return 저장된 {@link Message} 객체
	 */
	Message save(final Message message);

	/**
	 * 특정 채팅방의 메시지를 커서 기반 페이지네이션 방식으로 조회
	 *
	 * @param roomId 조회할 채팅방의 ID
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보
	 * @return 메시지 목록 (최신순)
	 */
	List<Message> findMessagesByRoomId(final Long roomId, final CursorRequest cursorRequestDto);
}
