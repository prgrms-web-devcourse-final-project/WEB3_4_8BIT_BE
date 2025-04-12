package com.backend.domain.like.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fish.repository.FishRepository;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.like.converter.LikeConverter;
import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostErrorCode;
import com.backend.domain.shipfishingpost.exception.ShipFishingPostException;
import com.backend.domain.shipfishingpost.repository.ShipFishingPostRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;
import com.backend.global.storage.entity.File;
import com.backend.global.storage.repository.StorageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

	private final LikeRepository likeRepository;
	private final FishRepository fishRepository;
	private final ShipFishingPostRepository shipFishingPostRepository;
	private final FishingTripPostRepository fishingTripPostRepository;
	private final StorageRepository storageRepository;
	private final LikeCacheService likeCacheService;

	/**
	 * 좋아요 토글 메서드
	 * - 이미 좋아요 되어 있다면 취소
	 * - 소프트 딜리트 상태였다면 복구
	 * - 없으면 새로 생성
	 *
	 * @param memberId   요청한 회원 ID
	 * @param requestDto 좋아요 대상 정보
	 */
	@Override
	@Transactional
	public void toggleLike(final Long memberId, final LikeRequest requestDto) {
		//게시글 존재 검증
		validateLikeTarget(requestDto.targetType(), requestDto.targetId());
		// 캐시가 없다면 DB 데이터로 초기화
		likeCacheService.initializeLikeCache(requestDto.targetType(), requestDto.targetId());

		likeRepository.findByMemberIdAndTargetTypeAndTargetId(
			memberId,
			requestDto.targetType(),
			requestDto.targetId()
		).ifPresentOrElse(
			existingLike -> {
				if (existingLike.isActive()) {
					log.debug("[좋아요 취소] memberId: {}, request: {}", memberId, requestDto);
					softDeleteLike(memberId, requestDto);
				} else {
					log.debug("[좋아요 복구] memberId: {}, request: {}", memberId, requestDto);
					restoreDeletedLike(memberId, requestDto);
				}
			},
			() -> {
				log.debug("[좋아요 생성] memberId: {}, request: {}", memberId, requestDto);
				createNewLike(memberId, requestDto);
			}
		);
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<LikeResponse.FishingTripPostLikedDetailResponse> getLikedFishingTripPosts(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	) {
		List<LikeResponse.FishingTripPostLikedQueryDto> detailPageDto = likeRepository.getFishingTripPostLikedDetailPage(
			cursorRequestDto,
			memberId
		);

		boolean isLast = detailPageDto.size() <= cursorRequestDto.size();

		if (!isLast) {
			detailPageDto.remove(detailPageDto.size() - 1);
		}

		List<LikeResponse.FishingTripPostLikedDetailResponse> result = detailPageDto.stream()
			.map(dto -> LikeConverter.toFishingTripPostDetailPage(dto, this::getImageUrlById))
			.toList();

		return ScrollResponse.from(
			result,
			cursorRequestDto.size(),
			result.size(),
			cursorRequestDto.fieldValue() == null,
			isLast
		);
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<LikeResponse.ShipFishingPostLikedDetailResponse> getLikedShipFishingPosts(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	) {
		List<LikeResponse.ShipFishingPostLikedQueryDto> detailPageDto = likeRepository.getShipFishingPostLikedDetailPage(
			cursorRequestDto,
			memberId
		);

		boolean isLast = detailPageDto.size() <= cursorRequestDto.size();

		if (!isLast) {
			detailPageDto.remove(detailPageDto.size() - 1);
		}

		List<LikeResponse.ShipFishingPostLikedDetailResponse> result = detailPageDto.stream()
			.map(dto -> {
				List<String> fishNames = fishRepository.findFishNameListByIdList(dto.fishIdList());
				return LikeConverter.toShipFishingPostDetailPage(dto, fishNames, this::getImageUrlById);
			})
			.toList();

		return ScrollResponse.from(
			result,
			cursorRequestDto.size(),
			result.size(),
			cursorRequestDto.fieldValue() == null,
			isLast
		);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getCountLikedShipFishingPosts(final Long memberId, final LikeTargetType targetType) {
		return likeRepository.countLikedPostsByType(memberId, targetType);
	}

	/**
	 * 좋아요 취소 처리
	 *
	 * @param memberId   사용자 ID
	 * @param requestDto 좋아요 요청 정보
	 */
	private void softDeleteLike(final Long memberId, final LikeRequest requestDto) {
		likeRepository.deleteByMemberIdAndTargetTypeAndTargetId(
			memberId,
			requestDto.targetType(),
			requestDto.targetId()
		);

		likeCacheService.updateLikeCountCache(
			requestDto.targetType(), requestDto.targetId(), false
		);
	}

	/**
	 * 기존 soft delete 상태의 좋아요를 복구 처리
	 *
	 * @param memberId   사용자 ID
	 * @param requestDto 좋아요 요청 정보
	 */
	private void restoreDeletedLike(final Long memberId, final LikeRequest requestDto) {
		likeRepository.restoreByMemberIdAndTargetTypeAndTargetId(
			memberId,
			requestDto.targetType(),
			requestDto.targetId()
		);

		likeCacheService.updateLikeCountCache(
			requestDto.targetType(),
			requestDto.targetId(),
			true
		);
	}

	/**
	 * 좋아요 이력이 없는 경우 새로 좋아요 생성
	 *
	 * @param memberId   사용자 ID
	 * @param requestDto 좋아요 요청 정보
	 */
	private void createNewLike(final Long memberId, final LikeRequest requestDto) {
		likeRepository.save(LikeConverter.fromMemberAndLikeRequestCreate(memberId, requestDto));

		likeCacheService.updateLikeCountCache(
			requestDto.targetType(),
			requestDto.targetId(),
			true
		);
	}

	/**
	 * 파일 ID를 통해 해당 파일의 이미지 URL을 조회합니다.
	 *
	 * <p> 파일을 조회하고, 존재할 경우 해당 파일의 URL을 반환합니다.
	 * 파일이 존재하지 않으면 {@code null}을 반환합니다.</p>
	 *
	 * @param fileId 조회할 파일의 ID
	 * @return 파일이 존재하면 해당 파일의 URL, 존재하지 않으면 {@code null}
	 */
	private String getImageUrlById(final Long fileId) {
		return storageRepository.findById(fileId)
			.map(File::getUrl)
			.orElse(null);
	}

	/**
	 * Like 대상 존재 여부 검증
	 *
	 * @param targetType 좋아요 대상 타입
	 * @param targetId   좋아요 대상 ID
	 * @throws ShipFishingPostException 존재하지 않는 선상 낚시 게시글일 경우 예외 발생
	 * @throws FishingTripPostException 존재하지 않는 동출 모집 게시글일 경우 예외 발생
	 */
	private void validateLikeTarget(final LikeTargetType targetType, final Long targetId) {

		boolean exists = switch (targetType) {
			case SHIP_FISHING_POST -> shipFishingPostRepository.existsById(targetId);
			case FISHING_TRIP_POST -> fishingTripPostRepository.existsById(targetId);
		};

		validTargetTypeAndTargetId(targetType, targetId, exists);
	}

	/**
	 * @param targetType 좋아요 대상 타입
	 * @param targetId   좋아요 대상 ID
	 * @param exists     게시글 존재 유무
	 * @throws ShipFishingPostException 존재하지 않는 선상 낚시 게시글일 경우 예외 발생
	 * @throws FishingTripPostException 존재하지 않는 동출 모집 게시글일 경우 예외 발생
	 */

	private void validTargetTypeAndTargetId(
		final LikeTargetType targetType,
		final Long targetId,
		final boolean exists
	) {
		if (!exists) {
			log.warn("[존재하지 게시글] 타입: {}, ID: {}", targetType, targetId);
			throw switch (targetType) {
				case SHIP_FISHING_POST -> new ShipFishingPostException(ShipFishingPostErrorCode.POSTS_NOT_FOUND);
				case FISHING_TRIP_POST ->
					new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND);
			};
		}
	}
}
