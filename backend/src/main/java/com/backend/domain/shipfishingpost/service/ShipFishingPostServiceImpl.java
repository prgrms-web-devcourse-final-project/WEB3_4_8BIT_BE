package com.backend.domain.shipfishingpost.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.reservation.entity.Reservation;
import com.backend.domain.reservation.repository.ReservationRepository;
import com.backend.domain.reservationdate.converter.ReservationDateConverter;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.reservationdate.service.ReservationDateService;
import com.backend.domain.ship.entity.Ship;
import com.backend.domain.ship.exception.ShipErrorCode;
import com.backend.domain.ship.exception.ShipException;
import com.backend.domain.ship.repository.ShipRepository;
import com.backend.domain.shipfishingpost.converter.ShipFishingPostConverter;
import com.backend.domain.shipfishingpost.dto.request.ShipFishingPostRequest;
import com.backend.domain.shipfishingpost.dto.response.ShipFishingPostResponse;
import com.backend.domain.shipfishingpost.entity.ShipFishingPost;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.domain.shipfishingpostfish.converter.ShipFishingPostFishConverter;
import com.backend.domain.shipfishingpostfish.entity.ShipFishingPostFish;
import com.backend.domain.shipfishingpostfish.repository.ShipFishingPostFishRepository;
import com.backend.global.dto.request.GlobalRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostServiceImpl implements ShipFishingPostService {

	private final ReservationDateService reservationDateService;

	private final FishRepository fishRepository;
	private final ShipRepository shipRepository;
	private final ReservationRepository reservationRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final ReservationDateRepository reservationDateRepository;
	private final ShipFishingPostFishRepository shipFishingPostFishRepository;

	@Override
	@Transactional
	public Long saveShipFishingPost(final ShipFishingPostRequest.Create requestDto, final Long memberId) {

		ShipFishingPost shipFishingPost = ShipFishingPostConverter.fromShipFishingPostRequestCreate(requestDto,
			memberId);

		// Verify : 선박 등록 여부 & 선박 소유자 정보 일치 검증
		verifyShipOwnership(shipFishingPost.getShipId(), memberId);

		// Verify : 물고기 검증
		verifyFishList(shipFishingPost.getFishList());

		Long savedShipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		saveUnAvailableDateList(requestDto, savedShipFishingPostId);

		saveFishList(requestDto, savedShipFishingPostId);

		log.debug("Save ship fish posts: {}", shipFishingPost);
		return savedShipFishingPostId;
	}

	@Override
	@Transactional(readOnly = true)
	public ShipFishingPostResponse.Detail getShipFishingPost(final Long shipFishingPostId) {

		return shipFishingPostRepository.findDetailById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));
	}

	@Override
	@Transactional(readOnly = true)
	public ShipFishingPostResponse.DetailAll getShipFishingPostAll(final Long shipFishingPostId) {

		return shipFishingPostRepository.findDetailAllById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<ShipFishingPostResponse.DetailPage> getShipFishingPostPage(
		final ShipFishingPostRequest.Search requestDto,
		final GlobalRequest.PageRequest pageRequestDto) {

		Pageable pageable = convertPageable(pageRequestDto);

		return shipFishingPostRepository.findAllBySearchAndCondition(requestDto, pageable);
	}

	@Override
	@Transactional
	public void deleteShipFishingPost(final Long shipFishingPostId, final Long memberId) {

		verifyPostOwnership(shipFishingPostId, memberId);

		verifyReservationExist(shipFishingPostId);

		shipFishingPostRepository.deleteById(shipFishingPostId);

		reservationDateService.deleteReservationDateList(shipFishingPostId);
	}

	/**
	 * shipId 등록 여부 & 선박 소유자 정보 일치 검증 메서드
	 *
	 * @param shipId {@link Long}
	 * @param memberId {@link Long}
	 */
	private void verifyShipOwnership(final Long shipId, final Long memberId) {

		Ship ship = shipRepository.findById(shipId)
			.orElseThrow(() -> new ShipException(ShipErrorCode.SHIP_NOT_FOUND));

		if (!ship.getMemberId().equals(memberId)) {
			throw new ShipException(ShipErrorCode.SHIP_MISMATCH_MEMBER_ID);
		}
	}

	/**
	 * 게시글의 소유자 여부 검증 메서드
	 *
	 * @param shipFishingPostId {@link Long}
	 * @param memberId {@link Long}
	 */
	private void verifyPostOwnership(final Long shipFishingPostId, final Long memberId) {
		ShipFishingPost shipFishingPost = getShipFishingPostEntity(shipFishingPostId);

		if (!shipFishingPost.getMemberId().equals(memberId)) {
			throw new ShipFishingPostException(ShipFishingPostErrorCode.NOT_AUTHORITY_POSTS);
		}
	}

	/**
	 * 저장하려는 물고기 정보가 있는지 검증 메서드
	 *
	 * @param fishList {@link List}
	 */
	private void verifyFishList(final List<Long> fishList) {

		List<Fish> findFishList = fishRepository.findAllById(fishList);

		if (findFishList.size() != fishList.size()) {
			throw new FishException(FishErrorCode.FISH_NOT_FOUND);
		}
	}

	/**
	 * 예약 불가 날짜 저장 메서드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @param shipFishingPostId {@link Long}
	 */
	private void saveUnAvailableDateList(final ShipFishingPostRequest.Create requestDto, final Long shipFishingPostId) {
		if (requestDto.unavailableDates().isEmpty()) {
			return;
		}

		List<ReservationDate> reservationDateList = ReservationDateConverter.fromReservationDateList
			(requestDto.unavailableDates(), shipFishingPostId, true);

		reservationDateRepository.saveAllByBulkQuery(reservationDateList, requestDto.unavailableDates().size());
	}

	/**
	 * 물고기 리스트 저장 메서드
	 *
	 * @param requestDto {@link ShipFishingPostRequest.Create}
	 * @param shipFishingPostId {@link Long}
	 */
	private void saveFishList(final ShipFishingPostRequest.Create requestDto, final Long shipFishingPostId) {
		if (requestDto.fishList().isEmpty()) {
			return;
		}

		List<ShipFishingPostFish> shipFishingPostFishList = ShipFishingPostFishConverter.fromShipFishingPostFishRequestFishIdList
			(shipFishingPostId, requestDto.fishList());

		shipFishingPostFishRepository.saveAllByBulkQuery(shipFishingPostFishList, requestDto.fishList().size());
	}

	/**
	 * 삭제할 게시글의 남은 예약 내역 검증
	 *
	 * @param shipFishingPostId {@link Long}
	 */
	private void verifyReservationExist(final Long shipFishingPostId) {

		List<Reservation> reservationList = reservationRepository
			.findByShipFishingPostIdAndTodayAfter(shipFishingPostId, LocalDate.now());

		if (!reservationList.isEmpty()) {
			throw new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_RESERVATION_EXIST);
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

	/**
	 * pageRequestDTO -> pageable
	 *
	 * @param pageRequestDto {@link }
	 * @return {@link Pageable}
	 */
	private Pageable convertPageable(final GlobalRequest.PageRequest pageRequestDto) {
		return PageRequest.of(
			pageRequestDto.page(),
			pageRequestDto.size(),
			Sort.by(Sort.Direction.fromString(
					pageRequestDto.order().equals("asc") ? "asc" : "desc"),
				pageRequestDto.sort()));
	}
}