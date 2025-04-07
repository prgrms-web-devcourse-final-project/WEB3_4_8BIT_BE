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
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

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
		// 선상 낚시 게시글 정보 조회
		ShipFishingPost shipFishingPost = getShipFishingPostEntity(requestDto.shipFishingPostId());

		// 가격 검증
		verifyPriceValue(requestDto.price(), requestDto.totalPrice(), shipFishingPost.getPrice(),
			shipFishingPost.getPrice() * requestDto.guestCount());

		// 예약 적용
		updateReservationDateWithRemainCount(requestDto.shipFishingPostId(), requestDto.reservationDate(),
			requestDto.guestCount(), false);

		// 예약 정보 저장
		Reservation reservation = reservationRepository.save(
			ReservationConverter.fromReservationRequest(requestDto, memberId));

		log.debug("선상 낚시 예약 신청 {} , {}", shipFishingPost.toString(), reservation.toString());

		return ReservationConverter.fromReservationResponseDetail(reservation);
	}

	@Override
	@Transactional(readOnly = true)
	public ReservationResponse.DetailWithMember getReservation(final Long reservationId, final Long memberId) {

		ReservationResponse.DetailWithMember responseDto = reservationRepository
			.findDetailWithMemberById(reservationId)
			.orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		verifyAuthorization(responseDto.shipFishingPostId(), responseDto.memberId(), memberId);

		return responseDto;
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<ReservationResponse.DetailWithName> getUserReservationList(final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationRepository.findDetailWithNameByMemberId(memberId, cursorRequestDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<ReservationResponse.DetailWithName> getCaptainReservationList(final Long shipFishingPostId,
		final Long memberId, final GlobalRequest.CursorRequest cursorRequestDto) {

		return reservationRepository
			.findDetailWithNameByMemberIdAndShipFishingPostId(memberId, shipFishingPostId, cursorRequestDto);
	}

	@Override
	@Transactional
	public void updateReservation(final Long reservationId, final Long memberId) {

		Reservation reservation = getReservationEntity(reservationId);

		verifyAuthorization(reservation.getShipFishingPostId(), reservation.getMemberId(), memberId);

		reservation.updateCanceled();

		// 예약 취소 적용
		updateReservationDateWithRemainCount(reservation.getShipFishingPostId(), reservation.getReservationDate(),
			reservation.getGuestCount(), true);
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
	 * 예약 일자 조회, 예약 가능하면 예약 일자 정보 남은 인원 업데이트 메서드입니다.
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param reservationDate {@link LocalDate}
	 * @param guestCount {@link Long}
	 * @param type {@link Boolean}
	 */
	private void updateReservationDateWithRemainCount(
		final Long shipFishingPostId,
		final LocalDate reservationDate,
		final Integer guestCount,
		final Boolean type) {

		ReservationDate findReservationDate = reservationDateRepository
			.findByIdWithPessimistic(shipFishingPostId, reservationDate)
			.orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

		if (type) {
			findReservationDate.remainPlus(guestCount);
		} else {
			verifyReservationDate(findReservationDate, guestCount);

			findReservationDate.remainMinus(guestCount);
		}
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
	 * @param shipFishingPostId {@link ReservationResponse.DetailWithMember}
	 * @param reservationMemberId {@link Long}
	 * @param memberId {@link Long}
	 */
	private void verifyAuthorization(
		final Long shipFishingPostId,
		final Long reservationMemberId,
		final Long memberId) {

		ShipFishingPost shipFishingPost = getShipFishingPostEntity(shipFishingPostId);

		if (!shipFishingPost.getMemberId().equals(memberId) && !reservationMemberId.equals(memberId)) {
			log.debug("권한이 없습니다.");
			throw new ReservationException(ReservationErrorCode.NOT_AUTHORITY_RESERVATION);
		}
	}

	/**
	 * 선상 낚시 예약 Entity 를 반환합니다.
	 *
	 * @param reservationId {@link Long}
	 * @return {@link Reservation}
	 */
	private Reservation getReservationEntity(final Long reservationId) {
		return reservationRepository.findById(reservationId)
			.orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
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
