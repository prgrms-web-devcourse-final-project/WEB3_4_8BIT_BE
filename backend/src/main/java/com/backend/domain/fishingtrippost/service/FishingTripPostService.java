package com.backend.domain.fishingtrippost.service;

import com.backend.domain.fishingtrippost.dto.request.FishingTripPostRequest;
import com.backend.domain.member.exception.MemberException;

public interface FishingTripPostService {

	/**
	 * 선장 저장 메소드
	 *
	 * @param memberId 동출 게시글 작성하는 멤버
	 * @param requestDto {@link FishingTripPostRequest.Create}
	 * @return {@link Long memberId} Long: 선장 memberId
	 * @throws MemberException 이미 추가 정보를 받았으면 예외 발생
	 * @implSpec 회원가입한 멤버와 선장 추가 정보를 파라미터로 받고 저장한다.
	 */
	Long createFishingTripPost(final Long memberId, final FishingTripPostRequest.Create requestDto);
}
