package com.backend.domain.reservationdate.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservationdate.converter.ReservationDateConverter;
import com.backend.domain.reservationdate.dto.response.ReservationDateResponse;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.entity.ReservationDateId;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationDateServiceImpl implements ReservationDateService {

	private final ShipFishingPostRepository shipFishingPostRepository;
	private final ReservationDateRepository reservationDateRepository;

	@Override
	@Transactional
	public ReservationDateResponse.Detail getReservationDate(
		final Long shipFishingPostId,
		final LocalDate reservationDate) {

		ShipFishingPost shipFishingPost = getShipFishingPostEntity(shipFishingPostId);

		ReservationDate reservationDateEntity = getReservationDateOrSave(
			shipFishingPostId, reservationDate, shipFishingPost.getMaxGuestCount());

		return ReservationDateConverter.fromReservationDateResponseDetail(reservationDateEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public ReservationDateResponse.UnAvailableDateList getReservationDateAvailableList(
		final Long shipFishingPostId,
		final LocalDate reservationDate) {

		List<LocalDate> date = getStartDateAndEndDate(reservationDate);

		List<LocalDate> unAvailableDateList = reservationDateRepository
			.findUnAvailableDatesByStartDateBetweenEndDate(shipFishingPostId, date.get(0), date.get(1));

		return ReservationDateConverter.fromUnAvailableDateList(unAvailableDateList);
	}

	/**
	 * 선상 낚시 게시글 Entity 를 반환합니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @return {@link ShipFishingPost}
	 */
	private ShipFishingPost getShipFishingPostEntity(final Long shipFishingPostId) {

		return shipFishingPostRepository.findById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));
	}

	/**
	 * 예약 일자에 정보가 존재하면 반환, 아니면 생성 및 저장 후 반환합니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param date {@link LocalDate}
	 * @param remainCount {@link Integer}
	 * @return {@link ReservationDate}
	 */
	private ReservationDate getReservationDateOrSave(
		final Long shipFishingPostId,
		final LocalDate date,
		final Integer remainCount) {

		ReservationDateId reservationDateId = ReservationDateConverter
			.fromReservationDateIdRequest(date, shipFishingPostId);

		return reservationDateRepository.findById(reservationDateId)
			.orElseGet(() -> {
				ReservationDate reservationDate = ReservationDateConverter
					.fromReservationDateRequestWithRemainCount(
						date,
						shipFishingPostId,
						remainCount
					);

				return reservationDateRepository.save(reservationDate);
			});
	}

	/**
	 * 입력된 날짜를 기반으로 해당 month 의 startDate 와 endDate 를 반환하는 메서드입니다.
	 *
	 * @param reservationDate {@link LocalDate}
	 * @return {@link List<LocalDate>}
	 */
	private List<LocalDate> getStartDateAndEndDate(final LocalDate reservationDate) {
		LocalDate now = LocalDate.now();
		LocalDate firstDayOfMonth = reservationDate.with(TemporalAdjusters.firstDayOfMonth());

		LocalDate startDate = now.isAfter(firstDayOfMonth) ? now : firstDayOfMonth;
		LocalDate endDate = reservationDate.with(TemporalAdjusters.lastDayOfMonth());
		return List.of(startDate, endDate);
	}
}
