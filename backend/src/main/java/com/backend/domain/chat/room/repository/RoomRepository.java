package com.backend.domain.chat.room.repository;

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
}
