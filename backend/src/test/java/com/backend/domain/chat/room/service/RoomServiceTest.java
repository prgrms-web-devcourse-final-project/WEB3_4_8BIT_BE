package com.backend.domain.chat.room.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.domain.chat.room.entity.Room;
import com.backend.domain.chat.room.entity.TargetType;
import com.backend.domain.chat.room.repository.RoomRepository;
import com.backend.global.util.BaseTest;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest extends BaseTest {

	@Mock
	private RoomRepository roomRepository;

	@InjectMocks
	private RoomServiceImpl roomService;

	@Test
	@DisplayName("채팅방 생성 [Service] - Success")
	void createRoom() {
		// given
		Room givenRoom = fixtureMonkeyValidation.giveMeOne(Room.class);

		when(roomRepository.save(any(Room.class))).thenReturn(givenRoom);

		// when
		Long roomId = roomService.createRoom(givenRoom.getTargetId(), TargetType.FISHING_TRIP_POST);

		// then
		assertThat(roomId).isEqualTo(givenRoom.getRoomId());
		verify(roomRepository, times(1)).save(any(Room.class));
	}
}