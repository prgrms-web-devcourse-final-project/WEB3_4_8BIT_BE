package com.backend.domain.reservation.repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.reservation.entity.QReservation.*;
import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;
import static com.backend.global.storage.entity.QFile.*;

import java.time.LocalDate;
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

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.QReservation;
import com.backend.domain.reservation.entity.ReservationStatus;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.util.QuerydslUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<ReservationResponse.DetailWithMember> findDetailWithMemberNameById(final Long reservationId) {

		ReservationResponse.DetailWithMember detailWithMember = jpaQueryFactory.select(
				Projections.constructor(ReservationResponse.DetailWithMember.class,
					reservation.reservationId,
					reservation.shipFishingPostId,
					member.memberId,
					member.name,
					member.phone,
					reservation.reservationNumber,
					reservation.guestCount,
					reservation.price,
					reservation.totalPrice,
					reservation.reservationDate,
					reservation.status,
					reservation.createdAt,
					reservation.modifiedAt))
			.from(reservation)
			.leftJoin(member)
			.on(reservation.memberId.eq(member.memberId))
			.where(reservation.reservationId.eq(reservationId))
			.fetchOne();

		return Optional.ofNullable(detailWithMember);
	}

	ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberId(final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		List<ReservationResponse.DetailWithName> detailWithNameList = jpaQueryFactory
			.select(Projections.constructor(
				ReservationResponse.DetailWithName.class,
				reservation.reservationId,
				reservation.shipFishingPostId,
				member.name,
				reservation.reservationNumber,
				reservation.guestCount,
				reservation.reservationDate,
				reservation.status,
				reservation.createdAt,
				reservation.modifiedAt
			))
			.from(reservation)
			.leftJoin(member)
			.on(reservation.memberId.eq(member.memberId))
			.where(member.memberId.eq(memberId).and(getCursorCondition(cursorRequestDto)))
			.orderBy(getSortCondition(reservation))
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		boolean hasNext = detailWithNameList.size() > cursorRequestDto.size();

		if (hasNext) {
			detailWithNameList.remove(detailWithNameList.size() - 1);
		}

		return ScrollResponse.from(
			detailWithNameList,
			cursorRequestDto.size(),
			detailWithNameList.size(),
			cursorRequestDto.fieldValue() == null,
			!hasNext);
	}

	ScrollResponse<ReservationResponse.DetailReservationList> findDetailReservationListByMemberId(
		final Long memberId,
		final Boolean afterToday,
		final Boolean isConfirm,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		List<ReservationResponse.DetailQueryDto> detailList = jpaQueryFactory
			.select(Projections.constructor(
				ReservationResponse.DetailQueryDto.class,
				reservation.reservationId,
				reservation.shipFishingPostId,
				reservation.reservationNumber,
				shipFishingPost.subject,
				reservation.reservationDate,
				shipFishingPost.startTime,
				shipFishingPost.location,
				reservation.guestCount,
				reservation.totalPrice,
				reservation.status,
				shipFishingPost.fileIdList,
				reservation.createdAt
			))
			.distinct()
			.from(reservation)
			.leftJoin(shipFishingPost)
			.on(reservation.shipFishingPostId.eq(shipFishingPost.shipFishingPostId))
			.where(ExpressionUtils.allOf(
				memberCondition(memberId),
				getCursorCondition(cursorRequestDto),
				statusCondition(isConfirm),
				dayCondition(afterToday)))
			.orderBy(reservation.reservationDate.desc())
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		boolean hasNext = detailList.size() <= cursorRequestDto.size();

		if (!hasNext) {
			detailList.remove(detailList.size() - 1);
		}

		List<ReservationResponse.DetailReservationList> content = mapToDetailReservationList(detailList);

		return ScrollResponse.from(
			content,
			cursorRequestDto.size(),
			content.size(),
			cursorRequestDto.fieldValue() == null,
			hasNext);
	}

	ScrollResponse<ReservationResponse.DetailWithName> findDetailWithNameByMemberIdAndShipFishingPostId(
		final Long memberId,
		final Long shipFishingPostId,
		final Boolean afterToday,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		List<ReservationResponse.DetailWithName> detailWithNameList = jpaQueryFactory
			.select(Projections.constructor(
				ReservationResponse.DetailWithName.class,
				reservation.reservationId,
				reservation.shipFishingPostId,
				member.name,
				reservation.reservationNumber,
				reservation.guestCount,
				reservation.reservationDate,
				reservation.status,
				reservation.createdAt,
				reservation.modifiedAt
			))
			.distinct()
			.from(reservation)
			.leftJoin(member)
			.on(reservation.memberId.eq(member.memberId))
			.leftJoin(shipFishingPost)
			.on(shipFishingPost.memberId.eq(memberId))
			.where(
				ExpressionUtils.allOf(
					shipFishingPostIdCondition(shipFishingPostId),
					getCursorCondition(cursorRequestDto),
					statusCondition(true),
					dayCondition(afterToday))
			)
			.orderBy(getSortCondition(reservation))
			.limit(cursorRequestDto.size() + 1)
			.fetch();

		boolean hasNext = detailWithNameList.size() <= cursorRequestDto.size();

		if (!hasNext) {
			detailWithNameList.remove(detailWithNameList.size() - 1);
		}

		return ScrollResponse.from(
			detailWithNameList,
			cursorRequestDto.size(),
			detailWithNameList.size(),
			cursorRequestDto.fieldValue() == null,
			hasNext);
	}

	Boolean findReservationListByShipFishingPostIdWithReservationConfirmAfterToday(
		final Long shipFishingPostId, final LocalDate today) {

		return jpaQueryFactory
			.selectOne()
			.from(reservation)
			.where(reservation.shipFishingPostId.eq(shipFishingPostId)
				.and(reservation.reservationDate.goe(today))
				.and(reservation.status.eq(ReservationStatus.CONFIRMED)))
			.fetchFirst() != null;
	}

	private List<ReservationResponse.DetailReservationList> mapToDetailReservationList(
		final List<ReservationResponse.DetailQueryDto> detailQueryDtoList
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

				return ReservationResponse.DetailReservationList.fromDetailReservationList(dto, fileUrls);
			})
			.collect(Collectors.toList());
	}

	private BooleanExpression memberCondition(final Long memberId) {
		return memberId == null ? null : reservation.memberId.eq(memberId);
	}

	private BooleanExpression dayCondition(final Boolean afterToday) {
		if (afterToday == null) {
			return null;
		}

		LocalDate today = LocalDate.now();

		return afterToday ? reservation.reservationDate.goe(today) : reservation.reservationDate.before(today);
	}

	private BooleanExpression statusCondition(final Boolean isConfirm) {
		if (isConfirm == null) {
			return null;
		}

		return isConfirm ? reservation.status.eq(ReservationStatus.CONFIRMED) :
			reservation.status.eq(ReservationStatus.CANCELLED);
	}

	private BooleanExpression shipFishingPostIdCondition(final Long shipFishingPostId) {
		return shipFishingPostId != null ? reservation.shipFishingPostId.eq(shipFishingPostId) : null;
	}

	private BooleanExpression getCursorCondition(final GlobalRequest.CursorRequest cursorRequestDto) {

		// 입력값 유효성 검사
		if (!StringUtils.hasText(cursorRequestDto.fieldValue()) || cursorRequestDto.id() == null) {
			return null;
		}

		// 기본키 ID 값
		Long idValue = cursorRequestDto.id();
		// Sort 필드 값
		String sortFieldValueStr = cursorRequestDto.fieldValue();
		// Sort 필드 값 변환
		LocalDate fieldValue = LocalDate.parse(sortFieldValueStr);
		// 정렬 순서 Order 객체로 변환
		Order order = QuerydslUtil.getOrder(cursorRequestDto);

		return QuerydslUtil.createFieldPredicate(reservation.reservationId, idValue, reservation.reservationDate,
			fieldValue, order);
	}

	private OrderSpecifier<?>[] getSortCondition(final QReservation reservation) {

		return new OrderSpecifier<?>[] {
			reservation.reservationDate.desc(),
			reservation.reservationId.asc()
		};
	}
}
