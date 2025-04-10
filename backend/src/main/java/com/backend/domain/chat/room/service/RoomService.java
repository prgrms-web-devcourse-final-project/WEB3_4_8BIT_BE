package com.backend.domain.chat.room.service;

import com.backend.domain.chat.room.entity.TargetType;

public interface RoomService {

	/**
	 * 주어진 대상 ID와 대상 타입을 기반으로 채팅방을 생성
	 *
	 * @param targetId 채팅방이 연결될 대상의 ID
	 * @param targetType 채팅방이 연결될 대상의 타입
	 * @return 생성된 채팅방의 ID
	 */
	Long createRoom(final Long targetId, final TargetType targetType);
}
