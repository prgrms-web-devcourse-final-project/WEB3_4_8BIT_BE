package com.backend.global.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public class GlobalRequest {

	/**
	 * {
	 * 		"page": 0,
	 * 		"size": 10,
	 * 		"sort": "createdAt",
	 * 		"order": "desc"
	 * }
	 *
	 * @param page 페이지 번호
	 * @param size 페이지 사이즈
	 * @param sort 정렬 필드
	 * @param order 정렬 기준
	 */
	public record PageRequest (
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
}
