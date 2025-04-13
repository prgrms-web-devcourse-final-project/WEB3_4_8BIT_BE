package com.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.entity.Like;
import com.backend.global.dto.request.GlobalRequest;

public interface LikeRepository {

	/**
	 * 좋아요 저장
	 *
	 * @param like 저장할 Like 엔티티
	 * @return 저장된 Like
	 */
	Like save(final Like like);

	/**
	 * 특정 대상의 총 좋아요 수 조회
	 *
	 * @param type     좋아요 대상 타입
	 * @param targetId 대상 ID
	 * @return 좋아요 수
	 */
	Long countByTargetTypeAndTargetId(
		final LikeTargetType type,
		final Long targetId
	);

	/**
	 * 특정 대상에 대해 멤버가 좋아요를 눌렀는지 여부 확인
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 * @return 좋아요 여부 (soft delete 제외)
	 */
	boolean existsByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 특정 좋아요 단건 조회
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 * @return Optional<Like>
	 */
	Optional<Like> findByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 좋아요 soft delete 처리
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 */
	void deleteByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * soft delete 된 좋아요 복원 처리
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 */
	void restoreByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * soft delete 된 좋아요 전체 영구 삭제
	 *
	 * @return 삭제된 수
	 */
	int deleteAllSoftDeletedLikes();

	/**
	 * 특정 게시글에 대한 모든 좋아요 삭제 (하드 딜리트)
	 *
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 */
	void deleteLikesByTargetTypeAndTargetId(
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 낚시 동행 좋아요 게시글 목록 조회 (커서 기반 페이징)
	 *
	 * @param cursorRequestDto 커서 요청
	 * @param memberId         멤버 ID
	 * @return 좋아요한 낚시 동행 게시글 목록
	 */
	List<LikeResponse.FishingTripPostLikedQueryDto> getFishingTripPostLikedDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	);

	/**
	 * 선상 낚시 좋아요 게시글 목록 조회 (커서 기반 페이징)
	 *
	 * @param cursorRequestDto 커서 요청
	 * @param memberId         멤버 ID
	 * @return 좋아요한 선상 낚시 게시글 목록
	 */
	List<LikeResponse.ShipFishingPostLikedQueryDto> getShipFishingPostLikedDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	);

	/**
	 * 특정 멤버가 좋아요한 게시글 개수 조회 (게시글 타입별)
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 게시글 타입
	 * @return 좋아요 수
	 */
	Long countLikedPostsByType(final Long memberId, final LikeTargetType targetType);

	/**
	 * 특정 멤버가 좋아요한 게시글 ID 목록 조회 (다중 대상)
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetIds  대상 ID 목록
	 * @return 좋아요 누른 게시글 ID 목록
	 */
	List<Long> findLikedTargetIdsByMemberIdAndTargetType(
		final Long memberId,
		final LikeTargetType targetType,
		final List<Long> targetIds
	);
}
