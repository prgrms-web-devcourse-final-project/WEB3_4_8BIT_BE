package com.backend.domain.fishingtrippost.service;

import java.util.List;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.dto.response.FishingTripPostResponse;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.notifier.FishingTripPostNotifier;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.like.repository.LikeRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface FishingTripPostService {

	/**
	 * 동출 게시글 저장 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.create}
	 * @return {@link Long fishingTripPostId} Long: 동출 게시글 Id fishingTripPostId
	 * @implSpec 로그인한 멤버 Id와 동출 게시글 작성에 필요한 정보를 받아 게시글 작성
	 */
	Long createFishingTripPost(
		final Long memberId,
		final FishingTripPostRequest.create requestDto
	);

	/**
	 * 동출 게시글 수정 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.update}
	 * @return {@link Long fishingTripPostId} Long: 동출 게시글 Id fishingTripPostId
	 * @implSpec 로그인한 멤버 Id와 동출 게시글 수정에 필요한 정보를 받아 게시글 수정
	 */
	Long updateFishingTripPost(
		final Long memberId,
		final Long fishingTripPostId,
		final FishingTripPostRequest.update requestDto
	);

	/**
	 * 주어진 게시글 ID를 기반으로 동출 모집 게시글의 상세 정보를 조회합니다.
	 *
	 * <p>이 메서드는 게시글의 기본 정보, 작성자 이름, 낚시 포인트 정보, 이미지 URL 목록, 좋아요 수,
	 * 현재 로그인한 사용자의 좋아요 여부를 포함한 {@link FishingTripPostResponse.Detail} DTO를 반환합니다.</p>
	 *
	 * <p>내부적으로는 먼저 {@link FishingTripPostResponse.DetailQueryDto}를 조회한 뒤,
	 * 해당 DTO의 파일 ID 목록을 통해 이미지 URL 리스트를 조회하고, 좋아요 정보를 함께 조합하여
	 * 최종 응답 DTO를 생성합니다.</p>
	 *
	 * @implSpec
	 * 이 구현은 다음 순서로 동작합니다:
	 * <ol>
	 *   <li>{@link FishingTripPostRepository}를 통해 게시글 ID에 해당하는 {@link FishingTripPostResponse.DetailQueryDto}를 조회합니다.</li>
	 *   <li>조회된 DTO가 존재하지 않으면 {@link FishingTripPostException}을 발생시킵니다.</li>
	 *   <li>DTO의 fileIdList를 기반으로 {@link com.backend.global.storage.repository.StorageRepository}에서 이미지 URL들을 조회합니다.</li>
	 *   <li>{@link LikeRepository}를 통해 좋아요 수와 로그인 사용자의 좋아요 여부를 조회합니다.</li>
	 *   <li>이 정보들을 바탕으로 최종 {@link FishingTripPostResponse.Detail} 객체를 생성하여 반환합니다.</li>
	 * </ol>
	 *
	 * @param memberId 로그인한 사용자 ID (비로그인 사용자의 경우 null)
	 * @param fishingTripPostId 조회할 게시글의 고유 ID
	 * @return 게시글 상세 정보가 담긴 {@link FishingTripPostResponse.Detail}
	 * @throws FishingTripPostException 게시글이 존재하지 않는 경우 발생
	 */
	FishingTripPostResponse.Detail getFishingTripPostDetail(final Long memberId,final Long fishingTripPostId);

	/**
	 * 낚시 동행 모집 게시글을 모집 완료 상태로 변경하고, 신청자들에게 완료 안내 메일을 발송합니다.
	 *
	 * <p>게시글 작성자(memberId)가 요청한 게시글(fishingTripPostId)을 조회한 뒤,
	 * 작성자인지 검증하고 상태를 {@link PostStatus#COMPLETED}로 변경합니다.
	 * 이후, {@link FishingTripPostNotifier#notifyMailIfCompleted(FishingTripPost)}
	 * 를 호출하여 신청자들에게 모집 완료 메일을 비동기적으로 전송합니다.</p>
	 *
	 * @param memberId 게시글을 모집 완료로 변경하려는 사용자 ID (작성자 본인이어야 함)
	 * @param fishingTripPostId 모집 완료로 변경할 게시글 ID
	 */
	void completeFishingTripPost(final Long memberId, final Long fishingTripPostId);

	/**
	 * 커서 기반으로 낚시 동행 게시글 목록을 조회합니다.
	 *
	 * <p>게시글의 생성일(createdAt)과 ID를 커서 기준으로 하여 페이징 처리된 목록을 반환합니다.
	 * 상태(postStatus), 지역(regionId), 키워드(subject) 필터링이 가능하며,
	 * 각 게시글에 대해 대표 이미지 URL이 포함된 {@link FishingTripPostResponse.DetailPage}로 매핑됩니다.</p>
	 *
	 * <p>이미지 URL은 파일 ID 리스트에서 첫 번째 ID를 기준으로 조회됩니다.
	 * 존재하지 않을 경우 null이 할당됩니다.</p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보 (정렬 기준, 방향, 커서 값 등)
	 * @param status 게시글 상태 필터 (예: RECRUITING, COMPLETED), null일 경우 전체 조회
	 * @param regionId 지역 ID 필터, null일 경우 전체 조회
	 * @param keyword 제목 키워드 필터 (부분 일치 검색), null 또는 빈 값일 경우 전체 조회
	 * @return 커서 기반 페이징된 {@link ScrollResponse} 객체로, 게시글 요약 정보 리스트를 포함합니다
	 */
	ScrollResponse<FishingTripPostResponse.DetailPage> getDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final PostStatus status,
		final Long regionId,
		final String keyword
	);

	/**
	 * 낚시 동출 게시글에 대한 참여 상세 정보를 조회하는 서비스 메서드입니다.
	 *
	 * <p>요청한 게시글에 대해 아래 정보를 포함한 응답을 생성합니다:</p>
	 * <ul>
	 *     <li>게시글의 모집 인원, 현재 참여자 수, 상태</li>
	 *     <li>현재 로그인한 사용자가 참여자인지 여부</li>
	 *     <li>현재 로그인한 사용자가 게시글 작성자인지 여부</li>
	 *     <li>작성자 정보 (ID, 닉네임, 프로필 이미지)</li>
	 *     <li>참여자 목록</li>
	 * </ul>
	 *
	 * <p>로그인하지 않은 사용자가 요청한 경우에도 기본 정보 및 참여자 목록은 반환되며,
	 * `isApplicant`, `isCurrentUserOwner` 필드는 false로 처리됩니다.</p>
	 *
	 * @param memberId 로그인한 사용자 ID (비로그인 시 null)
	 * @param fishingTripPostId 상세 정보를 조회할 낚시 동출 게시글 ID
	 * @return {@link FishingTripPostResponse.FishingTripPostParticipationDetail} 참여 상세 정보 DTO
	 */
	FishingTripPostResponse.FishingTripPostParticipationDetail getFishingTripPostParticipationDetail(
		final Long memberId,
		final Long fishingTripPostId
	);

	/**
	 * 동출 게시글을 삭제하는 메서드입니다.
	 * <p>게시글 작성자인지 검증 후, 관련된 모든 동출 신청 데이터와 게시글 자체를 삭제합니다.</p>
	 *
	 * @param memberId 요청을 수행하는 로그인한 회원의 ID
	 * @param fishingTripPostId 삭제할 동출 게시글의 ID
	 * @throws FishingTripPostException 작성자가 아닌 경우 예외 발생
	 */
	void delete(final Long memberId,final Long fishingTripPostId);

	/**
	 * 내가 신청한 동출 게시글 목록을 커서 기반으로 조회하는 서비스 메서드입니다.
	 * <p>
	 * 게시글 상태(PostStatus)에 따라 '모집중' 또는 '모집완료'인 게시글만 필터링되며,
	 * 로그인한 사용자가 신청자(memberId)인 게시글에 대해서만 결과를 반환합니다.
	 * 커서 기반 페이징을 적용하여 무한 스크롤 형태로 응답합니다.
	 * </p>
	 *
	 * @param cursorRequestDto 커서 페이징 요청 객체 (정렬 기준, 방향, 커서 값 포함)
	 * @param memberId 현재 로그인한 사용자 ID (신청자 기준)
	 * @param postStatus 게시글 상태 필터 (RECRUITING | COMPLETED)
	 * @return 페이징 처리된 내가 신청한 동출 게시글 리스트 응답
	 */
	ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> getMyFishingTripPostDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId,
		final PostStatus postStatus
	);

	/**
	 * 내가 작성한 동출 모집 게시글을 커서 기반으로 조회합니다.
	 * <p>
	 * 게시글 상태(PostStatus)에 따라 '모집중' 또는 '모집완료'인 게시글만 필터링되며,
	 * 로그인한 사용자가 신청자(memberId)인 게시글에 대해서만 결과를 반환합니다.
	 * 커서 기반 페이징을 적용하여 무한 스크롤 형태로 응답합니다.
	 * </p>
	 *
	 * @param cursorRequestDto 커서 기반 페이지네이션 요청 정보 (정렬 기준, 방향, 커서 값 등)
	 * @param memberId 조회할 작성자의 회원 ID
	 * @param postStatus 조회할 게시글 상태 (예: RECRUITING, COMPLETED 등)
	 * @return 내가 작성한 동출 모집 게시글 목록을 커서 기반으로 응답하는 ScrollResponse 객체
	 */
	ScrollResponse<FishingTripPostResponse.MyFishingTripPostDetailPage> getMyPostFishingTripPostDetailPage(
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long memberId,
		final PostStatus postStatus
	);

	/**
	 * 인기 동출 모집글(HOT 게시글) 조회 메서드
	 *
	 * <p> 좋아요 수 + 댓글 수를 기준으로 HOT 게시글을 선정하고,
	 * 최근 5일 이내 게시글 중에서 상위 5개를 반환한다.
	 *
	 * <p> 성능 최적화를 위해 Redis 캐시를 사용하며, 캐시에 값이 존재하면 캐시에서 바로 반환한다.
	 *
	 * <p> 캐시에 값이 없으면 DB에서 직접 조회 후, Redis에 캐싱(30분 TTL)하여 저장한다.
	 *
	 * @return 인기 동출 모집글 리스트 (최대 5개)
	 */
	List<FishingTripPostResponse.HotPost> getHotPost();
}
