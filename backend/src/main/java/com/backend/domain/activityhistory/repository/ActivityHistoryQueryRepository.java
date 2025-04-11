package com.backend.domain.activityhistory.repository;

import static com.backend.domain.activityhistory.entity.QActivityHistory.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.activityhistory.domain.ActivityType;
import com.backend.domain.activityhistory.dto.request.ActivityHistoryRequest;
import com.backend.domain.activityhistory.dto.response.ActivityHistoryResponse;
import com.backend.domain.activityhistory.dto.response.QActivityHistoryResponse_Detail;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.util.QuerydslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ActivityHistoryQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	// 정렬 필드를 매핑
	private static final Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
		"createdAt", activityHistory.createdAt
	);

	//activityType 가 일치하는 데이터를 가져오는 조건식 생성 함수
	private static final BiFunction<ActivityType, Long, BooleanExpression> BOOLEAN_EXPRESSION_BI_FUNCTION =
		(activityType, memberId) -> activityHistory.memberId.eq(memberId)
			.and(
				activityType != null ?
					activityHistory.activityType.eq(activityType) :
					null
			);

	public ScrollResponse<ActivityHistoryResponse.Detail> findDetail(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final ActivityHistoryRequest.Search requestDto,
		final Long memberId
	) {

		List<ActivityHistoryResponse.Detail> detailList = jpaQueryFactory
			.select(new QActivityHistoryResponse_Detail(
				activityHistory.activityHistoryId,
				activityHistory.activityType,
				activityHistory.activityType.stringValue(),
				activityHistory.description,
				activityHistory.createdAt))
			.from(activityHistory)
			.where(whereCondition(cursorRequestDto, requestDto, memberId))
			.orderBy(getOrderBy(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		// 다음 페이지가 있는지 확인 마지막 페이지라면 True
		boolean isLast = detailList.size() <= cursorRequestDto.size();

		if (!isLast) {
			detailList.remove(detailList.size() - 1);
		}
		return ScrollResponse.from(
			detailList,
			cursorRequestDto.size(),
			detailList.size(),
			cursorRequestDto.fieldValue() == null,
			isLast
		);
	}

	public List<Long> findActivityHistoryIdsBeforeOneMonth() {
		// 현재 시간으로부터 1달 전 시간 계산
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime oneMonthAgo = now.minusMonths(1);

		log.debug("검색 시간 조건: * ~ {}", oneMonthAgo);

		//
		return jpaQueryFactory
			.select(activityHistory.activityHistoryId)
			.from(activityHistory)
			.where(activityHistory.createdAt.before(oneMonthAgo))
			.groupBy(activityHistory.activityHistoryId)
			.fetch();
	}

	private BooleanExpression whereCondition(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final ActivityHistoryRequest.Search requestDto,
		final Long memberId
	) {

		BooleanExpression baseBooleanExpression = BOOLEAN_EXPRESSION_BI_FUNCTION.apply(
			requestDto.activityType(),
			memberId
		);

		// 입력값 유효성 검사
		if (!StringUtils.hasText(cursorRequestDto.fieldValue()) || cursorRequestDto.id() == null) {
			return baseBooleanExpression;
		}

		// 기본키 ID 값
		Long idValue = cursorRequestDto.id();
		// Sort 필드 값
		String sortFieldValueStr = cursorRequestDto.fieldValue();
		// 정렬 순서 Order 객체로 변환
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return getWhereBooleanExpression(sortFieldValueStr, idValue, order, baseBooleanExpression);
	}

	/**
	 * Where절에 들어가야하는 조건식을 만들어 반환하는 메소드 입니다.
	 *
	 * @param sortFieldValueStr     정렬 필드 값
	 * @param idValue               기본키 ID 값
	 * @param order                 {@link Order}
	 * @param baseBooleanExpression 항상 고정인 조건식
	 * @return
	 */
	private BooleanExpression getWhereBooleanExpression(
		final String sortFieldValueStr,
		final Long idValue,
		final Order order,
		final BooleanExpression baseBooleanExpression
	) {
		try {
			ZonedDateTime parseFieldValue = ZonedDateTime.parse(sortFieldValueStr);
			BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
				activityHistory.activityHistoryId,
				idValue,
				activityHistory.createdAt,
				parseFieldValue,
				order
			);
			return baseBooleanExpression.and(createFieldPredicate);
		} catch (NumberFormatException | DateTimeParseException e) {
			throw new GlobalException(GlobalErrorCode.REPOSITORY_FORMAT_PARSE_ERROR);
		}
	}

	/**
	 * 정렬할 필드와 정렬 방식을 OrderSpecifier로 반환합니다.
	 *
	 * @param pageRequestDto
	 * @return {@link OrderSpecifier}
	 */
	private OrderSpecifier<?>[] getOrderBy(final GlobalRequest.CursorRequest pageRequestDto) {
		// 기본 정렬 방식 설정
		Order queryOrder = QuerydslUtil.getOrder(pageRequestDto);

		// 정렬 필드 결정
		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(pageRequestDto.sort()) && FIELD_MAP.containsKey(pageRequestDto.sort()) ?
				FIELD_MAP.get(pageRequestDto.sort()) : activityHistory.createdAt;

		// 두 개의 OrderSpecifier를 배열로 반환
		return new OrderSpecifier<?>[] {
			new OrderSpecifier<>(queryOrder, sortField),
			new OrderSpecifier<>(Order.ASC, activityHistory.activityHistoryId)
		};
	}

	public long deleteByIdList(final List<Long> activityHistoryidList) {
		return jpaQueryFactory.delete(activityHistory)
			.where(activityHistory.activityHistoryId.in(activityHistoryidList))
			.execute();
	}
}
