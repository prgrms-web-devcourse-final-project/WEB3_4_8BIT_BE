package com.backend.domain.reservationdate.repository;

import static com.backend.domain.reservationdate.entity.QReservationDate.*;
import static com.backend.domain.shipfishingpost.entity.QShipFishingPost.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationDateQueryRepository {

	private final JdbcTemplate jdbcTemplate;
	private final JPAQueryFactory jpaQueryFactory;

	public void batchInsert(final List<ReservationDate> reservationDateList, final int batchSize) {
		String sql = "INSERT INTO reservation_dates " +
			"(ship_fishing_post_id, reservation_date, remain_count, is_ban, created_at, modified_at) " +
			"VALUES (?, ?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(sql, reservationDateList, batchSize, (ps, reservationDate) -> {
			ps.setLong(1, reservationDate.getShipFishingPostId());
			ps.setDate(2, java.sql.Date.valueOf(reservationDate.getReservationDate()));
			ps.setInt(3, reservationDate.getRemainCount());
			ps.setBoolean(4, reservationDate.getIsBan());
			ps.setTimestamp(5, java.sql.Timestamp.from(reservationDate.getCreatedAt().toInstant()));
			ps.setTimestamp(6, java.sql.Timestamp.from(reservationDate.getModifiedAt().toInstant()));
		});
	}

	public List<LocalDate> findUnAvailableDatesByStartDateBetweenEndDate(
		final Long shipFishingPostId,
		final LocalDate startDate,
		final LocalDate endDate) {

		return jpaQueryFactory
			.select(reservationDate1.reservationDate)
			.from(reservationDate1)
			.where(
				reservationDate1.shipFishingPostId.eq(shipFishingPostId),
				reservationDate1.reservationDate.between(startDate, endDate),
				reservationDate1.isBan.eq(true)
					.or(reservationDate1.remainCount.eq(0))
			)
			.orderBy(reservationDate1.reservationDate.asc())
			.fetch();
	}

	public Optional<ReservationDate> findByShipFishingPostIdAndReservationDate(
		final Long shipFishingPostId,
		final LocalDate reservationDate) {

		ReservationDate reservation = jpaQueryFactory
			.selectFrom(reservationDate1)
			.where(reservationDate1.shipFishingPostId.eq(shipFishingPostId)
				.and(reservationDate1.reservationDate.eq(reservationDate)))
			.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.fetchOne();

		return Optional.ofNullable(reservation);
	}

	public void plusRemainCount(final Long shipFishingPostId, final Integer updateCount, final LocalDate today) {

		jpaQueryFactory.update(reservationDate1)
			.set(reservationDate1.remainCount, reservationDate1.remainCount.add(updateCount))
			.where(afterTodayAndIsBanFalse(shipFishingPostId, today))
			.execute();
	}

	public void minusRemainCount(final Long shipFishingPostId, final Integer updateCount, final LocalDate today) {

		NumberExpression<Integer> adjusted = new CaseBuilder()
			.when(reservationDate1.remainCount.add(updateCount).gt(0))
			.then(reservationDate1.remainCount.add(updateCount))
			.otherwise(0);

		jpaQueryFactory.update(reservationDate1)
			.set(reservationDate1.remainCount, adjusted)
			.where(afterTodayAndIsBanFalse(shipFishingPostId, today))
			.execute();
	}

	public void deleteByShipFishingPostId(final Long shipFishingPostId) {

		jpaQueryFactory.delete(reservationDate1)
			.where(reservationDate1.shipFishingPostId.eq(shipFishingPostId))
			.execute();
	}

	public void deleteOrphanReservationDate() {

		jpaQueryFactory.delete(reservationDate1)
			.where(
				reservationDate1.shipFishingPostId
					.notIn(JPAExpressions.select(shipFishingPost.shipFishingPostId).from(shipFishingPost))
			)
			.execute();
	}

	private BooleanExpression afterTodayAndIsBanFalse(
		final Long shipFishingPostId,
		final LocalDate today) {
		return reservationDate1.shipFishingPostId.eq(shipFishingPostId)
			.and(reservationDate1.reservationDate.goe(today))
			.and(reservationDate1.isBan.eq(false));
	}
}