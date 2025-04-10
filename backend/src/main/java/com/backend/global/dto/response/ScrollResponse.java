package com.backend.global.dto.response;

import java.util.List;

public record ScrollResponse<T>(


	List<T> content,
	int pageSize,
	int numberOfElements,
	boolean isFirst,
	boolean isLast
) {

	public static <T> ScrollResponse<T> from(
		List<T> content,
		int pageSize,
		int numberOfElements,
		boolean isFirst,
		boolean isLast
	) {
		return new ScrollResponse<>(
			content,
			pageSize,
			numberOfElements,
			isFirst,
			isLast
		);
	}
}