package com.backend.domain.chat.room.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.backend.domain.chat.room.entity.Room;

public interface RoomRepository {

	/**
	 * 주어진 채팅방 정보를 저장
	 *
	 * @param room 저장할 채팅방 엔티티
	 * @return 저장된 채팅방 엔티티
	 */
	Room save(final Room room);

	/**
	 * 주어진 ID에 해당하는 채팅방을 조회
	 *
	 * @param id 조회할 채팅방의 ID
	 * @return 조회된 채팅방 엔티티
	 */
	Optional<Room> findById(final Long id);

	/**
	 * 채팅방의 마지막 메시지 시간을 업데이트
	 *
	 * @param roomId 채팅방 ID
	 * @param lastMessageTime 마지막 메시지 생성 시간
	 */
	void updateLastMessageTime(final Long roomId, final ZonedDateTime lastMessageTime);

	/**
	 * 주어진 채팅방 ID 목록에 해당하는 Room 엔티티들을 조회
	 *
	 * @param roomIdList 조회할 Room ID 목록
	 * @return 조회된 Room 리스트
	 */
	List<Room> findRoomsByIds(final List<Long> roomIdList);
}
