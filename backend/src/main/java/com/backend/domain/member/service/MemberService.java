package com.backend.domain.member.service;

import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.exception.MemberException;

public interface MemberService {

	/**
	 * 회원 추가 정보 저장 메소드
	 *
	 * @param memberId 회원의 고유 ID
	 * @param requestDto {@link MemberRequest.Create} 회원 추가 정보 (닉네임, 프로필 이미지, 자기소개)
	 * @return {@link Long} memberId - 저장된 회원의 ID
	 * @throws MemberException 이미 추가 정보가 등록된 경우 예외 발생
	 * @implSpec 회원의 추가 정보를 최초 1회 저장하며, 이미 저장된 경우 예외를 던진다.
	 */
	Long saveAddInfo(final Long memberId, final MemberRequest.Create requestDto);
}
