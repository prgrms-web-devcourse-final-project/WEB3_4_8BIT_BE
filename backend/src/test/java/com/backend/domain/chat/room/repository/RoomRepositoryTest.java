package com.backend.domain.chat.room.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.backend.domain.chat.room.entity.Room;
import com.backend.domain.chat.room.entity.Status;
import com.backend.domain.chat.room.entity.TargetType;
import com.backend.global.config.QuerydslConfig;
import com.backend.global.util.BaseTest;

@DataJpaTest
@Import({
	QuerydslConfig.class,
	RoomRepositoryImpl.class,
	RoomQueryRepository.class})
class RoomRepositoryTest extends BaseTest {

	@Autowired
	private RoomRepository roomRepository;

	private Room createRoom(Long targetId, TargetType targetType) {
		return Room.builder()
			.targetId(targetId)
			.targetType(targetType)
			.status(Status.ACTIVE)
			.build();
	}

	@Test
	@DisplayName("채팅방 생성 [Repository] - Success")
	void t01() {
		// given
		Room room = createRoom(1L, TargetType.FISHING_TRIP_POST);

		// when
		Room savedRoom = roomRepository.save(room);

		// then
		assertThat(savedRoom.getRoomId()).isNotNull();
		assertThat(savedRoom.getTargetId()).isEqualTo(1L);
		assertThat(savedRoom.getTargetType()).isEqualTo(TargetType.FISHING_TRIP_POST);
		assertThat(savedRoom.getStatus()).isEqualTo(Status.ACTIVE);
	}
}