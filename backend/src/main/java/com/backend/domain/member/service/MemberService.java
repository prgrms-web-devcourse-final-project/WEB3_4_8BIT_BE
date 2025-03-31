package com.backend.domain.member.service;

import com.backend.domain.member.converter.MemberConverter;
import com.backend.domain.member.dto.MemberRequest;
import com.backend.domain.member.dto.MemberResponse;
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

	/**
	 * 회원 상세 정보 조회 메소드
	 *
	 * @param memberId 조회할 회원의 고유 ID
	 * @return {@link MemberResponse.Detail} 조회된 회원의 상세 응답 DTO
	 * @throws MemberException 회원이 존재하지 않는 경우 예외 발생
	 * @implSpec 회원 ID로 회원 엔티티를 조회한 후, {@link MemberConverter}를 통해 응답 DTO로 변환한다.
	 */
	MemberResponse.Detail getMemberDetail(final Long memberId);
}
