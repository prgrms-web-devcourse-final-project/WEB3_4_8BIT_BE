package com.backend.domain.reservationdate.service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservation.exception.ReservationErrorCode;
import com.backend.domain.reservation.exception.ReservationException;
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

		verifyTodayAfterDate(reservationDate);

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

	@Override
	@Transactional
	public void updateReservationDate(final Long shipFishingPostId, final LocalDate reservationDate,
		final Long memberId) {

		verifyTodayAfterDate(reservationDate);

		ShipFishingPost shipFishingPost = getShipFishingPostEntity(shipFishingPostId);

		verifyPostOwnership(shipFishingPost.getMemberId(), memberId);

		ReservationDate reservation = getReservationDateOrSave(shipFishingPostId, reservationDate,
			shipFishingPost.getMaxGuestCount());

		reservation.updateBan();
	}

	@Override
	@Transactional
	public void deleteReservationDateList(final Long shipFishingPostId) {

		reservationDateRepository.deleteByShipFishingPostId(shipFishingPostId);
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
		LocalDate firstDay = reservationDate.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDay = reservationDate.with(TemporalAdjusters.lastDayOfMonth());

		return List.of(firstDay, lastDay);
	}

	/**
	 * 이전 예약 조회, 수정 불가 검증 메서드
	 *
	 * @param reservationDate 예약 날짜
	 */
	private void verifyTodayAfterDate(final LocalDate reservationDate) {

		if (reservationDate.isBefore(LocalDate.now())) {
			throw new ReservationException(ReservationErrorCode.NOT_AVAILABLE_DATE_RESERVATION);
		}
	}

	/**
	 * 게시글의 소유자 여부 검증 메서드
	 *
	 * @param postOwnerId {@link Long}
	 * @param memberId {@link Long}
	 */
	private void verifyPostOwnership(final Long postOwnerId, final Long memberId) {

		if (!postOwnerId.equals(memberId)) {
			throw new ShipFishingPostException(ShipFishingPostErrorCode.NOT_AUTHORITY_POSTS);
		}
	}
}
