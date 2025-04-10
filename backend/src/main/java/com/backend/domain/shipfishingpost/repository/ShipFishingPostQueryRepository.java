package com.backend.domain.shipfishingpost.repository;

import static com.backend.domain.fish.entity.QFish.*;
import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.reservationdate.entity.QReservationDate.*;
import static com.backend.domain.review.entity.QReview.*;
import static com.backend.domain.ship.entity.QShip.*;
import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;
import static com.backend.global.storage.entity.QFile.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.domain.member.dto.MemberResponse;
import com.backend.domain.ship.dto.response.ShipResponse;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.util.QuerydslUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShipFishingPostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

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
					shipFishingPost.fileIdList,
					shipFishingPost.fishIdList,
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
					ship.restroomType,
					ship.loungeArea,
					ship.kitchenFacility,
					ship.fishingChair,
					ship.passengerInsurance,
					ship.fishingGearRental,
					ship.mealProvided,
					ship.parkingAvailable
				),
				Projections.constructor(
					MemberResponse.ContactInfo.class,
					member.memberId,
					member.email,
					member.name,
					member.phone
				)
			))
			.from(shipFishingPost)
			.join(ship).on(shipFishingPost.shipId.eq(ship.shipId))
			.join(member).on(shipFishingPost.memberId.eq(member.memberId))
			.where(shipFishingPost.shipFishingPostId.eq(shipFishingPostId))
			.fetchOne();

		return Optional.ofNullable(detailAll);
	}

	public List<ShipFishingPostResponse.MyPagePostList> findDetailMyPageListByMemberId(final Long memberId) {

		List<ShipFishingPostResponse.DetailQueryDto> detailList = jpaQueryFactory
			.select(Projections.constructor(
				ShipFishingPostResponse.DetailQueryDto.class,
				shipFishingPost.shipFishingPostId,
				shipFishingPost.subject,
				shipFishingPost.location,
				shipFishingPost.price,
				shipFishingPost.fileIdList,
				shipFishingPost.fishIdList,
				shipFishingPost.reviewEverRate,
				shipFishingPost.createdAt,
				JPAExpressions
					.select(review.count())
					.from(review)
					.where(review.shipFishingPostId.eq(shipFishingPost.shipFishingPostId))
			))
			.distinct()
			.from(shipFishingPost)
			.where(shipFishingPost.memberId.eq(memberId))
			.orderBy(shipFishingPost.createdAt.desc())
			.fetch();

		return mapToMyPagePostList(detailList);
	}

	public ScrollResponse<ShipFishingPostResponse.DetailScroll> findDetailScrollBySearch(
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		BooleanExpression conditionList = buildConditions(requestDto, cursorRequestDto);

		List<ShipFishingPostResponse.DetailQueryDto> detailList = jpaQueryFactory
			.select(Projections.constructor(
				ShipFishingPostResponse.DetailQueryDto.class,
				shipFishingPost.shipFishingPostId,
				shipFishingPost.subject,
				shipFishingPost.location,
				shipFishingPost.price,
				shipFishingPost.fileIdList,
				shipFishingPost.fishIdList,
				shipFishingPost.reviewEverRate,
				shipFishingPost.createdAt,
				JPAExpressions
					.select(review.count())
					.from(review)
					.where(review.shipFishingPostId.eq(shipFishingPost.shipFishingPostId))
			))
			.distinct()
			.from(shipFishingPost)
			.leftJoin(reservationDate1)
			.on(reservationDate1.shipFishingPostId.eq(shipFishingPost.shipFishingPostId)
				.and(reservationDateCondition(requestDto.searchDate())))
			.where(conditionList)
			.orderBy(getSortCondition(cursorRequestDto))
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		boolean hasNext = detailList.size() <= cursorRequestDto.size();

		if (!hasNext) {
			detailList.remove(detailList.size() - 1);
		}

		List<ShipFishingPostResponse.DetailScroll> content = mapToDetailScroll(detailList);

		return ScrollResponse.from(
			content,
			cursorRequestDto.size(),
			content.size(),
			cursorRequestDto.fieldValue() == null,
			hasNext);
	}

	public void updateReviewEverRate(final ZonedDateTime now, final ZonedDateTime lastRun) {

		// 현재 생성 & 수정된 이력이 있는 리뷰만 반영됨. 삭제는 리뷰 삭제시 직접 반영하거나 soft delete 를 적용한 후 반영해야 함
		List<Long> changedIdList = jpaQueryFactory
			.selectDistinct(review.shipFishingPostId)
			.from(review)
			.where(review.modifiedAt.between(lastRun, now))
			.fetch();

		log.debug("리뷰 변경 감지 {}", changedIdList);

		if (changedIdList.isEmpty()) {
			log.debug(" 리뷰가 증감된 게시글 없음");

			return;
		}

		NumberExpression<Double> avgExp = review.rating.avg();

		List<Tuple> rateList = jpaQueryFactory
			.select(review.shipFishingPostId, avgExp)
			.from(review)
			.where(review.shipFishingPostId.in(changedIdList))
			.groupBy(review.shipFishingPostId)
			.fetch();

		rateList.forEach(tuple -> {
			Long shipFishingPostId = tuple.get(review.shipFishingPostId);
			Double avg = tuple.get(avgExp);

			jpaQueryFactory.update(shipFishingPost)
				.set(shipFishingPost.reviewEverRate, avg)
				.where(shipFishingPost.shipFishingPostId.eq(shipFishingPostId))
				.execute();
		});

		// 리뷰개수가 한개 이상으로 집계된 id 리스트
		Set<Long> updated = rateList.stream()
			.map(t -> t.get(review.shipFishingPostId))
			.collect(Collectors.toSet());

		// 리뷰가 0개라서 집계되지 않은 id 리스트
		List<Long> zeroIdList = changedIdList.stream()
			.filter(id -> !updated.contains(id))
			.toList();

		if (!zeroIdList.isEmpty()) {
			jpaQueryFactory.update(shipFishingPost)
				.set(shipFishingPost.reviewEverRate, 0.0)
				.where(shipFishingPost.shipFishingPostId.in(zeroIdList))
				.execute();
		}
	}

	public void updateReviewEverRateByDeleteReview(final Long shipFishingPostId) {

		Double avg = jpaQueryFactory
			.select(review.rating.avg())
			.from(review)
			.where(review.shipFishingPostId.eq(shipFishingPostId))
			.fetchOne();

		double newAvg = avg != null ? avg : 0.0;

		jpaQueryFactory.update(shipFishingPost)
			.set(shipFishingPost.reviewEverRate, newAvg)
			.where(shipFishingPost.shipFishingPostId.eq(shipFishingPostId))
			.execute();
	}

	private List<ShipFishingPostResponse.MyPagePostList> mapToMyPagePostList(
		final List<ShipFishingPostResponse.DetailQueryDto> detailQueryDtoList
	) {
		Set<Long> fileIdList = detailQueryDtoList.stream()
			.flatMap(dto -> {
				List<Long> ids = dto.fileIdList();
				return (ids == null ? List.<Long>of() : ids).stream();
			})
			.collect(Collectors.toSet());

		Map<Long, String> fileUrlMap = jpaQueryFactory
			.select(file.fileId, file.url)
			.from(file)
			.where(file.fileId.in(fileIdList))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(file.fileId),
				tuple -> tuple.get(file.url)
			));

		return detailQueryDtoList.stream()
			.map(dto -> {
				List<String> fileUrls = Stream.ofNullable(dto.fileIdList())
					.flatMap(Collection::stream)
					.map(fileUrlMap::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

				return ShipFishingPostResponse.MyPagePostList.fromMyPagePostList(dto, fileUrls);
			})
			.collect(Collectors.toList());
	}

	private List<ShipFishingPostResponse.DetailScroll> mapToDetailScroll(
		final List<ShipFishingPostResponse.DetailQueryDto> detailQueryDtoList) {

		Set<Long> fileIdList = detailQueryDtoList.stream()
			.flatMap(dto -> {
				List<Long> ids = dto.fileIdList();
				return (ids == null ? List.<Long>of() : ids).stream();
			})
			.collect(Collectors.toSet());

		Set<Long> fishIdList = detailQueryDtoList.stream()
			.flatMap(dto -> {
				List<Long> ids = dto.fishIdList();
				return (ids == null ? List.<Long>of() : ids).stream();
			})
			.collect(Collectors.toSet());

		Map<Long, String> fileUrlMap = jpaQueryFactory
			.select(file.fileId, file.url)
			.from(file)
			.where(file.fileId.in(fileIdList))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(file.fileId),
				tuple -> tuple.get(file.url)
			));

		Map<Long, String> fishNameMap = jpaQueryFactory
			.select(fish.fishId, fish.name)
			.from(fish)
			.where(fish.fishId.in(fishIdList))
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(fish.fishId),
				tuple -> tuple.get(fish.name)
			));

		return detailQueryDtoList.stream()
			.map(dto -> {
				List<String> fileUrls = Stream.ofNullable(dto.fileIdList())
					.flatMap(Collection::stream)
					.map(fileUrlMap::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

				List<String> fishNames = Stream.ofNullable(dto.fishIdList())
					.flatMap(Collection::stream)
					.map(fishNameMap::get)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
				return ShipFishingPostResponse.DetailScroll.fromDetailScroll(dto, fileUrls, fishNames);
			})
			.collect(Collectors.toList());
	}

	public void updateLikeCount(final Long postId, final Long likeCount) {
		jpaQueryFactory.update(shipFishingPost)
			.set(shipFishingPost.likeCount, likeCount)
			.where(shipFishingPost.shipFishingPostId.eq(postId))
			.execute();
	}

	private BooleanExpression buildConditions(
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		List<BooleanExpression> expressions = Stream.of(
				getCursorCondition(cursorRequestDto),
				keywordBySubjectCondition(requestDto.keyword()),
				minPriceCondition(requestDto.minPrice()),
				maxPriceCondition(requestDto.maxPrice()),
				guestCountCondition(requestDto.guestCount()),
				reviewRatingCondition(requestDto.minRating()),
				locationCondition(requestDto.location()),
				durationTimeCondition(requestDto.duration()),
				searchDateCondition(requestDto.searchDate()),
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
		if (guestCount == null) {
			return null;
		}

		return reservationDate1.isNull()
			.and(shipFishingPost.maxGuestCount.goe(guestCount))
			.or(reservationDate1.isNotNull().and(reservationDate1.remainCount.goe(guestCount)));
	}

	private BooleanExpression reviewRatingCondition(final Double minRating) {
		return minRating != null ? shipFishingPost.reviewEverRate.goe(minRating) : null;
	}

	private BooleanExpression locationCondition(final String location) {
		return (location != null && !location.isEmpty()) ? shipFishingPost.location.containsIgnoreCase(location) :
			null;
	}

	private BooleanExpression durationTimeCondition(final LocalTime duration) {
		return duration != null ? shipFishingPost.durationTime.loe(duration) : null;
	}

	private BooleanExpression searchDateCondition(final LocalDate searchDate) {

		return searchDate == null ? null : reservationDate1.isNull()
			.or(reservationDate1.isBan.isFalse());
	}

	private BooleanExpression reservationDateCondition(final LocalDate searchDate) {

		return searchDate == null ? null : reservationDate1.reservationDate.eq(searchDate);
	}

	// 물고기 검증 : JSON_CONTAINS 고려
	private BooleanExpression targetFishCondition(final Long targetFish) {
		if (targetFish == null) {
			return null;
		}

		String targetFishStr = targetFish.toString();

		// JSON 배열에서 각 인덱스의 값을 추출
		StringTemplate fish0 = Expressions.stringTemplate("JSON_UNQUOTE(JSON_EXTRACT({0}, '$[0]'))",
			shipFishingPost.fishIdList);
		StringTemplate fish1 = Expressions.stringTemplate("JSON_UNQUOTE(JSON_EXTRACT({0}, '$[1]'))",
			shipFishingPost.fishIdList);
		StringTemplate fish2 = Expressions.stringTemplate("JSON_UNQUOTE(JSON_EXTRACT({0}, '$[2]'))",
			shipFishingPost.fishIdList);
		StringTemplate fish3 = Expressions.stringTemplate("JSON_UNQUOTE(JSON_EXTRACT({0}, '$[3]'))",
			shipFishingPost.fishIdList);
		StringTemplate fish4 = Expressions.stringTemplate("JSON_UNQUOTE(JSON_EXTRACT({0}, '$[4]'))",
			shipFishingPost.fishIdList);

		return fish0.isNotNull().and(fish0.eq(targetFishStr))
			.or(fish1.isNotNull().and(fish1.eq(targetFishStr)))
			.or(fish2.isNotNull().and(fish2.eq(targetFishStr)))
			.or(fish3.isNotNull().and(fish3.eq(targetFishStr)))
			.or(fish4.isNotNull().and(fish4.eq(targetFishStr)));
	}

	private BooleanExpression getCursorCondition(final GlobalRequest.CursorRequest cursorRequestDto) {

		// 입력값 유효성 검사
		if (!StringUtils.hasText(cursorRequestDto.fieldValue()) || cursorRequestDto.id() == null) {
			return null;
		}

		// 기본키 ID 값
		Long idValue = cursorRequestDto.id();
		// Sort 필드 값
		String sortField = cursorRequestDto.sort();
		// Sort 필드 값 변환
		String fieldValue = cursorRequestDto.fieldValue();
		// 정렬 순서 Order 객체로 변환
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return getWhereBooleanExpression(sortField, fieldValue, idValue, order);
	}

	/**
	 * Where절에 들어가야하는 조건식을 만들어 반환하는 메소드 입니다.
	 *
	 * @param sortField         정렬 필드
	 * @param sortFieldValueStr 정렬 필드 값
	 * @param idValue           기본키 ID 값
	 * @param order             {@link Order}
	 * @return
	 */
	private BooleanExpression getWhereBooleanExpression(
		final String sortField,
		final String sortFieldValueStr,
		final Long idValue,
		final Order order
	) {
		try {
			switch (sortField) {
				case "price" -> {
					Long fieldValue = Long.valueOf(sortFieldValueStr);

					return QuerydslUtil.createFieldPredicate(
						shipFishingPost.shipFishingPostId,
						idValue,
						shipFishingPost.price,
						fieldValue,
						order
					);
				}
				case "reviewEverRate" -> {
					Double fieldValue = Double.valueOf(sortFieldValueStr);

					return QuerydslUtil.createFieldPredicate(
						shipFishingPost.shipFishingPostId,
						idValue,
						shipFishingPost.reviewEverRate,
						fieldValue,
						order
					);
				}
				default -> {//createdAT
					ZonedDateTime parseFieldValue = ZonedDateTime.parse(sortFieldValueStr);

					return QuerydslUtil.createFieldPredicate(
						shipFishingPost.shipFishingPostId,
						idValue,
						shipFishingPost.createdAt,
						parseFieldValue,
						order
					);
				}
			}
		} catch (NumberFormatException | DateTimeParseException e) {
			throw new GlobalException(GlobalErrorCode.REPOSITORY_FORMAT_PARSE_ERROR);
		}
	}

	private boolean isValidColumn(final String column) {
		List<String> validColumns = Arrays.asList("createdAt", "price", "reviewEverRate");
		return validColumns.contains(column);
	}

	private OrderSpecifier<?>[] getSortCondition(
		final GlobalRequest.CursorRequest cursorRequestDto) {

		if (cursorRequestDto.sort() == null ||
			cursorRequestDto.sort().isEmpty() ||
			!isValidColumn(cursorRequestDto.sort())) {
			return new OrderSpecifier<?>[] {shipFishingPost.createdAt.desc()};
		}

		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		Expression<Comparable> path = Expressions.path(Comparable.class, shipFishingPost, cursorRequestDto.sort());

		return new OrderSpecifier<?>[] {
			new OrderSpecifier<>(order, path),
			new OrderSpecifier<>(Order.ASC, shipFishingPost.shipFishingPostId)
		};
	}
}