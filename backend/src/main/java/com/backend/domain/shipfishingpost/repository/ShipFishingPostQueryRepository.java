package com.backend.domain.shipfishingpost.repository;

import static com.backend.domain.ship.entity.QShip.*;
import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;
import static com.backend.domain.shipfishingpostfish.entity.QShipFishingPostFish.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.QShipFishingPost;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.util.pageutil.Page;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShipFishingPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<ShipFishingPostResponse.Detail> findDetailById(final Long shipFishingPostId) {

		ShipFishingPostResponse.Detail detail = jpaQueryFactory
			.select(Projections.constructor(
				ShipFishingPostResponse.Detail.class,
				shipFishingPost.shipFishingPostId,
				shipFishingPost.subject,
				shipFishingPost.content,
				shipFishingPost.price,
				shipFishingPost.imageList,
				shipFishingPost.startTime,
				shipFishingPost.durationTime,
				shipFishingPost.maxGuestCount,
				shipFishingPost.reviewEverRate
			))
			.from(shipFishingPost)
			.where(shipFishingPost.shipFishingPostId.eq(shipFishingPostId))
			.fetchOne();

		return Optional.ofNullable(detail);
	}

	public Optional<ShipFishingPostResponse.DetailAll> findDetailAllById(final Long shipFishingPostId) {

		ShipFishingPostResponse.DetailAll detailAll = jpaQueryFactory
			.select(Projections.constructor(
				ShipFishingPostResponse.DetailAll.class,
				Projections.constructor(
					ShipFishingPostResponse.Detail.class,
					shipFishingPost.shipFishingPostId,
					shipFishingPost.subject,
					shipFishingPost.content,
					shipFishingPost.price,
					shipFishingPost.imageList,
					shipFishingPost.startTime,
					shipFishingPost.durationTime,
					shipFishingPost.maxGuestCount,
					shipFishingPost.reviewEverRate
				),
				Projections.constructor(
					ShipResponse.Detail.class,
					ship.shipId,
					ship.shipName,
					ship.shipNumber,
					ship.departurePort,
					ship.publicRestroom,
					ship.loungeArea,
					ship.kitchenFacility,
					ship.fishingChair,
					ship.passengerInsurance,
					ship.fishingGearRental,
					ship.mealProvided,
					ship.parkingAvailable
				)
			))
			.from(shipFishingPost)
			.join(ship).on(shipFishingPost.shipId.eq(ship.shipId))
			.where(shipFishingPost.shipFishingPostId.eq(shipFishingPostId))
			.fetchOne();

		return Optional.ofNullable(detailAll);
	}

	public Page<ShipFishingPostResponse.DetailPage> findDetailPage(
		final ShipFishingPostRequest.Search requestDto,
		final Pageable pageable) {

		BooleanExpression conditions = buildConditions(requestDto);

		List<ShipFishingPostResponse.DetailPage> detailPages = jpaQueryFactory
			.select(Projections.constructor(
				ShipFishingPostResponse.DetailPage.class,
				shipFishingPost.shipFishingPostId,
				shipFishingPost.subject,
				shipFishingPost.location,
				shipFishingPost.price,
				shipFishingPost.startTime,
				shipFishingPost.endTime,
				shipFishingPost.durationTime,
				shipFishingPost.imageList,
				shipFishingPost.reviewEverRate
			))
			.from(shipFishingPost)
			.join(shipFishingPostFish)
			.on(shipFishingPost.shipFishingPostId.eq(shipFishingPostFish.shipFishingPostId))
			.where(conditions)
			.orderBy(getSortCondition(pageable, shipFishingPost))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = jpaQueryFactory
			.select(shipFishingPost.shipFishingPostId.countDistinct())
			.from(shipFishingPost)
			.join(shipFishingPostFish)
			.on(shipFishingPost.shipFishingPostId.eq(shipFishingPostFish.shipFishingPostId))
			.where(conditions)
			.fetchOne();

		int totalPages = (int)Math.ceil((double)totalCount / pageable.getPageSize());
		return new Page<>(
			detailPages,
			pageable.getPageNumber(),
			pageable.getPageSize(),
			totalCount.intValue(),
			totalPages
		);
	}

	private BooleanExpression buildConditions(final ShipFishingPostRequest.Search requestDto) {
		List<BooleanExpression> expressions = Stream.of(
				keywordBySubjectCondition(requestDto.keyword()),
				minPriceCondition(requestDto.minPrice()),
				maxPriceCondition(requestDto.maxPrice()),
				guestCountCondition(requestDto.guestCount()),
				reviewRatingCondition(requestDto.minRating()),
				locationCondition(requestDto.location()),
				durationTimeCondition(requestDto.duration()),
				targetFishCondition(requestDto.fishId())
			).filter(Objects::nonNull)
			.toList();

		return expressions.stream()
			.reduce(BooleanExpression::and)
			.orElse(null);
	}

	private BooleanExpression keywordBySubjectCondition(final String keyword) {
		return (keyword != null && !keyword.isEmpty()) ? shipFishingPost.subject.containsIgnoreCase(keyword) : null;
	}

	private BooleanExpression minPriceCondition(final Long minPrice) {
		return minPrice != null ? shipFishingPost.price.goe(minPrice) : null;
	}

	private BooleanExpression maxPriceCondition(final Long maxPrice) {
		return maxPrice != null ? shipFishingPost.price.loe(maxPrice) : null;
	}

	private BooleanExpression guestCountCondition(final Long guestCount) {
		return guestCount != null ? shipFishingPost.maxGuestCount.goe(guestCount) : null;
	}

	private BooleanExpression reviewRatingCondition(final Double minRating) {
		return minRating != null ? shipFishingPost.reviewEverRate.goe(minRating) : null;
	}

	private BooleanExpression locationCondition(final String location) {
		return (location != null && !location.isEmpty()) ? shipFishingPost.location.containsIgnoreCase(location) : null;
	}

	private BooleanExpression durationTimeCondition(final LocalTime duration) {
		return duration != null ? shipFishingPost.durationTime.loe(duration) : null;
	}

	private BooleanExpression targetFishCondition(final Long targetFish) {
		return targetFish != null ? shipFishingPostFish.fishId.eq(targetFish) : null;
	}

	// Todo : 예약 도메인 추가 후 진행
	// private BooleanExpression searchDateCondition(final LocalDate searchDate) {
	// 	return searchDate != null ?   : null;
	// }

	private boolean isValidColumn(final String column) {
		List<String> validColumns = Arrays.asList("createdAt", "modifiedAt", "price", "reviewEverRate");
		return validColumns.contains(column);
	}

	private OrderSpecifier<?>[] getSortCondition(final Pageable pageable, final QShipFishingPost shipFishingPost) {
		if (pageable.getSort().isEmpty()) {
			return new OrderSpecifier<?>[] {shipFishingPost.createdAt.desc()};
		}

		List<OrderSpecifier<?>> orders = new ArrayList<>();

		for (Sort.Order order : pageable.getSort()) {
			String column = order.getProperty();
			if (!isValidColumn(column)) {
				throw new GlobalException(GlobalErrorCode.WRONG_SORT_CONDITION);
			}

			Expression<Comparable> path = Expressions.path(Comparable.class, shipFishingPost, column);
			orders.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, path));
		}
		return orders.toArray(new OrderSpecifier[0]);
	}
}