package com.backend.domain.chat.room.repository;

import static com.backend.domain.chat.room.entity.QRoom.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.domain.chat.room.entity.Room;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoomQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public void updateLastMessageTime(final Long roomId, final ZonedDateTime lastMessageTime) {
		jpaQueryFactory
			.update(room)
			.set(room.lastMessageTime, lastMessageTime)
			.where(room.roomId.eq(roomId))
			.execute();
	}

	public List<Room> findRoomsByIds(final List<Long> roomIdList) {
		if (roomIdList == null || roomIdList.isEmpty()) {
			return List.of();
		}

		return jpaQueryFactory
			.selectFrom(room)
			.where(room.roomId.in(roomIdList))
			.fetch();
	}
}
