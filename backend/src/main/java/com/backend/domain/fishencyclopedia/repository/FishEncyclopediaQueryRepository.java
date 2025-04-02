package com.backend.domain.fishencyclopedia.repository;

import static com.backend.domain.fishencyclopedia.entity.QFishEncyclopedia.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.dto.response.QFishEncyclopediaResponse_Detail;
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

@Repository
@RequiredArgsConstructor
public class FishEncyclopediaQueryRepository {

	private final JPAQueryFactory queryFactory;

	// 정렬 필드를 매핑
	private static final Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
			"count", fishEncyclopedia.count,
			"length", fishEncyclopedia.length,
			"createdAt", fishEncyclopedia.createdAt
		);

	public ScrollResponse<FishEncyclopediaResponse.Detail> findDetailByAllByFishPointIdAndFishId(
		final GlobalRequest.CursorRequest pageRequestDto,
		final Long fishId,
		final Long memberId
	) {

		List<FishEncyclopediaResponse.Detail> detailList = queryFactory.select(
				new QFishEncyclopediaResponse_Detail(
					fishEncyclopedia.fishEncyclopediaId,
					fishEncyclopedia.length,
					fishEncyclopedia.count,
					fishPoint.fishPointName,
					fishPoint.fishPointDetailName,
					fishEncyclopedia.createdAt)
			)
			.from(fishEncyclopedia)
			.leftJoin(fishPoint)
			.on(fishEncyclopedia.fishPointId.eq(fishPoint.fishPointId))
			.where(whereCondition(pageRequestDto, fishId, memberId))
			.orderBy(getOrderBy(pageRequestDto))
			.limit(pageRequestDto.size() + 1)
			.fetch();

		// 다음 페이지가 있는지 확인
		boolean hasNext = detailList.size() > pageRequestDto.size();

		if (hasNext) {
			detailList.remove(detailList.size() - 1);
		}

		/**
		 * TODO 추후 isFirst 검증 로직 수정되어야햠
		 * 현재는 fieldValue가 null인 경우로 한정했지만
		 * 이전 페이지 불러올 때 fieldValue가 null이 아닐 것으로 판단되어 해당 부분 수정 필요
		 *
		 * 대략적인 구상은 조회 후 size보다 작은 데이터가 나온다면 결과 값으로
		 * 다시 사이즈만큼 조회 후 반환하면 될 것으로 생각함
		 */
		return ScrollResponse.from(
			detailList,
			pageRequestDto.size(),
			detailList.size(),
			pageRequestDto.fieldValue() == null,
			hasNext
		);
	}

	private BooleanExpression whereCondition(GlobalRequest.CursorRequest cursorRequestDto, Long fishId, Long memberId) {
		// 입력값 유효성 검사
		if (!StringUtils.hasText(cursorRequestDto.fieldValue()) || cursorRequestDto.id() == null) {
			return null;
		}

		// 기본키 ID 필드
		Long idValue = cursorRequestDto.id();
		// Sort 필드 타입 소문자로 변환
		String sortFieldType = cursorRequestDto.sort().toLowerCase();
		// Sort 필드 값
		String sortFieldValueStr = cursorRequestDto.fieldValue();
		// 정렬 순서 Order 객체로 변환
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		BooleanExpression baseBooleanExpression = fishEncyclopedia.fishId.eq(fishId)
						.and(fishEncyclopedia.memberId.eq(memberId));

		// 각 필드 타입별 처리를 추상화
		switch (sortFieldType) {
			case "count":
				try {
					Integer fieldValue = Integer.valueOf(sortFieldValueStr);

					BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
						fishEncyclopedia.fishEncyclopediaId,
						idValue,
						fishEncyclopedia.count,
						fieldValue, order
					);

					return baseBooleanExpression.and(createFieldPredicate);

				} catch (NumberFormatException e) {
					throw new GlobalException(e, GlobalErrorCode.REPOSITORY_FORMAT_PARSE_ERROR);
				}
			case "length":
				try {
					Integer fieldValue = Integer.valueOf(sortFieldValueStr);

					BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
						fishEncyclopedia.fishEncyclopediaId,
						idValue,
						fishEncyclopedia.length,
						fieldValue,
						order
					);
					return baseBooleanExpression.and(createFieldPredicate);
				} catch (NumberFormatException e) {
					throw new GlobalException(e, GlobalErrorCode.REPOSITORY_FORMAT_PARSE_ERROR);
				}
			default: //createdAT
				try {
					ZonedDateTime parseFieldValue = ZonedDateTime.parse(sortFieldValueStr);
					BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
						fishEncyclopedia.fishEncyclopediaId,
						idValue,
						fishEncyclopedia.createdAt,
						parseFieldValue,
						order
					);
					return baseBooleanExpression.and(createFieldPredicate);
				} catch (DateTimeParseException e) {
					throw new GlobalException(e, GlobalErrorCode.REPOSITORY_FORMAT_PARSE_ERROR);
				}
		}
	}

	/**
	 * 정렬할 필드와 정렬 방식을 OrderSpecifier로 반환합니다.
	 *
	 * @param pageRequestDto
	 * @return {@link OrderSpecifier}
	 */
	private OrderSpecifier<?>[] getOrderBy(GlobalRequest.CursorRequest pageRequestDto) {
    // 기본 정렬 방식 설정
    Order queryOrder = QuerydslUtil.getOrder(pageRequestDto);

    // 정렬 필드 결정
    ComparableExpressionBase<?> sortField = StringUtils.hasText(pageRequestDto.sort()) && FIELD_MAP.containsKey(pageRequestDto.sort()) ?
            FIELD_MAP.get(pageRequestDto.sort()) : fishEncyclopedia.createdAt;

    // 두 개의 OrderSpecifier를 배열로 반환
    return new OrderSpecifier<?>[] {
        new OrderSpecifier<>(queryOrder, sortField),
        new OrderSpecifier<>(Order.ASC, fishEncyclopedia.fishEncyclopediaId)
    };
}
}

