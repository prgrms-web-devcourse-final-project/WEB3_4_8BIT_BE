package com.backend.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public class ChatRequest {

	public record MessageCursorRequest(
		@Schema(description = "커서 ID (마지막 메시지 ObjectId)", example = "652fbb23a0e6b41dd0a122a3")
		String id,

		@Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
		@Schema(description = "페이지 사이즈", example = "10")
		Integer size
	) {
		public MessageCursorRequest {
			size = (size == null) ? 10 : size;
		}
	}

	public record RoomCursorRequest(
		@Schema(description = "커서 시간 (마지막 메시지 시간)", example = "2025-04-12T10:30:00+09:00")
		String lastMessageTime,

		@Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
		@Schema(description = "페이지 사이즈", example = "10")
		Integer size
	) {
		public RoomCursorRequest {
			size = (size == null || size < 1) ? 10 : size;
		}
	}
}
