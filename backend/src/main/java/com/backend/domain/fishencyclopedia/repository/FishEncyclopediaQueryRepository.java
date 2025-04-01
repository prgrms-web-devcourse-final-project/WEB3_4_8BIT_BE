package com.backend.domain.fishencyclopedia.repository;

import static com.backend.domain.fishencyclopedia.entity.QFishEncyclopedia.*;
import static com.backend.domain.fishpoint.entity.QFishPoint.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.fishencyclopedia.dto.response.FishEncyclopediaResponse;
import com.backend.domain.fishencyclopedia.dto.response.QFishEncyclopediaResponse_Detail;
import com.backend.global.dto.request.GlobalRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FishEncyclopediaQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<FishEncyclopediaResponse.Detail> findDetailByAllByFishPointIdAndFishId(
		final GlobalRequest.PageRequest pageRequestDto,
		final Long fishId,
		final Long memberId) {

		Pageable pageable = PageRequest.of(pageRequestDto.page(), pageRequestDto.size());

		List<FishEncyclopediaResponse.Detail> detailList = queryFactory.selectDistinct(
				new QFishEncyclopediaResponse_Detail(
					fishEncyclopedia.length,
					fishEncyclopedia.count,
					fishPoint.fishPointName,
					fishPoint.fishPointDetailName,
					fishEncyclopedia.createdAt)
			)
			.from(fishEncyclopedia)
			.leftJoin(fishPoint)
			.on(fishEncyclopedia.fishPointId.eq(fishPoint.fishPointId))
			.offset(pageable.getPageNumber())
			.limit(pageable.getPageSize() + 1)
			.where(fishEncyclopedia.fishId.eq(fishId), fishEncyclopedia.memberId.eq(memberId))
			.orderBy(getOrderBy(pageRequestDto))
			.fetch();

        // 다음 페이지가 있는지 확인
        boolean hasNext = detailList.size() > pageRequestDto.size();

        if (hasNext) {
            detailList.remove(detailList.size() - 1);
        }

		return new SliceImpl<>(detailList, pageable, hasNext);
	}

	/**
	 * 정렬할 필드와 정렬 방식을 OrderSpecifier로 반환합니다.
	 *
	 * @param pageRequestDto
	 * @return {@link OrderSpecifier}
	 */
	private OrderSpecifier<?> getOrderBy(GlobalRequest.PageRequest pageRequestDto) {
		// 기본 정렬 방식 설정
		Order queryOrder = Order.ASC.toString().equalsIgnoreCase(pageRequestDto.order()) ?
				Order.ASC :
				Order.DESC; // 기본 정렬은 DESC로 동작

		// 정렬 필드를 매핑
		Map<String, ComparableExpressionBase<?>> fieldMap = Map.of(
			"count", fishEncyclopedia.count,
			"length", fishEncyclopedia.length,
			"createdAt", fishEncyclopedia.createdAt
		);

		// fieldMap에 일치하는게 없다면 기본 값 createdAt로 설정
		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(pageRequestDto.sort()) && fieldMap.containsKey(
				pageRequestDto.sort()) ?
				fieldMap.get(pageRequestDto.sort()) : fishEncyclopedia.createdAt;

		return new OrderSpecifier<>(queryOrder, sortField);
	}
}