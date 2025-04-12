package com.backend.global.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.backend.global.dto.request.GlobalRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;

public class QuerydslUtil {

	/**
	 * Order 객체로 변환하여 반환합니다.
	 *
	 * @param pageRequestDto
	 * @return {@link Order} 기본 값 DESC
	 */
	public static Order getOrder(final GlobalRequest.CursorRequest pageRequestDto) {
		// 기본 정렬은 DESC로 동작
		return Order.ASC.toString().equalsIgnoreCase(pageRequestDto.order()) ?
			Order.ASC :
			Order.DESC;
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>Integer 타입의 필드만 사용 가능합니다.</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   기본키 필드 값
	 * @param sortField      정렬 대상 필드
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order          정렬 순서
	 * @return {@link BooleanExpression}
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final NumberPath<Integer> sortField,
		final Integer sortFieldValue,
		final Order order
	) {
		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>ZonedDateTime 타입의 필드만 사용 가능합니다.</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   기본키 필드 값
	 * @param sortField      정렬 대상 필드
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order          정렬 순서
	 * @return {@link BooleanExpression}
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final DateTimePath<ZonedDateTime> sortField,
		final ZonedDateTime sortFieldValue,
		final Order order
	) {

		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>LocalDate 타입의 필드만 사용 가능합니다.</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   기본키 필드 값
	 * @param sortField      정렬 대상 필드
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order          정렬 순서
	 * @return {@link BooleanExpression}
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final DatePath<LocalDate> sortField,
		final LocalDate sortFieldValue,
		final Order order
	) {

		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>Long 타입의 필드만 사용 가능합니다.</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   기본키 필드 값
	 * @param sortField      정렬 대상 필드
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order          정렬 순서
	 * @return {@link BooleanExpression}
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final NumberPath<Long> sortField,
		final Long sortFieldValue,
		final Order order
	) {

		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>Double 타입의 필드만 사용 가능합니다.</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   기본키 필드 값
	 * @param sortField      정렬 대상 필드
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order          정렬 순서
	 * @return {@link BooleanExpression}
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final NumberPath<Double> sortField,
		final Double sortFieldValue,
		final Order order
	) {

		return sortField.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC) ? sortField.lt(sortFieldValue) : sortField.gt(sortFieldValue));
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>두 개 이상의 필드를 더하거나 계산된 결과로 정렬할 때 사용 가능합니다.</p>
	 * <p>예: likeCount + commentCount 와 같은 계산식을 기반으로 커서 정렬을 수행할 때 사용됩니다.</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   기본키 필드 값
	 * @param sortExpression 정렬 대상 계산식 (예: likeCount + commentCount)
	 * @param sortFieldValue 정렬 대상 필드 값
	 * @param order          정렬 순서 (ASC 또는 DESC)
	 * @return {@link BooleanExpression} 커서 기반 정렬을 위한 조건절
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final NumberExpression<Long> sortExpression,
		final Long sortFieldValue,
		final Order order
	) {
		return sortExpression.eq(sortFieldValue)
			.and(idField.gt(idFieldValue))
			.or(order.equals(Order.DESC)
				? sortExpression.lt(sortFieldValue)
				: sortExpression.gt(sortFieldValue));
	}

}
