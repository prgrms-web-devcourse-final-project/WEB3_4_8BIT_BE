package com.backend.domain.chat.dto.response;

import java.util.List;

public record CursorResponse<T>(
	List<T> content,
	String nextCursorId
) {
	public static <T> CursorResponse<T> of(List<T> content, String nextCursorId) {
		return new CursorResponse<>(content, nextCursorId);
	}
}
