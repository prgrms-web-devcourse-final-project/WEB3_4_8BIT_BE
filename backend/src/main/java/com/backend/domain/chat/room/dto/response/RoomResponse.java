package com.backend.domain.chat.room.dto.response;

import com.backend.domain.chat.message.dto.response.MessageResponse;
import com.backend.domain.chat.room.entity.TargetType;

public class RoomResponse {

	public record Basic(
		Long roomId,
		Long targetId,
		String targetName,
		TargetType targetType,
		int participantCount,
		MessageResponse.Last lastMessage
	) {}
}
