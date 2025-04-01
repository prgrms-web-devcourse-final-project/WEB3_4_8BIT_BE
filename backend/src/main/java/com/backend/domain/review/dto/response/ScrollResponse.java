package com.backend.domain.review.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

public record ScrollResponse<T>(List<T> content, boolean hasNext) {

	public static <T> ScrollResponse<T> from(Slice<T> slice) {
		return new ScrollResponse<>(slice.getContent(), slice.hasNext());
	}
}
