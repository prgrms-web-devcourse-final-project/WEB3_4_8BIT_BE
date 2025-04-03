package com.backend.domain.fishencyclopedia.repository;

import static com.backend.domain.catchmaxlength.entity.QCatchMaxLength.*;
import static com.backend.domain.fish.entity.QFish.*;
import static com.backend.domain.fishencyclopedia.entity.QFishEncyclopedia.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.dto.response.QFishEncyclopediaResponse_Detail;
import com.backend.domain.fishencyclopedia.dto.response.QFishEncyclopediaResponse_DetailPage;
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

	//fishId와 memberId가 일치하는 데이터를 가져오는 조건식 생성 함수
    private static final BiFunction<Long, Long, BooleanExpression> BOOLEAN_EXPRESSION_BI_FUNCTION =
        (fishId, memberId) -> fishEncyclopedia.fishId.eq(fishId)
            .and(fishEncyclopedia.memberId.eq(memberId));

	public ScrollResponse<FishEncyclopediaResponse.Detail> findDetailByAllByMemberIdAndFishId(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	) {

		List<FishEncyclopediaResponse.Detail> detailList = queryFactory
			.select(
				new QFishEncyclopediaResponse_Detail(
					fishEncyclopedia.fishEncyclopediaId,
					fishEncyclopedia.length,
					fishEncyclopedia.count,
					fishPoint.fishPointName,
					fishPoint.fishPointDetailName,
					fishEncyclopedia.createdAt
				)
			)
			.from(fishEncyclopedia)
			.leftJoin(fishPoint)
			.on(fishEncyclopedia.fishPointId.eq(fishPoint.fishPointId))
			.where(whereCondition(cursorRequestDto, fishId, memberId))
			.orderBy(getOrderBy(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		// 다음 페이지가 있는지 확인
		boolean hasNext = detailList.size() > cursorRequestDto.size();

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
			cursorRequestDto.size(),
			detailList.size(),
			cursorRequestDto.fieldValue() == null,
			hasNext
		);
	}

	public List<FishEncyclopediaResponse.DetailPage> findDetailPageByAllByMemberIdAndFishId(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	) {

		return queryFactory
			.select(
				new QFishEncyclopediaResponse_DetailPage(
					fishEncyclopedia.fishEncyclopediaId,
					fish.imageId,
					fish.name,
					catchMaxLength.bestLength,
					catchMaxLength.catchCount
				)
			)
			.from(fishEncyclopedia)
			.leftJoin(fish)
			.on(fishEncyclopedia.fishId.eq(fish.fishId))
			.leftJoin(catchMaxLength)
			.on(fishEncyclopedia.fishId.eq(catchMaxLength.fishId))
			.where(BOOLEAN_EXPRESSION_BI_FUNCTION.apply(fishId, memberId))
			.orderBy(getOrderBy(cursorRequestDto))
			.fetch();
	}

	private BooleanExpression whereCondition(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishId,
		final Long memberId
	) {

		BooleanExpression baseBooleanExpression = BOOLEAN_EXPRESSION_BI_FUNCTION.apply(fishId, memberId);

		// 입력값 유효성 검사
		if (!StringUtils.hasText(cursorRequestDto.fieldValue()) || cursorRequestDto.id() == null) {
			return baseBooleanExpression;
		}

		// 기본키 ID 값
		Long idValue = cursorRequestDto.id();
		// Sort 필드 소문자로 변환
		String sortField = cursorRequestDto.sort().toLowerCase();
		// Sort 필드 값
		String sortFieldValueStr = cursorRequestDto.fieldValue();
		// 정렬 순서 Order 객체로 변환
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return getWhereBooleanExpression(sortField, sortFieldValueStr, idValue, order, baseBooleanExpression);
	}

	/**
	 * Where절에 들어가야하는 조건식을 만들어 반환하는 메소드 입니다.
	 *
	 * @param sortField             정렬 필드
	 * @param sortFieldValueStr     정렬 필드 값
	 * @param idValue               기본키 ID 값
	 * @param order                 {@link Order}
	 * @param baseBooleanExpression 항상 고정인 조건식
	 * @return
	 */
	private BooleanExpression getWhereBooleanExpression(
		final String sortField,
		final String sortFieldValueStr,
		final Long idValue,
		final Order order,
		final BooleanExpression baseBooleanExpression
	) {
		try {
			switch (sortField) {
				case "count" -> {
					Integer fieldValue = Integer.valueOf(sortFieldValueStr);

					BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
						fishEncyclopedia.fishEncyclopediaId,
						idValue,
						fishEncyclopedia.count,
						fieldValue,
						order
					);

					return baseBooleanExpression.and(createFieldPredicate);
				}
				case "length" -> {
					Integer fieldValue = Integer.valueOf(sortFieldValueStr);

					BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
						fishEncyclopedia.fishEncyclopediaId,
						idValue,
						fishEncyclopedia.length,
						fieldValue,
						order
					);
					return baseBooleanExpression.and(createFieldPredicate);
				}
				default -> {//createdAT
					ZonedDateTime parseFieldValue = ZonedDateTime.parse(sortFieldValueStr);
					BooleanExpression createFieldPredicate = QuerydslUtil.createFieldPredicate(
						fishEncyclopedia.fishEncyclopediaId,
						idValue,
						fishEncyclopedia.createdAt,
						parseFieldValue,
						order
					);
					return baseBooleanExpression.and(createFieldPredicate);
				}
			}
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
				FIELD_MAP.get(pageRequestDto.sort()) : fishEncyclopedia.createdAt;

		// 두 개의 OrderSpecifier를 배열로 반환
		return new OrderSpecifier<?>[] {
			new OrderSpecifier<>(queryOrder, sortField),
			new OrderSpecifier<>(Order.ASC, fishEncyclopedia.fishEncyclopediaId)
		};
	}
}

