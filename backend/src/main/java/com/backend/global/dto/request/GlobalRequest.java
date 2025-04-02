package com.backend.global.dto.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public class GlobalRequest {

	/**
	 * {
	 * "page": 0,
	 * "size": 10,
	 * "sort": "createdAt",
	 * "order": "desc"
	 * }
	 *
	 * @param page  페이지 번호
	 * @param size  페이지 사이즈
	 * @param sort  정렬 필드
	 * @param order 정렬 기준
	 */
	public record PageRequest(
		@Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
		@Schema(description = "페이지 번호", example = "0")
		Integer page,

		@Min(value = 1, message = "페이지 사이즈는 0 이상이어야 합니다.")
		@Schema(description = "페이지 사이즈", example = "10")
		Integer size,

		@Schema(description = "정렬 필드 (default createdAt)", example = "createdAt")
		String sort,

		@Schema(description = "정렬 기준 [asc, desc] (default desc)", example = "desc")
		String order) {

		public PageRequest {
			page = (page == null) ? 0 : page;
			size = (size == null) ? 10 : size;
		}

		public Pageable toPageable() {
			String sortBy = (sort == null || sort.isBlank()) ? "createdAt" : sort;
			String sortOrder = (order == null || order.isBlank()) ? "desc" : order;
			Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
			return org.springframework.data.domain.PageRequest.of(page, size, Sort.by(direction, sortBy));
		}
	}

	/**
	 * {
	 *     "order": "createdAt",
	 *     "sort": "asc",
	 *     "type": "next"
	 *     "fieldValue": "2024-07-16T13:09:08.663442+08:00",
	 *     "id": 130,
	 *     "size": 3
	 * }
	 * @param order      정렬 필드
	 * @param sort       정렬 기준 (ASC, DESC)
	 * @param type       이전인지 다음인지 (Next, Prev)
	 * @param fieldValue 정렬 필드 Value
	 * @param id         Id
	 * @param size       페이지 사이즈
	 */
	public record CursorRequest(
		@Schema(description = "정렬 필드 (default createdAt)", example = "createdAt")
		String order,
		@Schema(description = "정렬 기준 [asc, desc] (default desc)", example = "desc")
		String sort,
		@Schema(description = "이전, 이후 [prev, next] (default next)", example = "next")
		String type,
		@Schema(description = "정렬 필드 값 [이전이면 처음 값, 이후면 마지막 값]", example = "next")
		String fieldValue,
		@Schema(description = "정렬 필드 ID값 [이전이면 처음 값, 이후면 마지막 값]", example = "next")
		Long id,
		@Min(value = 1, message = "페이지 사이즈는 0 이상이어야 합니다.")
		@Schema(description = "페이지 사이즈", example = "10")
		Integer size
	) {
		public static final String PREV = "prev";
		public static final String NEXT = "next";

		public CursorRequest {
			size = (size == null) ? 10 : size;
			type = PREV.equalsIgnoreCase(type) ? PREV : NEXT;
		}
	}
}
