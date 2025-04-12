package com.backend.domain.chat.room.service;

import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.chat.room.converter.RoomConverter;
import com.backend.domain.chat.room.entity.Room;
import com.backend.domain.chat.room.entity.TargetType;
import com.backend.domain.chat.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	@Override
	@Transactional
	public Long createRoom(final Long targetId, final TargetType targetType) {
		Room room = RoomConverter.fromTargetIdAndTargetType(targetId, targetType);
		return roomRepository.save(room).getRoomId();
	}

	@Async
	@Override
	@Transactional
	public void updateLastMessageTime(Long roomId, ZonedDateTime lastMessageTime) {
		roomRepository.updateLastMessageTime(roomId, lastMessageTime);
	}
}
