package com.backend.domain.captain.service;

import com.backend.domain.captain.dto.Request.CaptainRequest;

public interface CaptainService {

	/**
	 * 선장 저장 메소드
	 *
	 * @param memberId 회원가입후 선장을 선택한 멤버
	 * @param requestDto {@link CaptainRequest.Create}
	 * @return {@link Long memberId} Long: 선장 memberId
	 * @implSpec 회원가입한 멤버와 선장 추가 정보를 파라미터로 받고 저장한다.
	 * @author Janghyeonsuk
	 */
	Long createCaptain(final Long memberId, final CaptainRequest.Create requestDto);
}
