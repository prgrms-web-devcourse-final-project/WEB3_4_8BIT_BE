package com.backend.domain.fishingtrippost.service;

import org.springframework.stereotype.Service;

import com.backend.domain.captain.entity.Captain;
import com.backend.domain.fishingtrippost.converter.FishingTripPostConvert;
import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.fishingtrippost.entity.FishingTripPost;
import com.backend.domain.fishingtrippost.exception.FishingTripPostErrorCode;
import com.backend.domain.fishingtrippost.exception.FishingTripPostException;
import com.backend.domain.fishingtrippost.repository.FishingTripPostRepository;
import com.backend.domain.fishpoint.exception.FishPointErrorCode;
import com.backend.domain.fishpoint.exception.FishPointException;
import com.backend.domain.fishpoint.repository.FishPointRepository;
import com.backend.domain.member.exception.MemberErrorCode;
import com.backend.domain.member.exception.MemberException;
import com.backend.domain.member.repository.MemberRepository;
import com.backend.global.exception.GlobalException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FishingTripPostServiceImpl implements FishingTripPostService {

	private final FishingTripPostRepository fishingTripPostRepository;
	private final MemberRepository memberRepository;
	private final FishPointRepository fishPointRepository;

	@Override
	@Transactional
	public Long createFishingTripPost(final Long memberId, final FishingTripPostRequest.Form requestDto) {
		// 멤버, 낚시 포인트 존재 검증
		validMemberAndFishPoint(memberId, requestDto);

		FishingTripPost fishingTripPost = FishingTripPostConvert.fromFishingTripPostCreate(memberId, requestDto);

		return fishingTripPostRepository.save(fishingTripPost).getFishingTripPostId();
	}

	@Override
	@Transactional
	public Long updateFishingTripPost(
		final Long memberId,
		final Long fishTripPostId,
		final FishingTripPostRequest.Form requestDto
	) {

		FishingTripPost fishingTripPost = getFishingTripPostById(fishTripPostId);

		// 동출 모집 게시글 작성자인지 검증
		validAuthor(fishingTripPost, memberId);

		fishingTripPost.updateFishingTripPost(
			requestDto.subject(),
			requestDto.content(),
			requestDto.recruitmentCount(),
			requestDto.isShipFish(),
			requestDto.fishingDate(),
			requestDto.fishingPointId(),
			requestDto.fileIdList()
		);
		log.debug("[동출 모집 게시글 정보 수정] : {}", fishingTripPost);

		return fishingTripPost.getFishingPointId();
	}

	/**
	 * 동출 모집 게시글 ID로 동출 게시글 엔티티를 조회하는 메소드
	 *
	 * @param fishingTripPostId 동출 모집 게시글의 ID
	 * @return {@link Captain} 조회된 동출 모집 게시글 엔티티
	 * @throws FishingTripPostException 동출 모집 게시글이 존재하지 않는 경우 예외 발생
	 */

	private FishingTripPost getFishingTripPostById(final Long fishingTripPostId) {

		FishingTripPost fishingTripPost = fishingTripPostRepository.findById(fishingTripPostId)
			.orElseThrow(() -> new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_NOT_FOUND));
		log.debug("[동출 모집 게시글 조회] : {}", fishingTripPost);

		return fishingTripPost;
	}

	/**
	 * 로그인한 멤버와 낚시포인트 존재 검증 메서드
	 *
	 * @param memberId 검증할 회원 Id
	 * @throws MemberException    존재하지 않는 회원이면 예외 발생
	 * @throws FishPointException 존재하지 않는 낚시 포인트면 예외 발생
	 */

	private void validMemberAndFishPoint(final Long memberId, final FishingTripPostRequest.Form requestDto)
		throws GlobalException {

		if (!memberRepository.existsById(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		if (!fishPointRepository.existsById(requestDto.fishingPointId())) {
			throw new FishPointException(FishPointErrorCode.FISH_POINT_NOT_FOUND);
		}
	}

	/**
	 * 로그인한 멤버와 동출 모집 게시글 작성자 동일 검증 메서드
	 *
	 * @param memberId 검증할 회원 Id
	 * @throws FishingTripPostException 동출 모집 게시글을 작성한 멤버가 아니면 예외 발생
	 */

	private void validAuthor(final FishingTripPost post, final Long memberId) {
		if (!post.getMemberId().equals(memberId)) {
			throw new FishingTripPostException(FishingTripPostErrorCode.FISHING_TRIP_POST_UNAUTHORIZED_AUTHOR);
		}
	}

}
