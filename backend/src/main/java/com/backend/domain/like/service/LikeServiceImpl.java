package com.backend.domain.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.like.converter.LikeConverter;
import com.backend.domain.like.dto.request.LikeRequest;
import com.backend.domain.like.repository.LikeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

	private final LikeRepository likeRepository;
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
}
