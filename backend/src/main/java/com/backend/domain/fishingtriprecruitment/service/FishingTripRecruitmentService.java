package com.backend.domain.fishingtriprecruitment.service;

import com.backend.domain.fishingtrippost.exception.FishingTripPostException;

import java.util.List;

import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

public interface FishingTripRecruitmentService {

	/**
	 * 동출 게시글 저장 메소드
	 *
	 * @param memberId   동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripRecruitmentRequest.Create}
	 * @return {@link Long fishingTripRecruitmentId} Long: 동출 모집 신청 Id fishingTripRecruitmentId
	 * @implSpec 로그인한 멤버 Id와 동출 모집 신청에 필요한 정보를 받아 모집 신청 작성
	 */

	Long createFishingTripRecruitment(
		final Long memberId,
		final FishingTripRecruitmentRequest.Create requestDto
	);

	/**
	 * 낚시 동행 신청을 거절합니다.
	 *
	 * <p>이 메서드는 특정 낚시 모집글에 대해 들어온 개별 신청을 거절할 때 사용되며,
	 * 신청을 거절할 권한이 있는 작성자인지를 먼저 검증한 후, 해당 신청의 상태를 {@code REJECTED}로 변경합니다.</p>
	 *
	 * @implSpec
	 * <ol>
	 *     <li>해당 신청 {@code fishingTripRecruitmentId}를 통해 모집글 ID를 조회합니다.</li>
	 *     <li>모집글 작성자 ID와 현재 로그인한 사용자 ID {@code memberId}가 일치하는지 검증합니다.</li>
	 *     <li>검증 통과 시, 신청 상태를 {@code REJECTED}로 변경합니다.</li>
	 *     <li>신청이 존재하지 않거나 권한이 없을 경우, 관련 예외를 발생시킵니다.</li>
	 * </ol>
	 *
	 * @param memberId 현재 로그인한 사용자(모집글 작성자)의 ID
	 * @param fishingTripRecruitmentId 거절할 낚시 동행 신청의 고유 ID
	 * @throws FishingTripPostException 작성자가 아닐 경우 또는 모집글/신청이 존재하지 않을 경우 발생
	 */
	void refuseFishingTripRecruitment(final Long memberId, final Long fishingTripRecruitmentId);

	/**
	 * 동출 모집 신청자 목록을 페이징 방식으로 조회합니다.
	 *
	 * <p>해당 메서드는 모집글 작성자가 자신이 작성한 동출 모집글에 대해,
	 * 특정 신청 상태({@code PENDING}, {@code APPROVED}, {@code REJECTED})의 신청자 목록을 커서 기반으로 조회할 때 사용됩니다.
	 * 페이징 요청 정보는 {@link GlobalRequest.CursorRequest}로 전달됩니다.
	 *
	 * @implSpec
	 * <ol>
	 *     <li>먼저, 요청자의 {@code memberId}가 해당 모집글({@code fishingTripPostId})의 작성자인지 검증합니다.</li>
	 *     <li>검증 통과 후, {@link com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository}를 통해
	 *         신청자 목록을 커서 기반 페이징으로 조회합니다.</li>
	 *     <li>결과는 {@link ScrollResponse} 형식으로 반환되며, 정렬 기준은 기본적으로 ID 오름차순입니다.</li>
	 * </ol>
	 *
	 * @param memberId 로그인한 사용자 ID (모집글 작성자)
	 * @param cursorRequestDto 커서 기반 페이징 요청 정보 (정렬 필드, 방향, 기준값 등)
	 * @param fishingTripPostId 조회할 모집글 ID
	 * @param status 필터링할 모집 상태 (PENDING, APPROVED, REJECTED)
	 * @return 페이징된 신청자 상세 응답 목록
	 * @throws FishingTripPostException 작성자가 아닌 경우 또는 모집글이 존재하지 않는 경우
	 */
	ScrollResponse<FishingTripRecruitmentResponse.DetailPage> getDetailPageList(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishingTripPostId,
		final RecruitmentStatus status
	);
}
