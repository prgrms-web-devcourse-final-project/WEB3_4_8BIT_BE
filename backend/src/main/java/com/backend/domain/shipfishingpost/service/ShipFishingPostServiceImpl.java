package com.backend.domain.shipfishingpost.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.dto.FishResponse;
import com.backend.domain.fish.entity.Fish;
import com.backend.domain.fish.exception.FishErrorCode;
import com.backend.domain.fish.exception.FishException;
import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.reservation.repository.ReservationRepository;
import com.backend.domain.reservationdate.converter.ReservationDateConverter;
import com.backend.domain.reservationdate.entity.ReservationDate;
import com.backend.domain.reservationdate.repository.ReservationDateRepository;
import com.backend.domain.reservationdate.service.ReservationDateService;
import com.backend.domain.review.repository.ReviewRepository;
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
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;
import com.backend.global.storage.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipFishingPostServiceImpl implements ShipFishingPostService {

	private final S3StorageService s3StorageService;
	private final ReservationDateService reservationDateService;

	private final FishRepository fishRepository;
	private final ShipRepository shipRepository;
	private final ReviewRepository reviewRepository;
	private final StorageRepository storageRepository;
	private final ReservationRepository reservationRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final ReservationDateRepository reservationDateRepository;

	@Override
	@Transactional
	public Long createShipFishingPost(final ShipFishingPostRequest.Create requestDto, final Long memberId) {

		ShipFishingPost shipFishingPost = ShipFishingPostConverter.fromShipFishingPostRequestCreate(requestDto,
			memberId);

		// Verify : 선박 등록 여부 & 선박 소유자 정보 일치 & 승선 최대 인원 수 검증
		verifyShipOwnership(shipFishingPost.getShipId(), memberId, requestDto.maxGuestCount());

		// Verify : 물고기 검증
		verifyFishList(shipFishingPost.getFishIdList());

		Long savedShipFishingPostId = shipFishingPostRepository.save(shipFishingPost).getShipFishingPostId();

		saveUnAvailableDateList(requestDto, savedShipFishingPostId);

		log.debug("Save ship fish posts: {}", shipFishingPost.toString());

		return savedShipFishingPostId;
	}

	@Override
	public List<ShipFishingPostResponse.MyPagePostList> getMyPageShipFishingPostList(final Long memberId) {

		return shipFishingPostRepository.findMyPagePostList(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public ShipFishingPostResponse.DetailWithFileUrlAndFishName getShipFishingPostAll(final Long shipFishingPostId) {

		ShipFishingPostResponse.DetailAll detailAll = shipFishingPostRepository.findDetailAllById(shipFishingPostId)
			.orElseThrow(() -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND));

		List<String> fileUrlList = getFileUrlList(detailAll.detailShipFishingPost().fileIdList());

		List<FishResponse.Summary> fishInfoList = getFishNameList(detailAll.detailShipFishingPost().fishIdList());

		return ShipFishingPostConverter.fromDetailWithFileUrlAndFishName(detailAll, fileUrlList, fishInfoList);
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<ShipFishingPostResponse.DetailScroll> getShipFishingPostScroll(
		final ShipFishingPostRequest.Search searchDto,
		final GlobalRequest.CursorRequest cursorRequestDto) {

		return shipFishingPostRepository.findDetailScrollBySearch(searchDto, cursorRequestDto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ShipFishingPostResponse.MainPageHotPost> getMainPageHotShipFishingPostList(final Integer size) {

		return shipFishingPostRepository.findMainPageHotPostWithSize(size);
	}

	@Override
	@Transactional
	public Long updateShipFishingPost(final Long shipFishingPostId, final ShipFishingPostRequest.Update requestDto,
		final Long memberId) {

		ShipFishingPost shipFishingPost = getShipFishingPostEntity(shipFishingPostId);

		verifyPostOwnership(shipFishingPost.getMemberId(), memberId);

		updateFileIdList(shipFishingPost.getFileIdList(), requestDto.fileIdList(), memberId);

		verifyShipOwnership(shipFishingPost.getShipId(), memberId, requestDto.maxGuestCount());

		updateReservationDate(shipFishingPostId, requestDto.maxGuestCount(), shipFishingPost.getMaxGuestCount());

		shipFishingPost.updateShipFishingPost(
			requestDto.subject(),
			requestDto.content(),
			requestDto.price(),
			requestDto.startTime(),
			requestDto.endTime(),
			requestDto.maxGuestCount(),
			requestDto.fileIdList(),
			requestDto.fishIdList()
		);

		shipFishingPost.setDurationTime();

		return shipFishingPost.getShipFishingPostId();
	}

	@Override
	@Transactional
	public void deleteShipFishingPost(final Long shipFishingPostId, final Long memberId) {

		ShipFishingPost shipFishingPost = getShipFishingPostEntity(shipFishingPostId);

		verifyPostOwnership(shipFishingPost.getMemberId(), memberId);

		verifyReservationExist(shipFishingPostId);

		shipFishingPostRepository.deleteById(shipFishingPostId);

		reservationDateService.deleteReservationDateList(shipFishingPostId);

		s3StorageService.deleteFilesByIdList(memberId, shipFishingPost.getFileIdList());

		reviewRepository.deleteAllByShipFishingPostId(shipFishingPostId);
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
	 * 이미지 파일 id 리스트로 해당 이미지 URL 목록을 조회합니다.
	 *
	 * @param fileIdList 이미지 파일 id 리스트
	 * @return 이미지 Url 리스트
	 */
	private List<String> getFileUrlList(final List<Long> fileIdList) {
		return storageRepository.findAllById(fileIdList).stream()
			.map(File::getUrl)
			.toList();
	}

	/**
	 * 어류 id 리스트로 해당 어류 Name 목록을 조회합니다.
	 *
	 * @param fishIdList 어류 id 리스트
	 * @return 어류 Name 리스트
	 */
	private List<FishResponse.Summary> getFishNameList(final List<Long> fishIdList) {
		return fishRepository.findFishSummaryById(fishIdList);
	}

	/**
	 * shipId 등록 여부 & 선박 소유자 정보 일치 검증 메서드
	 *
	 * @param shipId {@link Long}
	 * @param memberId {@link Long}
	 * @param maxGuestCount {@link Integer}
	 */
	private void verifyShipOwnership(final Long shipId, final Long memberId, final int maxGuestCount) {

		Ship ship = shipRepository.findById(shipId)
			.orElseThrow(() -> new ShipException(ShipErrorCode.SHIP_NOT_FOUND));

		if (!ship.getMemberId().equals(memberId)) {
			throw new ShipException(ShipErrorCode.SHIP_MISMATCH_MEMBER_ID);
		}

		if (ship.getPassengerCapacity() < maxGuestCount) {
			throw new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_CAPACITY_EXCEEDED);
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

	/**
	 * 저장하려는 물고기 정보가 있는지 검증 메서드
	 *
	 * @param fishList {@link List}
	 */
	private void verifyFishList(final List<Long> fishList) {

		List<Fish> findFishList = fishRepository.findAllById(fishList);
		log.debug("{}", findFishList.size());

		log.debug("{}", findFishList);

		if (findFishList.size() != fishList.size()) {
			throw new FishException(FishErrorCode.FISH_NOT_FOUND);
		}
	}

	/**
	 * 삭제할 게시글의 남은 예약 내역 검증
	 *
	 * @param shipFishingPostId {@link Long}
	 */
	private void verifyReservationExist(final Long shipFishingPostId) {

		Boolean reservationExists = reservationRepository
			.findByShipFishingPostIdAndTodayAfter(shipFishingPostId, LocalDate.now());

		if (reservationExists) {
			throw new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_RESERVATION_EXIST);
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
	 * 게시글 정원 변경으로 인한 예약 일자 정원 업데이트 메서드
	 *
	 * @param shipFishingPostId 게시글 id
	 * @param updateGuestCount 업데이트한 정원
	 * @param oldGuestCount 기존 정원
	 */
	private void updateReservationDate(
		final Long shipFishingPostId,
		final Integer updateGuestCount,
		final Integer oldGuestCount) {

		int updateCount = updateGuestCount - oldGuestCount;

		if (updateCount > 0) {
			reservationDateRepository.updateRemainCountWithPlus(shipFishingPostId, updateCount, LocalDate.now());
		} else if (updateCount < 0) {
			reservationDateRepository.updateRemainCountWithMinus(shipFishingPostId, updateCount, LocalDate.now());
		}
	}

	/**
	 * 이미지 업데이트 메서드
	 *
	 * @param updateFileIdList 업데이트 된 이미지 목록
	 * @param oldFileIdList 기존 게시글 이미지 목록
	 * @param memberId 유저 고유 id
	 */
	private void updateFileIdList(
		final List<Long> updateFileIdList,
		final List<Long> oldFileIdList,
		final Long memberId) {

		List<Long> deleteFileIdList = oldFileIdList.stream()
			.filter(id -> !updateFileIdList.contains(id))
			.toList();

		if (!deleteFileIdList.isEmpty()) {
			s3StorageService.deleteFilesByIdList(memberId, deleteFileIdList);
		}
	}
}