package com.backend.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.entity.Like;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

	Optional<Like> findByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	boolean existsByMemberIdAndTargetTypeAndTargetIdAndIsDeletedFalse(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	void deleteLikesByTargetTypeAndTargetId(
		final LikeTargetType targetType,
		final Long targetId
	);
}
