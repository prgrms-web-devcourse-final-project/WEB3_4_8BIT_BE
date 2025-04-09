package com.backend.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.entity.Like;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

	Optional<Like> findByMemberIdAndTargetTypeAndTargetId(Long memberId, LikeTargetType targetType, Long targetId);

	boolean existsByMemberIdAndTargetTypeAndTargetId(Long memberId, LikeTargetType targetType, Long targetId);
}
