package com.backend.domain.reservation.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservation.converter.ReservationConverter;
import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.exception.ReservationErrorCode;
import com.backend.domain.reservation.exception.ReservationException;
import com.backend.domain.reservation.repository.ReservationRepository;
import com.backend.domain.reservationdate.entity.ReservationDate;
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
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final ReservationDateRepository reservationDateRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;

	@Override
	@Transactional
	public ReservationResponse.Detail createReservation(final ReservationRequest.Reserve requestDto,
		final Long memberId) {
		log.debug("예약 신청 transaction start");
		// 선상 낚시 게시글 정보 조회
		ShipFishingPost shipFishingPost = getShipFishingPostEntity(requestDto.shipFishingPostId());

		// 가격 검증
		verifyPriceValue(requestDto.price(), requestDto.totalPrice(), shipFishingPost.getPrice(),
			shipFishingPost.getPrice() * requestDto.guestCount());

		// 예약 적용
		updateReservationDateWithRemainCount(requestDto.shipFishingPostId(), requestDto.reservationDate(),
			requestDto.guestCount());

		// 예약 정보 저장
		Reservation reservation = reservationRepository.save(
			ReservationConverter.fromReservationRequest(requestDto, memberId));

		log.debug("예약 신청 transaction end");
		return ReservationConverter.fromReservationResponseDetail(reservation);
	}

	@Override
	@Transactional(readOnly = true)
	public ReservationResponse.DetailWithMember getReservation(final Long reservationId, final Long memberId) {

		ReservationResponse.DetailWithMember responseDto = reservationRepository
			.findDetailWithMemberById(reservationId)
			.orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		verifyAuthorization(responseDto, memberId);

		return responseDto;
	}

	/**
	 * 가격 검증 메서드입니다.
	 *
	 * @param inputPrice {@link Long}
	 * @param inputTotalPrice {@link Long}
	 * @param serverPrice {@link Long}
	 * @param serverTotalPrice {@link Long}
	 */
	private void verifyPriceValue(final Long inputPrice, final Long inputTotalPrice, final Long serverPrice,
		final Long serverTotalPrice) {

		if (!serverPrice.equals(inputPrice)) {
			throw new ReservationException(ReservationErrorCode.WRONG_PRICE_VALUE);
		}

		if (!serverTotalPrice.equals(inputTotalPrice)) {
			throw new ReservationException(ReservationErrorCode.WRONG_PRICE_VALUE);
		}
	}

	/**
	 * 예약 일자 조회, 예약 가능하면 예약 일자 정보 남은 인원 차감 메서드입니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @param guestCount {@link Long}
	 */
	private void updateReservationDateWithRemainCount(final Long shipFishingPostId, final LocalDate reservationDate,
		final Integer guestCount) {

		log.debug("예약 일자 검증 및 업데이트 method start");

		ReservationDate findReservationDate = reservationDateRepository
			.findByIdWithPessimistic(shipFishingPostId, reservationDate)
			.orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		verifyReservationDate(findReservationDate, guestCount);

		findReservationDate.remainMinus(guestCount);

		reservationDateRepository.save(findReservationDate);

		log.debug("예약 일자 예약 여부 검증 및 잔여인원 차감 method end");
	}

	/**
	 * 선택한 예약 일자의 예약 가능 여부를 검증하는 메서드입니다.
	 *
	 * @param reservationDate {@link ReservationDate}
	 * @param guestCount {@link Integer}
	 */
	private void verifyReservationDate(final ReservationDate reservationDate, final int guestCount) {

		if (reservationDate.getIsBan()) {
			throw new ReservationException(ReservationErrorCode.NOT_AVAILABLE_DATE_RESERVATION);
		}

		if (reservationDate.getRemainCount() < guestCount) {
			throw new ReservationException(ReservationErrorCode.NOT_AVAILABLE_END_RESERVATION);
		}
	}

	/**
	 * 예약 소유자 인지 예약을 한 게시글의 선상인지 검증하는 메서드입니다.
	 *
	 * @param responseDto {@link ReservationResponse.DetailWithMember}
	 * @param memberId {@link Long}
	 */
	private void verifyAuthorization(final ReservationResponse.DetailWithMember responseDto, final Long memberId) {

		ShipFishingPost shipFishingPost = getShipFishingPostEntity(responseDto.shipFishingPostId());

		if (!shipFishingPost.getMemberId().equals(memberId) && !responseDto.memberId().equals(memberId)) {
			throw new ReservationException(ReservationErrorCode.NOT_AUTHORITY_RESERVATION);
		}
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

}
