package com.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import com.backend.domain.like.domain.LikeTargetType;
import com.backend.domain.like.dto.response.LikeResponse;
import com.backend.domain.like.entity.Like;
import com.backend.global.dto.request.GlobalRequest;

public interface LikeRepository {

	/**
	 * 좋아요 저장 메서드
	 *
	 * @param like 저장할 좋아요 엔티티
	 * @return 저장된 Like 엔티티
	 * @implSpec 새로운 좋아요를 저장한다.
	 */
	Like save(final Like like);

	/**
	 * 특정 게시글의 총 좋아요 수 조회
	 *
	 * @param type     대상 타입
	 * @param targetId 대상 ID
	 * @return 좋아요 수
	 * @implSpec 대상 타입과 ID 기준으로 좋아요 수 카운트
	 */
	Long countByTargetTypeAndTargetId(
		final LikeTargetType type,
		final Long targetId
	);

	/**
	 * 특정 대상에 대해 좋아요한 여부 확인
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 좋아요 대상 타입
	 * @param targetId   좋아요 대상 ID
	 * @return true/false
	 * @implSpec 이미 좋아요한 경우 true 반환
	 */

	boolean existsByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 좋아요 단건 조회 메서드
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 좋아요 대상 타입
	 * @param targetId   좋아요 대상 ID
	 * @return Optional<Like> 좋아요 정보
	 * @implSpec 회원 ID, 대상 타입, 대상 ID로 좋아요 단건 조회
	 */
	Optional<Like> findByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 좋아요 소프트 삭제 처리
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 * @implSpec 삭제 처리 시 isDeleted = true로 수정
	 */
	void deleteByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 소프트 삭제된 좋아요 복구 처리
	 *
	 * @param memberId   멤버 ID
	 * @param targetType 대상 타입
	 * @param targetId   대상 ID
	 * @implSpec 복구 시 isDeleted = false로 수정
	 */
	void restoreByMemberIdAndTargetTypeAndTargetId(
		final Long memberId,
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * isDeleted = true인 좋아요를 영구 삭제
	 *
	 * @return 삭제된 좋아요 개수
	 * @implSpec 소프트 삭제된 좋아요를 DB에서 완전히 제거
	 */
	int deleteAllSoftDeletedLikes();

	/**
	 * 게시글의 좋아요를 전체 영구 삭제
	 *
	 * @implSpec 하드 딜리트 메서드
	 */
	void deleteLikesByTargetTypeAndTargetId(
		final LikeTargetType targetType,
		final Long targetId
	);

	/**
	 * 로그인한 사용자가 좋아요를 누른 낚시 동행 게시글 목록을 커서 기반으로 조회합니다.
	 *
	 * <p>정렬 기준은 좋아요를 누른 시점(like.createdAt)이며, ID(likeId)를 보조 커서로 사용합니다.
	 * 커서 기반 페이징을 통해 최신순으로 정렬된 좋아요 게시글을 효율적으로 조회할 수 있습니다.</p>
	 *
	 * <p>응답은 {@link LikeResponse.FishingTripPostLikedQueryDto} 형태로,
	 * 게시글의 요약 정보 및 대표 이미지 ID 리스트를 포함합니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보 (정렬 방향, 커서 값 등)
	 * @param memberId         로그인한 사용자 ID
	 * @return 커서 기반 페이징된 좋아요 게시글 리스트
	 */
	List<LikeResponse.FishingTripPostLikedQueryDto> getFishingTripPostLikedDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	);

	/**
	 * 로그인한 사용자가 좋아요를 누른 선상 낚시 게시글 목록을 커서 기반으로 조회합니다.
	 *
	 * <p>정렬 기준은 좋아요를 누른 시점(like.createdAt)이며, ID(likeId)를 보조 커서로 사용합니다.
	 * 커서 기반 페이징을 통해 최신순으로 정렬된 좋아요 게시글을 효율적으로 조회할 수 있습니다.</p>
	 *
	 * <p>응답은 {@link LikeResponse.FishingTripPostLikedQueryDto} 형태로,
	 * 게시글의 요약 정보 및 대표 이미지 ID 리스트를 포함합니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보 (정렬 방향, 커서 값 등)
	 * @param memberId         로그인한 사용자 ID
	 * @return 커서 기반 페이징된 좋아요 게시글 리스트
	 */
	List<LikeResponse.ShipFishingPostLikedQueryDto> getShipFishingPostLikedDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId
	);

}
