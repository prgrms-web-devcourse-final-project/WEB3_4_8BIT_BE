package com.backend.global.util;

import java.time.ZonedDateTime;

import com.backend.global.dto.request.GlobalRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;


public class QuerydslUtil {

	public static Order getOrder(GlobalRequest.CursorRequest pageRequestDto) {
		// 기본 정렬은 DESC로 동작
		return Order.ASC.toString().equalsIgnoreCase(pageRequestDto.order()) ?
			Order.ASC :
			Order.DESC;
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 *
	 * @param idField 기본키 필드
	 * @param idFieldValue 기본키 필드 값
	 * @param sortField 정렬 대상 필드
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order 정렬 순서
	 *
	 * @return {@link BooleanExpression}
	 */
	public static BooleanExpression createFieldPredicate(
		NumberPath<Long> idField,
		Long idFieldValue,
		NumberPath<Integer> sortField,
		Integer sortFieldValue,
		Order order
	) {
		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}

	public static BooleanExpression createFieldPredicate(
		NumberPath<Long> idField,
		Long idFieldValue,
		DateTimePath<ZonedDateTime> sortField,
		ZonedDateTime sortFieldValue,
		Order order
	) {

		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}
}
