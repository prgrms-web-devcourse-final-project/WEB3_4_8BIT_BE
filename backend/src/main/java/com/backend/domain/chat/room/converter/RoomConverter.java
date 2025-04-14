package com.backend.domain.chat.room.converter;

import com.backend.domain.chat.room.entity.Room;
import com.backend.domain.chat.room.entity.Status;
import com.backend.domain.chat.room.entity.TargetType;

public class RoomConverter {

	public static Room fromTargetIdAndTargetType(
		final Long targetId,
		final String targetName,
		final TargetType targetType
	) {

		return Room.builder()
			.targetId(targetId)
			.targetType(targetType)
			.targetName(targetName)
			.status(Status.ACTIVE)
			.build();
	}
}
