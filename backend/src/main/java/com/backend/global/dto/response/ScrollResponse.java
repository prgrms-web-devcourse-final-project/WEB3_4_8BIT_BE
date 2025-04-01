package com.backend.global.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

public record ScrollResponse<T>(
	List<T> content,
	int pageNumber,
	int pageSize,
	int numberOfElements,
	boolean first,
	boolean last
) {

	public static <T> ScrollResponse<T> from(Slice<T> slice) {
		return new ScrollResponse<>(
			slice.getContent(),
			slice.getNumber(),
			slice.getSize(),
			slice.getNumberOfElements(),
			slice.isFirst(),
			slice.isLast()
		);
	}
}