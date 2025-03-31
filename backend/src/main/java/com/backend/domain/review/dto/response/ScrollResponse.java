package com.backend.domain.review.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record ScrollResponse<T>(List<T> content, boolean hasNext) {

	public static <T> ScrollResponse<T> from(Page<T> page) {
		return new ScrollResponse<>(page.getContent(), page.hasNext());
	}
}
