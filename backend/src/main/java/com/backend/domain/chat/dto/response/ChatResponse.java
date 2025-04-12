package com.backend.domain.chat.dto.response;

import java.util.List;

public class ChatResponse {

	public record MessageCursorResponse<T>(
		List<T> content,
		String nextCursorId
	) {
		public static <T> MessageCursorResponse<T> of(List<T> content, String nextCursorId) {
			return new MessageCursorResponse<>(content, nextCursorId);
		}
	}

	public record RoomCursorResponse<T>(
		List<T> content,
		String nextCursorTime
	) {
		public static <T> RoomCursorResponse<T> of(List<T> content, String nextCursorTime) {
			return new RoomCursorResponse<>(content, nextCursorTime);
		}
	}
}
