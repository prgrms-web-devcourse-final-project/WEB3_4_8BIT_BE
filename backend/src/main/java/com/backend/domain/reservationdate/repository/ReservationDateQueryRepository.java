package com.backend.domain.reservationdate.repository;

import static com.backend.domain.reservationdate.entity.QReservationDate.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backend.domain.reservationdate.entity.ReservationDate;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
}