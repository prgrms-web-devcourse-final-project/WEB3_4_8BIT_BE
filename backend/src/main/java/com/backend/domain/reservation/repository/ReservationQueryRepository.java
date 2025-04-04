package com.backend.domain.reservation.repository;

import static com.backend.domain.member.entity.QMember.*;
import static com.backend.domain.reservation.entity.QReservation.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<ReservationResponse.DetailWithMember> findDetailWithMemberNameById(final Long reservationId) {

		ReservationResponse.DetailWithMember detailWithMember = jpaQueryFactory.select(Projections.constructor(
				ReservationResponse.DetailWithMember.class,
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
				reservation.modifiedAt
			))
			.from(reservation)
			.leftJoin(member)
			.on(reservation.memberId.eq(member.memberId))
			.where(reservation.reservationId.eq(reservationId))
			.fetchOne();

		return Optional.ofNullable(detailWithMember);
	}
}
