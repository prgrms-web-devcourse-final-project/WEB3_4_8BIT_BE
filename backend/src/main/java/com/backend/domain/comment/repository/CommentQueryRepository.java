package com.backend.domain.comment.repository;

import static com.backend.domain.comment.entity.QComment.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.global.storage.entity.QFile.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.comment.dto.request.CommentRequest;
import com.backend.domain.comment.dto.response.CommentResponse;
import com.backend.domain.comment.dto.response.QCommentResponse_Detail;
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
public class CommentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	// 정렬 필드를 매핑
	private static final Map<String, ComparableExpressionBase<?>> FIELD_MAP = Map.of(
		"createdAt", comment.createdAt
	);


	//fishId와 memberId가 일치하는 데이터를 가져오는 조건식 생성 함수
	private static final Function<Long, BooleanExpression> BOOLEAN_EXPRESSION_FUNCTION =
		comment.fishingTripPostId::eq;

	public void addChildCount(final Long parentId) {
		// 원자적 연산으로 동시성 문제 방지
		jpaQueryFactory
			.update(comment)
			.set(comment.childCount, comment.childCount.add(1))
			.where(comment.commentId.eq(parentId))
			.execute();
	}

	public ScrollResponse<CommentResponse.Detail> findDetailByFishTripPostId(
		final Long fishingTripPostId,
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final CommentRequest.Search requestDto
	) {
		List<CommentResponse.Detail> detailList = jpaQueryFactory
			.select(new QCommentResponse_Detail(
				comment.commentId,
				comment.content,
				comment.memberId.eq(memberId),
				file.url,
				comment.createdAt,
				comment.childCount,
				comment.parentId))
			.from(comment)
			.leftJoin(member)
			.on(comment.memberId.eq(member.memberId))
			.leftJoin(file)
			.on(file.fileId.eq(member.fileId))
			.where(whereCondition(requestDto, cursorRequestDto, fishingTripPostId))
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

	private BooleanExpression whereCondition(
		final CommentRequest.Search requestDto,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishingTripPostId
	) {

		BooleanExpression baseBooleanExpression = BOOLEAN_EXPRESSION_FUNCTION.apply(fishingTripPostId);

		// 입력값 유효성 검사
		if ((!StringUtils.hasText(cursorRequestDto.fieldValue()) && cursorRequestDto.id() == null) && requestDto.parentId() == null) {
			return baseBooleanExpression;
		} else if ((!StringUtils.hasText(cursorRequestDto.fieldValue()) && cursorRequestDto.id() == null) && requestDto.parentId() != null){
			return baseBooleanExpression.and(comment.parentId.eq(requestDto.parentId()));
		}

		// 기본키 ID 값
		Long idValue = cursorRequestDto.id();
		// 정렬 순서 Order 객체로 변환
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return getWhereBooleanExpression(cursorRequestDto.fieldValue(), idValue, order, baseBooleanExpression)
			.and(comment.parentId.eq(requestDto.parentId()));
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
				comment.commentId,
				idValue,
				comment.createdAt,
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
				FIELD_MAP.get(pageRequestDto.sort()) : comment.commentId;

		// 두 개의 OrderSpecifier를 배열로 반환
		return new OrderSpecifier<?>[] {
			new OrderSpecifier<>(queryOrder, sortField),
			new OrderSpecifier<>(Order.ASC, comment.commentId)
		};
	}
}
