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

		return order == Order.DESC
			? sortField.lt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.lt(idFieldValue)))
			: sortField.gt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.gt(idFieldValue)));
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

		return order == Order.DESC
			? sortField.lt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.lt(idFieldValue)))
			: sortField.gt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.gt(idFieldValue)));
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

		return order == Order.DESC
			? sortField.lt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.lt(idFieldValue)))
			: sortField.gt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.gt(idFieldValue)));
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

		return order == Order.DESC
			? sortField.lt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.lt(idFieldValue)))
			: sortField.gt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.gt(idFieldValue)));
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

		return order == Order.DESC
			? sortField.lt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.lt(idFieldValue)))
			: sortField.gt(sortFieldValue)
			.or(sortField.eq(sortFieldValue).and(idField.gt(idFieldValue)));
	}

	/**
	 * 커서 방식 Where 조건절 생성 메소드 입니다.
	 * <p>likeCount + commentCount 같은 계산식 정렬 시 사용</p>
	 *
	 * @param idField        기본키 필드
	 * @param idFieldValue   커서 기준 ID
	 * @param sortExpression 정렬 기준 계산식
	 * @param sortFieldValue 커서 기준 정렬값
	 * @param order          정렬 방향
	 * @return BooleanExpression
	 */
	public static BooleanExpression createFieldPredicate(
		final NumberPath<Long> idField,
		final Long idFieldValue,
		final NumberExpression<Long> sortExpression,
		final Long sortFieldValue,
		final Order order
	) {

		return order == Order.DESC
			? sortExpression.lt(sortFieldValue)
			.or(sortExpression.eq(sortFieldValue).and(idField.lt(idFieldValue)))
			: sortExpression.gt(sortFieldValue)
			.or(sortExpression.eq(sortFieldValue).and(idField.gt(idFieldValue)));
	}

	public static BooleanExpression buildLongCursorCondition(
		NumberPath<Long> sortField,
		NumberExpression<Long> idField,
		Long sortFieldValue,
		Long idValue,
		Order order,
		boolean isPrev
	) {
		if (order == Order.ASC) {
			if (isPrev) {
				return sortField.lt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.lt(idValue)));
			} else {
				return sortField.gt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.gt(idValue)));
			}
		} else { // DESC
			if (isPrev) {
				return sortField.gt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.gt(idValue)));
			} else {
				return sortField.lt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.lt(idValue)));
			}
		}
	}

	// Double용 커서 조건
	public static BooleanExpression buildDoubleCursorCondition(
		NumberPath<Double> sortField,
		NumberExpression<Long> idField,
		Double sortFieldValue,
		Long idValue,
		Order order,
		boolean isPrev
	) {
		if (order == Order.ASC) {
			if (isPrev) {
				return sortField.lt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.lt(idValue)));
			} else {
				return sortField.gt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.gt(idValue)));
			}
		} else { // DESC
			if (isPrev) {
				return sortField.gt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.gt(idValue)));
			} else {
				return sortField.lt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.lt(idValue)));
			}
		}
	}

	// ZonedDateTime
	public static BooleanExpression buildDateCursorCondition(
		DateTimePath<ZonedDateTime> sortField,
		NumberExpression<Long> idField,
		ZonedDateTime sortFieldValue,
		Long idValue,
		Order order,
		boolean isPrev
	) {
		if (order == Order.ASC) {
			if (isPrev) {
				return sortField.lt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.lt(idValue)));
			} else {
				return sortField.gt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.gt(idValue)));
			}
		} else { // DESC
			if (isPrev) {
				return sortField.gt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.gt(idValue)));
			} else {
				return sortField.lt(sortFieldValue)
					.or(sortField.eq(sortFieldValue).and(idField.lt(idValue)));
			}
		}
	}

}
