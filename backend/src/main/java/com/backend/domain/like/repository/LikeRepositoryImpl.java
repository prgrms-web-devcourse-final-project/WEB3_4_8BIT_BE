package com.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.entity.Like;
import com.backend.global.dto.request.GlobalRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

	private final LikeJpaRepository likeJpaRepository;
	private final LikeQueryRepository likeQueryRepository;

	@Override
	public Like save(final Like like) {
		return likeJpaRepository.save(like);
	}

	@Override
	public boolean existsByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	) {
		return likeJpaRepository.existsByMemberIdAndTargetTypeAndTargetId(memberId, targetType, targetId);
	}

	@Override
	public Optional<Like> findByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	) {
		return likeJpaRepository.findByMemberIdAndTargetTypeAndTargetId(memberId, targetType, targetId);
	}

	@Override
	public Long countByTargetTypeAndTargetId(
		final LikeTargetType type,
		final Long targetId
	) {
		return likeQueryRepository.countByTargetTypeAndTargetId(type, targetId);
	}

	@Override
	public void deleteByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	) {
		likeQueryRepository.deleteByMemberIdAndTargetTypeAndTargetId(memberId, targetType, targetId);
	}

	@Override
	public void restoreByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	) {
		likeQueryRepository.restoreByMemberIdAndTargetTypeAndTargetId(memberId, targetType, targetId);
	}

	@Override
	public int deleteAllSoftDeletedLikes() {
		return likeQueryRepository.deleteAllSoftDeletedLikes();
	}

	@Override
	public List<LikeResponse.FishingTripPostLikedQueryDto> getFishingTripPostLikedDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	) {
		return likeQueryRepository.getLikedFishingTripPosts(cursorRequestDto, memberId);
	}

	@Override
	public List<LikeResponse.ShipFishingPostLikedQueryDto> getShipFishingPostLikedDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	) {
		return likeQueryRepository.getLikedShipFishingPosts(cursorRequestDto, memberId);
	}
}
