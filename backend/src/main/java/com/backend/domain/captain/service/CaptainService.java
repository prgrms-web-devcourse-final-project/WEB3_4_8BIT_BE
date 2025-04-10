package com.backend.domain.captain.service;

import com.backend.domain.captain.dto.Request.CaptainRequest;
import com.backend.domain.captain.dto.Response.CaptainResponse;
import com.backend.domain.captain.exception.CaptainException;
import com.backend.domain.member.exception.MemberException;
import com.backend.global.exception.GlobalException;

public interface CaptainService {

	/**
	 * 선장 저장 메소드
	 *
	 * @param memberId 회원가입후 선장을 선택한 멤버
	 * @param requestDto {@link CaptainRequest.Create}
	 * @return {@link Long memberId} Long: 선장 memberId
	 * @throws MemberException 이미 추가 정보를 받았으면 예외 발생
	 * @implSpec 회원가입한 멤버와 선장 추가 정보를 파라미터로 받고 저장한다.
	 * @author Janghyeonsuk
	 */
	Long createCaptain(final Long memberId, final CaptainRequest.Create requestDto);


	/**
	 * 선장 상세 조회 메소드
	 *
	 * @param captainId 선장 Id
	 * @return {@link CaptainResponse.Detail} 선장 상세정보 응답 데이터
	 * @throws CaptainException 선장이 존재하지 않는 경우 예외 발생
	 * @implSpec QueryDSL을 사용해 필요한 필드만 조회하여 {@link CaptainResponse.Detail} DTO로 직접 반환한다
	 * @author Janghyeonsuk
	 */
	CaptainResponse.Detail getCaptainDetail(final Long captainId);

	/**
	 * 선장 배 리스트 수정 메소드
	 *
	 * @param captainId 선장 Id
	 * @param requestDto 배 리스트 포함된 dto
	 * @return {@link Long} 업데이트 된 선장 Id
	 *@throws GlobalException 배 리스트가 비어있을 시 예외 발생
	 * @implSpec 새로운 배 리스트를 받고 선장 정보 업데이트
	 * @author Janghyeonsuk
	 */
	Long updateCaptainShipList(final Long captainId, final CaptainRequest.Update requestDto);
}
