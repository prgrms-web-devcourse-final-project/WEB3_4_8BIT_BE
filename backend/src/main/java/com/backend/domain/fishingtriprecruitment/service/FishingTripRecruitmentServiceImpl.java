package com.backend.domain.fishingtriprecruitment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fishingtrippost.domain.PostStatus;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.notifier.FishingTripPostNotifier;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtriprecruitment.converter.FishingTripRecruitmentConverter;
import com.backend.domain.fishingtriprecruitment.domain.RecruitmentStatus;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.dto.response.FishingTripRecruitmentResponse;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.domain.fishingtriprecruitment.exception.FishingTripRecruitmentErrorCode;
import com.backend.domain.fishingtriprecruitment.exception.FishingTripRecruitmentException;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.dto.request.GlobalRequest;
import com.backend.global.dto.response.ScrollResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishingTripRecruitmentServiceImpl implements FishingTripRecruitmentService {

	private final FishingTripRecruitmentRepository fishingTripRecruitmentRepository;
	private final MemberRepository memberRepository;
	private final FishingTripPostRepository fishingTripPostRepository;
	private final FishingTripPostNotifier fishingTripPostNotifier;

	@Override
	@Transactional
	public Long createFishingTripRecruitment(
		final Long memberId,
		final FishingTripRecruitmentRequest.Create requestDto
	) {
		// 회원 및 동출 모집 게시글 유효성 검사
		Long fishingTripPostId = requestDto.fishingTripPostId();

		validMemberAndFishPoint(memberId, requestDto.fishingTripPostId());
		if (isPostApplicant(memberId, fishingTripPostId)) {
			throw new FishingTripRecruitmentException(
				FishingTripRecruitmentErrorCode.FISHING_TRIP_RECRUITMENT_ALREADY_APPLIED);
		}

		if (isPostOwner(memberId, requestDto.fishingTripPostId())) {
			throw new FishingTripRecruitmentException(
				FishingTripRecruitmentErrorCode.FISHING_TRIP_RECRUITMENT_AUTHOR_DO_NOT_APPLIED);
		}

		FishingTripRecruitment recruitment = FishingTripRecruitmentConverter.fromFishingTripRecruitmentCreate(
			memberId,
			requestDto
		);

		FishingTripRecruitment savedRecruitment = fishingTripRecruitmentRepository.save(recruitment);
		log.debug("[동출 모집 생성] : {}", savedRecruitment);

		return savedRecruitment.getFishingTripRecruitmentId();
	}

	@Override
	@Transactional
	public void refuseFishingTripRecruitment(final Long memberId, final Long fishingTripRecruitmentId) {

		FishingTripRecruitment fishingTripRecruitment = getFishingTripRecruitmentById(fishingTripRecruitmentId);

		validateFishingTripPostOwner(memberId, fishingTripRecruitment.getFishingTripPostId());

		fishingTripRecruitment.setRecruitmentStatus(RecruitmentStatus.REJECTED);
	}

	@Override
	@Transactional(readOnly = true)
	public ScrollResponse<FishingTripRecruitmentResponse.DetailPage> getDetailPageList(
		final Long memberId,
		final GlobalRequest.CursorRequest cursorRequestDto,
		final Long fishingTripPostId,
		final RecruitmentStatus status) {

		validatePostOwnerOrApplicant(memberId, fishingTripPostId);

		ScrollResponse<FishingTripRecruitmentResponse.DetailPage> detailPageList = fishingTripRecruitmentRepository.findDetailPageByFishingTripPostIdAndStatus(
			cursorRequestDto, fishingTripPostId, status);

		log.debug("동출 신청 조회하였습니다.");

		return detailPageList;
	}

	@Override
	@Transactional
	public void acceptFishingTripRecruitment(final Long memberId, final Long fishingTripRecruitmentId) {

		FishingTripRecruitment fishingTripRecruitment = getFishingTripRecruitmentById(fishingTripRecruitmentId);

		FishingTripPost fishingTripPost = validateFishingTripPostOwner(memberId,
			fishingTripRecruitment.getFishingTripPostId());
		validateFishingTripPost(fishingTripPost);

		fishingTripRecruitment.setRecruitmentStatus(RecruitmentStatus.APPROVED);
		fishingTripPost.increaseCurrentCount(1);
		completedFishingTripPost(fishingTripPost);
		fishingTripPostNotifier.notifyMailIfCompleted(fishingTripPost);
	}

	/**
	 * 모집 인원이 정원에 도달했는지 확인하고, 도달한 경우 게시글 상태를 {@code COMPLETED}로 변경합니다.
	 *
	 * <p>현재 인원({@code currentCount})이 모집 정원({@code recruitmentCount}) 이상일 경우,
	 * 더 이상 모집할 수 없으므로 게시글 상태를 {@link PostStatus#COMPLETED}로 설정합니다.</p>
	 *
	 * @param fishingTripPost 상태를 검사할 낚시 동행 모집글 엔티티
	 */
	private static void completedFishingTripPost(final FishingTripPost fishingTripPost) {
		if (fishingTripPost.getCurrentCount() >= fishingTripPost.getRecruitmentCount())
			fishingTripPost.setPostStatus(PostStatus.COMPLETED);
	}

	/**
	 * 동출 모집글의 현재 인원이 모집 정원을 초과했는지 검증합니다.
	 *
	 * <p>모집글의 {@code currentCount}가 {@code recruitmentCount} 이상일 경우,
	 * 더 이상 신청을 승인할 수 없으므로 예외를 발생시킵니다.</p>
	 *
	 * @param fishingTripPost 검증할 동출 모집글 엔티티
	 *
	 * @throws FishingTripPostException 모집 정원이 초과된 경우 발생하며,
	 *         {@link com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode#FISHING_TRIP_POST_OVER_RECRUITMENT}
	 *         에러 코드를 포함합니다.
	 */
	private static void validateFishingTripPost(final FishingTripPost fishingTripPost) {
		if (fishingTripPost.getCurrentCount() >= fishingTripPost.getRecruitmentCount())
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_OVER_RECRUITMENT);
	}

	/**
	 * 낚시 동행 모집글의 작성자인지를 검증합니다.
	 *
	 * <p>현재 로그인한 사용자 ID({@code memberId})가 해당 모집글({@code fishingTripPostId})의 작성자인지 확인하여,
	 * 작성자가 아닌 경우 예외를 발생시킵니다.</p>
	 * @param memberId 현재 로그인한 사용자(검증 대상)의 ID
	 * @param fishingTripPostId 검증할 낚시 동행 모집글의 ID
	 * @throws FishingTripPostException 모집글이 없거나 작성자가 아닌 경우 예외 발생
	 */
	private FishingTripPost validateFishingTripPostOwner(final Long memberId, final Long fishingTripPostId) {
		FishingTripPost fishingTripPost = fishingTripPostRepository.findById(fishingTripPostId)
			.orElseThrow(() -> new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND));
		log.debug("[동출 모집 게시글 조회] : {}", fishingTripPost);

		if (!fishingTripPost.getMemberId().equals(memberId)) {
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR);
		}

		return fishingTripPost;
	}

	/**
	 * 동출 모집 게시글 ID로 게시글 엔티티를 조회하는 메소드
	 *
	 * @param fishingTripRecruitmentId 조회할 동출 모집 게시글의 ID
	 * @return {@link FishingTripRecruitment} 조회된 게시글 엔티티
	 * @throws FishingTripRecruitmentException 게시글이 존재하지 않는 경우 예외 발생
	 */
	private FishingTripRecruitment getFishingTripRecruitmentById(final Long fishingTripRecruitmentId) {

		FishingTripRecruitment fishingTripRecruitment = fishingTripRecruitmentRepository.findById(
				fishingTripRecruitmentId)
			.orElseThrow(() -> new FishingTripRecruitmentException(
				FishingTripRecruitmentErrorCode.FISHING_TRIP_RECRUITMENT_NOT_FOUND));
		log.debug("[동출 모집 게시글 조회] : {}", fishingTripRecruitment);

		return fishingTripRecruitment;
	}

	/**
	 * 로그인한 멤버 및 동출 모집 게시글 유효성 검사
	 *
	 * @param memberId          로그인한 회원 ID
	 * @param fishingTripPostId 요청된 게시글 ID
	 * @throws MemberException          존재하지 않는 회원일 경우
	 * @throws FishingTripPostException 존재하지 않는 게시글일 경우
	 */
	private void validMemberAndFishPoint(final Long memberId, final Long fishingTripPostId) {

		if (!memberRepository.existsById(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		if (!fishingTripPostRepository.existsById(fishingTripPostId)) {
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND);
		}
	}

	/**
	 * 주어진 사용자가 해당 동출 게시글의 작성자이거나 참여자인지 검증합니다.
	 * <p>작성자도 아니고 신청자도 아닌 경우 예외를 발생시킵니다.</p>
	 *
	 * @param memberId          현재 로그인한 사용자 ID
	 * @param fishingTripPostId 검증 대상 동출 게시글 ID
	 */
	private void validatePostOwnerOrApplicant(final Long memberId, final Long fishingTripPostId) {
		if (!isPostOwner(memberId, fishingTripPostId) && !isPostApplicant(memberId, fishingTripPostId)) {
			throw new FishingTripRecruitmentException(
				FishingTripRecruitmentErrorCode.FISHING_TRIP_RECRUITMENT_UNAUTHORIZED);
		}
	}

	/**
	 * 사용자가 특정 동출 모집 게시글의 작성자인지 확인합니다.
	 *
	 * <p>게시글 ID와 사용자 ID를 기준으로 작성자 여부를 판단합니다.</p>
	 *
	 * @param memberId 사용자의 고유 ID
	 * @param fishingTripPostId 동출 모집 게시글의 고유 ID
	 * @return true: 해당 게시글의 작성자인 경우, false: 그렇지 않은 경우
	 */
	private boolean isPostOwner(final Long memberId, final Long fishingTripPostId) {
		return fishingTripPostRepository.existFishingTripPostByMemberIdAndPostId(memberId, fishingTripPostId);
	}

	/**
	 * 사용자가 특정 동출 모집 게시글에 신청한 참여자인지 확인합니다.
	 *
	 * <p>게시글 ID와 사용자 ID를 기준으로 참여자 여부를 판단합니다.</p>
	 *
	 * @param memberId 사용자의 고유 ID
	 * @param fishingTripPostId 동출 모집 게시글의 고유 ID
	 * @return true: 해당 게시글에 신청한 사용자일 경우, false: 그렇지 않은 경우
	 */
	private boolean isPostApplicant(final Long memberId, final Long fishingTripPostId) {
		return fishingTripRecruitmentRepository.existsByFishingTripPostIdAndMemberId(fishingTripPostId, memberId);
	}

}
