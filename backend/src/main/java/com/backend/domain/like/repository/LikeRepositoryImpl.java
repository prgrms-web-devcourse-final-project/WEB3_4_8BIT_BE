package com.backend.domain.like.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.entity.Like;

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
}
