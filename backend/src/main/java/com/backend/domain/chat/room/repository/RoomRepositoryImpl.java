package com.backend.domain.chat.room.repository;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.chat.room.entity.Room;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {

	private final RoomJpaRepository roomJpaRepository;
	private final RoomQueryRepository roomQueryRepository;

	@Override
	public Room save(final Room room) {
		return roomJpaRepository.save(room);
	}

	@Override
	public Optional<Room> findById(final Long roomId) {
		return roomJpaRepository.findById(roomId);
	}

	@Override
	public void updateLastMessageTime(final Long roomId, final ZonedDateTime lastMessageTime) {
		roomQueryRepository.updateLastMessageTime(roomId, lastMessageTime);
	}
}
