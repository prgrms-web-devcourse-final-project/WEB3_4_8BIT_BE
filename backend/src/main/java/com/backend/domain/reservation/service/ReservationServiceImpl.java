package com.backend.domain.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.reservation.converter.ReservationConverter;
import com.backend.domain.reservation.dto.request.ReservationRequest;
import com.backend.domain.reservation.dto.response.ReservationResponse;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.exception.ReservationErrorCode;
import com.backend.domain.reservation.exception.ReservationException;
import com.backend.domain.reservation.repository.ReservationRepository;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.reservationdate.service.ReservationDateService;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final EntityManager entityManager;
	private final ReservationRepository reservationRepository;
	private final ReservationDateService reservationDateService;
	private final ReservationDateRepository reservationDateRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;

	@Override
	@Transactional
	public ReservationResponse.Detail createReservation(ReservationRequest.Reserve requestDto, Long memberId) {
		log.debug("createReservation transaction start");
		// 선상 낚시 게시글 정보 조회
		ShipFishingPost shipFishingPost = getShipFishingPostEntity(requestDto.shipFishingPostId());

		// 가격 검증
		verifyPriceValue(requestDto.price(), requestDto.totalPrice(), shipFishingPost.getPrice(),
			shipFishingPost.getPrice() * requestDto.guestCount());

		// 예약 적용
		reservationDateService.updateReservationDateWithRemainCount(requestDto.shipFishingPostId(),
			requestDto.reservationDate(), requestDto.guestCount());

		// 예약 정보 저장
		Reservation reservation = reservationRepository.save(
			ReservationConverter.fromReservationRequest(requestDto, memberId));

		log.debug("createReservation transaction end");
		return ReservationConverter.fromReservationResponseDetail(reservation);
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
}
