package com.backend.domain.fishingtrippost.repository;

import java.util.List;
import java.util.Optional;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface FishingTripPostRepository {

	/**
	 * 동출 모집 게시글 저장 메서드
	 *
	 * @param fishingTripPost {@link FishingTripPost}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPost 받아서 저장 후 저장된 엔티티 반환
	 */

	FishingTripPost save(final FishingTripPost fishingTripPost);

	/**
	 * 동출 모집 게시글 조회 메서드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @return {@link FishingTripPost}
	 * @implSpec FishingTripPostId 받아서 조회된 동출 모집 게시글 엔티티 반환
	 */
	Optional<FishingTripPost> findById(final Long fishingTripPostId);

	/**
	 * 주어진 게시글 ID를 기반으로 동출 게시글의 상세 정보를 조회합니다.
	 *
	 * <p>이 메서드는 게시글 작성자, 제목, 내용, 모집 정보, 출조 날짜, 위치 정보, 이미지 파일 ID 목록을 포함한
	 * {@link com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse.DetailQueryDto}를 반환합니다.</p>
	 *
	 * <p>이 DTO는 최종 응답 DTO {@link com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse.Detail}
	 * 를 생성하기 위한 중간 단계로 사용됩니다. 이미지 파일 ID를 통해 실제 URL을 조회하는 로직은 서비스 계층에서 처리됩니다.</p>
	 *
	 * @param fishingTripPostId 조회할 동출 게시글의 고유 ID
	 * @return 게시글 상세 정보를 담은 DTO를 {@link Optional}로 감싼 값. 존재하지 않으면 {@link Optional#empty()}
	 * @implSpec 이 메서드는 QueryDSL을 기반으로 동출 게시글, 작성자(member), 낚시 포인트(fishPoint) 정보를 조인하여
	 * 단일 쿼리로 가져옵니다. 결과가 없을 경우 {@link Optional#empty()}를 반환합니다.
	 * <p>
	 * 구현 시 성능과 확장성을 고려하여 필요한 필드만 조회하도록 주의해야 합니다.
	 */
	Optional<FishingTripPostResponse.DetailQueryDto> findDetailQueryDtoById(final Long fishingTripPostId);

	/**
	 * 동출 모집 게시글 존재 여부 조회 메소드
	 *
	 * @param fishingTripPostId {@link Long}
	 * @return {@link Boolean} 데이터가 있다면 true, 없으면 false
	 * @implSpec fishingTripPostId 데이터가 있는지 확인 후 결과 반한
	 */
	boolean existsById(final Long fishingTripPostId);

	/**
	 * 커서 기반으로 낚시 동행 게시글 목록을 조회합니다.
	 *
	 * <p>정렬 기준 및 방향, 커서 값에 따라 페이징된 게시글 목록을 반환하며,
	 * 게시글 상태, 지역, 제목 키워드로 필터링이 가능합니다.</p>
	 *
	 * <p>이 메서드는 파일 ID 리스트를 포함한 DTO 형태로 반환되며,
	 * 이후 서비스 계층에서 이미지 URL 매핑 등의 추가 작업이 수행됩니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보 (정렬 기준, 방향, 커서 값 등)
	 * @param status           게시글 상태 필터 (예: RECRUITING, COMPLETED), null일 경우 전체
	 * @param regionId         지역 ID 필터, null일 경우 전체
	 * @param keyword          제목 키워드 검색 필터, null 또는 빈 값일 경우 전체
	 * @return 페이징 처리된 {@link FishingTripPostResponse.DetailPageQueryDto} 목록
	 */
	List<FishingTripPostResponse.DetailPageQueryDto> findScrollDetailPageDto(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus status,
		final Long regionId,
		final String keyword
	);

	/**
	 * 주어진 게시글 ID와 사용자 ID를 기준으로 낚시 동행 게시글의 참여 관련 상세 정보를 조회합니다.
	 *
	 * <p>이 메서드는 게시글의 모집 현황, 작성자 정보, 로그인한 사용자가
	 * 참여자인지 또는 작성자인지를 포함하는 정보를 반환합니다.</p>
	 *
	 * <p>로그인하지 않은 사용자인 경우 {@code memberId}는 {@code null}로 전달되며,
	 * 이 경우 isApplicant, isWriter는 {@code false}로 처리됩니다.</p>
	 *
	 * @param fishingTripPostId 참여 정보를 조회할 게시글 ID (필수)
	 * @param memberId 로그인한 사용자 ID (nullable)
	 * @return 게시글 참여 관련 상세 정보 DTO
	 */
	FishingTripPostResponse.ParticipantDetailDto findParticipantDetailDto(
		final Long fishingTripPostId,
		final Long memberId
	);

	/**
	 * 특정 동출 모집 게시글에 승인된 참여자 목록을 조회합니다.
	 *
	 * <p>이 메서드는 모집 상태가 {@link RecruitmentStatus#APPROVED}인 참여자만 조회하며,
	 * 참여자의 ID, 닉네임, 프로필 이미지 URL 정보를 포함합니다.</p>
	 *
	 * @param fishingTripPostId 참여자 목록을 조회할 대상 게시글 ID
	 * @return 승인된 참여자들의 기본 정보 리스트
	 */
	List<FishingTripPostResponse.ParticipantDetail> findApprovedParticipants(
		final Long fishingTripPostId
	);

	/**
	 * 동출 모집 게시글 좋아요 수 업데이트 메서드
	 *
	 * @param fishingTripPostId 동출 모집 게시글 ID
	 * @param likeCount         업데이트할 좋아요 수
	 * @implSpec 해당 게시글의 좋아요 수를 갱신합니다.
	 */
	boolean updateLikeCount(final Long fishingTripPostId, final Long likeCount);

	/**
	 * 동출 게시글을 삭제하는 메서드입니다.
	 * <p>해당 게시글 객체를 인자로 받아 삭제를 수행합니다.</p>
	 *
	 * @param fishingTripPost 삭제할 동출 게시글 엔티티
	 */
	void delete(final FishingTripPost fishingTripPost);

	/**
	 * 내가 신청한 동출 게시글을 커서 기반으로 조회하는 메서드입니다.
	 * <p>
	 * 동출 모집 상태(PostStatus)가 주어진 조건과 일치하는 게시글만 조회되며,
	 * 커서 기반 페이징을 통해 최신순 정렬로 데이터를 반환합니다.
	 * </p>
	 *
	 * @param cursorRequestDto 커서 페이징 요청 객체 (size, 방향, 기준 필드 등 포함)
	 * @param postStatus 게시글 상태 필터 (RECRUITING or COMPLETED)
	 * @param memberId 현재 로그인한 사용자의 ID (신청자 기준)
	 * @return ScrollResponse 형태로 페이징 처리된 내가 신청한 게시글 목록 반환
	 */
	ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> findMyFishingTripRecruitmentDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus postStatus,
		final Long memberId
	);

	/**
	 * 내가 작성한 동출 게시글 목록을 커서 기반으로 조회하는 메서드입니다.
	 *
	 * <p>
	 * 동출 모집 상태(PostStatus)가 주어진 조건과 일치하는 게시글만 조회되며,
	 * 커서 기반 페이징을 통해 최신순 정렬로 데이터를 반환합니다.
	 * </p>
	 *
	 * @param cursorRequestDto 커서 기반 페이징 요청 정보 (정렬 기준, 방향, 기준 값 등)
	 * @param postStatus 조회할 게시글의 상태 (예: RECRUITING, COMPLETED 등)
	 * @param memberId 현재 로그인한 회원의 ID
	 * @return 내가 작성한 동출 게시글 목록의 스크롤 응답 (ScrollResponse)
	 */
	ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> findMyPostFishingTripPostDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus postStatus,
		final Long memberId
	);

	List<FishingTripPostResponse.HotPostDto> findHotPostDto();
}
