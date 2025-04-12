package com.backend.domain.chat.room.service;

import static com.backend.domain.chat.room.dto.response.RoomResponse.*;

import java.time.ZonedDateTime;
import java.util.List;

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

	/**
	 * 채팅방의 마지막 메시지 시간을 업데이트
	 *
	 * @param roomId 채팅방 ID
	 * @param lastMessageTime 마지막 메시지 시간
	 */
	void updateLastMessageTime(final Long roomId, final ZonedDateTime lastMessageTime);

	/**
	 * 주어진 회원 ID를 기준으로, 회원이 참여 중인 모든 채팅방 목록을 조회
	 *
	 * @param memberId 조회할 회원의 ID
	 * @return 채팅방 기본 정보 리스트
	 */
	List<Basic> getRoomList(final Long memberId);
}
