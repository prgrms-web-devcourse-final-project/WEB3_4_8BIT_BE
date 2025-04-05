package com.backend.domain.fishingtriprecruitment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishingtriprecruitment.converter.FishingTripRecruitmentConverter;
import com.backend.domain.fishingtriprecruitment.dto.request.FishingTripRecruitmentRequest;
import com.backend.domain.fishingtriprecruitment.entity.FishingTripRecruitment;
import com.backend.domain.fishingtriprecruitment.exception.FishingTripRecruitmentErrorCode;
import com.backend.domain.fishingtriprecruitment.exception.FishingTripRecruitmentException;
import com.backend.domain.fishingtriprecruitment.repository.FishingTripRecruitmentRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishingTripRecruitmentServiceImpl implements FishingTripRecruitmentService {

	private final FishingTripRecruitmentRepository fishingTripRecruitmentRepository;
	private final MemberRepository memberRepository;
	private final FishingTripPostRepository fishingTripPostRepository;

	@Override
	@Transactional
	public Long createFishingTripRecruitment(
		final Long memberId,
		final FishingTripRecruitmentRequest.Create requestDto
	) {
		// 회원 및 동출 모집 게시글 유효성 검사
		validMemberAndFishPoint(memberId, requestDto.fishingTripPostId());

		FishingTripRecruitment recruitment = FishingTripRecruitmentConverter.fromFishingTripRecruitmentCreate(
			memberId,
			requestDto
		);

		FishingTripRecruitment savedRecruitment = fishingTripRecruitmentRepository.save(recruitment);
		log.debug("[동출 모집 생성] : {}", savedRecruitment);

		return savedRecruitment.getFishingTripRecruitmentId();
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

}
